package ajbc.doodle.calendar.services;

import java.lang.Thread.State;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.webpush.PushMessageConfig;
import ajbc.doodle.calendar.entities.webpush.Subscription;
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

	private final int INITIAL_SIZE=10;
	private final int MILLI_SECOND = 1000;
	private Thread managerThread = new Thread();
	private PriorityBlockingQueue<Notification> notifications = new PriorityBlockingQueue<Notification>(INITIAL_SIZE,
			timeComparator);

	
	public void addNotifications(List<Notification> allNotifications) throws DaoException {

		System.out.println(managerThread.getState());
		
		if(managerThread.getState() == State.WAITING) 
			managerThread.interrupt();
		
		this.notifications = new PriorityBlockingQueue<Notification>(10, timeComparator);

		for (Notification notification : allNotifications) 
			if(notification.getDiscontinued() == 0)
				this.notifications.add(notification);
		

		initManager();

	}

	public void initManager() throws DaoException {
//		if (managerService.isUserLogged(notifications.peek()))
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

	@Transactional
	private void buildThread() throws DaoException, InterruptedException {

		System.out.println("size  :  " + notifications.size());
		
		while (!notifications.isEmpty()) {

			List<Notification> nots = getClosestNotifcations();

			List<User> usersId = getUsers(nots);

			List<Subscription> subs = getSubscriptionsByUsersId(usersId);

			Runnable task = new NotificationTask(nots, subs, msgConfig);

			long delay = getDelayTime(nots.get(0));

			System.out.println("delay :   " + delay);

			Thread.sleep(delay * MILLI_SECOND);

			if (managerService.isUserLogged(nots.get(0))) {
				task.run();
				managerService.setNotificationsInactive(nots);				
			}
			
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
	

	
	public void updateNotificationQueue(Notification notToUpdate) throws DaoException {
		for (Notification notification : notifications) 
			if(notification.getNotificationId() == notToUpdate.getNotificationId()) {
				System.out.println("step 1111 "+notification);
				notifications.remove(notification);
				System.out.println("step 2222 "+ notToUpdate);
				notifications.add(notToUpdate);
			}
		if(managerThread.getState() == State.WAITING) 
			managerThread.interrupt();
		
		initManager();
	}
	

	
	public void updateOrDeleteListNotifications(List<Notification> notifications) throws DaoException {
		addNotifications(notifications);
	}
	
	public void deleteNotificationQueue(Notification notToDelete) throws DaoException {
		for (Notification notification : notifications) 
			if(notification.getNotificationId() == notToDelete.getNotificationId())
				notifications.remove(notToDelete);
		
		if(managerThread.getState() == State.WAITING) 
			managerThread.interrupt();
		
		initManager();
		
	}
	
}
