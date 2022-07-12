package ajbc.doodle.calendar.services;

import java.lang.Thread.State;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.entities.webpush.PushMessageConfig;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;
import ajbc.doodle.calendar.services.threads.NotificationTask;

@Service
public class NotificationManager {

	@Autowired
	PushMessageConfig msgConfig;

	@Autowired
	private NotificationManagerService managerService;

	Comparator<Notification> timeComparator = new Comparator<Notification>() {
		@Override
		public int compare(Notification not1, Notification not2) {
			return not1.getAlertTime().compareTo(not2.getAlertTime());
		}
	};

	private final int MILLI_SECOND = 1000;
	private Thread managerThread = new Thread();
	private Notification currNotification;
	private PriorityBlockingQueue<Notification> notifications = new PriorityBlockingQueue<Notification>(10,
			timeComparator);

	
	public void addNotifications(List<Notification> notifications) throws DaoException {

		System.out.println(managerThread.getState());
		
		if(managerThread.getState() == State.WAITING)
			managerThread.interrupt();
		

		for (Notification notification : notifications) 
			if(notification.getDiscontinued() == 0)
				this.notifications.add(notification);
		

		initManager();

	}

	public void initManager() throws DaoException {
		if (managerService.isUserLogged(notifications.peek()))
			startThreadManager();
	}

	
	private void startThreadManager() {
		managerThread = new Thread(() -> {
			System.out.println("hi");
			try {
				buildThread();
			} catch (DaoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		managerThread.start();
	}

	private void buildThread() throws DaoException, InterruptedException {

		while (!notifications.isEmpty()) {

			List<Notification> nots = getClosestNotifcations();

			currNotification = nots.get(0);

			List<User> usersId = getUsers(nots);

			List<Subscription> subs = getSubscriptionsByUsersId(usersId);

			Runnable task = new NotificationTask(nots, subs, msgConfig);

			long delay = getDelayTime(currNotification);

			System.out.println("delay :   " + delay);

			Thread.sleep(delay * MILLI_SECOND);

			task.run();
		}

	}

	private List<User> getUsers(List<Notification> nots) throws DaoException {
		List<User> usersId = new ArrayList<User>();
		for (Notification notification : nots)
			usersId.add(managerService.getUser(notification));
		return usersId;
	}

	private List<Subscription> getSubscriptionsByUsersId(List<User> users) throws DaoException {
		List<Subscription> subs = new ArrayList<Subscription>();
		for (User user : users)
			subs.add(managerService.getSubscriptionByUserId(user.getUserId()));
		return subs;
	}

	private List<Notification> getClosestNotifcations() throws DaoException {
		List<Notification> nots = new ArrayList<Notification>();
		Notification tmp = notifications.poll();
		nots.add(tmp);

		while (notifications.size() > 0 && tmp.getAlertTime().equals(notifications.peek().getAlertTime())) {
			Notification not = notifications.poll();
			nots.add(not);
			managerService.setNotificationInactive(not);
		}

		return nots;
	}

	private long getDelayTime(Notification notification) {

		Duration duration = Duration.between(LocalDateTime.now(), notification.getAlertTime());
		long delay = duration.getSeconds();

		// In case something went wrong and the event already occurred.
		if (delay < 0)
			delay = 0;

		return delay;
	}

}
