package ajbc.doodle.calendar.services;

import java.lang.Thread.State;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.webpush.PushMessageConfig;
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

	private final int INITIAL_SIZE = 10;
	private final int MILLI_SECOND = 1000;
	private Thread managerThread = new Thread();
	private PriorityBlockingQueue<Notification> notificationsQueue = new PriorityBlockingQueue<Notification>(
			INITIAL_SIZE, timeComparator);

	private ExecutorService executorService = Executors.newCachedThreadPool();

	public void deleteNotificationQueue(Notification notToDelete) {
		if (managerThread.isAlive())
			managerThread.interrupt();

		for (Notification notification : notificationsQueue)
			if (notification.getNotificationId() == notToDelete.getNotificationId()) {
				notificationsQueue.remove(notification);
				System.out.println("Notification removed! : " + notification);
				break;
			}

		startThreadManager();

	}

	public void deleteListNotificationInQueue(List<Notification> notsToUpdate) {
		notsToUpdate.stream().forEach(n -> deleteNotificationQueue(n));
	}

	public void updateListNotificationInQueue(List<Notification> notsToUpdate) {
		notsToUpdate.stream().forEach(n -> updateNotificationQueue(n));
	}

	public void updateNotificationQueue(Notification notToUpdate) {
		if (managerThread.isAlive())
			managerThread.interrupt();

		for (Notification notification : notificationsQueue)
			if (notification.getNotificationId() == notToUpdate.getNotificationId()) {
				notificationsQueue.remove(notification);
				notificationsQueue.add(notToUpdate);
				break;
			}

		startThreadManager();
	}

	public void addNotification(Notification notification) throws DaoException {

		if (managerThread.isAlive())
			managerThread.interrupt();

		if (notification.getDiscontinued() == 0)
			this.notificationsQueue.add(notification);

		startThreadManager();

	}

	// TODO Notification...
	public void addNotifications(List<Notification> notifications) throws DaoException {

		System.out.println(managerThread.getState());

		if (managerThread.isAlive())
			managerThread.interrupt();

		for (Notification notification : notifications)
			if (notification.getDiscontinued() == 0)
				this.notificationsQueue.add(notification);

		startThreadManager();

	}

	private void startThreadManager() {

		managerThread = new Thread(() -> {
			try {
				buildThread();
			} catch (DaoException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		managerThread.start();

	}

	@Transactional
	private void buildThread() throws DaoException, InterruptedException {

		System.out.println("size  :  " + notificationsQueue.size());

		while (!notificationsQueue.isEmpty()) {

			Notification head, polledNotification;

			try {
				// 
				head = notificationsQueue.peek();
				Long delay = getDelayTime(head);

				System.out.println("next notification: " + head);
				System.out.println("sleep for " + delay);
				if (delay > 0)
					Thread.sleep(delay * MILLI_SECOND);
			} catch (InterruptedException e) {
				System.out.println("interrupted");
				break;
			}

			User user = managerService.getUser(head); //TODO name

			// poll here in case user not logged infinity loop
			polledNotification = notificationsQueue.poll();

			if (managerService.isUserLogged(user)) { 
				executorService.execute(new NotificationTask(polledNotification, user, msgConfig));
			} else
				System.out.println("User not logged!");

			managerService.setNotificationsInactive(polledNotification);

		}

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
