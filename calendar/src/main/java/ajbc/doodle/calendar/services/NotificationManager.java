package ajbc.doodle.calendar.services;

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
	@Qualifier("htUserDao")
	private UserDao userDao;

	Comparator<Notification> timeComparator = new Comparator<Notification>() {
		@Override
		public int compare(Notification not1, Notification not2) {
			return not1.getAlertTime().compareTo(not2.getAlertTime());
		}
	};

	private final int MILLI_SECOND=1000;
	private Thread managerThread;
	private Notification currNotification;
	private PriorityBlockingQueue<Notification> notifications = new PriorityBlockingQueue<Notification>(10,
			timeComparator);


	public void addNotification(Notification notification) throws DaoException {

		insertNotifications(notification);

		initManager();

	}
	
	private void insertNotifications(Notification notification) {
			if(notification.getDiscontinued() == 0)
				this.notifications.add(notification);
			
			if(notifications.isEmpty())
				currNotification = notification;
	}

	public void initManager() {

		if (0 > currNotification.getAlertTime().compareTo(notifications.peek().getAlertTime())) {
			managerThread.interrupt();

			startThreadManager();
		}

	}

	private void startThreadManager() {
		managerThread = new Thread(() -> {
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
		
		List<Notification> nots = getClosestNotifcations();

		currNotification = nots.get(0);
		
		List<Integer> usersId = getUsersId(nots);

		List<Subscription> subs = getSubscriptionsByUsersId(usersId);

		Runnable task = new NotificationTask(nots, subs, msgConfig);

		task.run();
		
		long delay = getDelayTime(currNotification);
		
		Thread.sleep(delay*MILLI_SECOND);
	}

	private List<Integer> getUsersId(List<Notification> nots) throws DaoException {
		List<Integer> usersId = new ArrayList<Integer>();
		for (Notification notification : nots)
			usersId.add(userDao.getUser(notification.getUserId()).getUserId());
		return usersId;
	}

	private List<Subscription> getSubscriptionsByUsersId(List<Integer> usersId) throws DaoException {
		List<Subscription> subs = new ArrayList<Subscription>();
		for (Integer userId : usersId)
			subs.add(userDao.getSubscriptionByUserId(userId));
		return subs;
	}

	private List<Notification> getClosestNotifcations() {
		List<Notification> nots = new ArrayList<Notification>();
		Notification tmp = notifications.poll();
		nots.add(tmp);
		
		while (tmp.getAlertTime().equals(notifications.peek().getAlertTime()))
			nots.add(notifications.poll());

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
