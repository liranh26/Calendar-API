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
import lombok.NoArgsConstructor;

@NoArgsConstructor
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

	protected final int INITIAL_SIZE = 20;
	protected Thread managerThread = new Thread();
	protected PriorityBlockingQueue<Notification> notificationsQueue = new PriorityBlockingQueue<Notification>(
			INITIAL_SIZE, timeComparator);

	protected ExecutorService executorService = Executors.newCachedThreadPool();

	public void deleteNotificationAndInitiateThread(Notification notToDelete) {	
		deleteNotificationQueue(notToDelete);
		initiateThreadManager();
	}
	
	public void deleteListNotificationInQueue(List<Notification> notsToUpdate) {
		notsToUpdate.stream().forEach(n -> deleteNotificationQueue(n));
		initiateThreadManager();
	}
	
	protected void deleteNotificationQueue(Notification notToDelete) {
		for (Notification notification : notificationsQueue)
			if (notification.getNotificationId() == notToDelete.getNotificationId()) {
				notificationsQueue.remove(notification);
				System.out.println("Notification removed! : " + notification);
				break;
			}
	}


	public void updateListNotificationInQueue(List<Notification> notsToUpdate) {
		notsToUpdate.stream().forEach(n -> updateNotificationInQueue(n));
		initiateThreadManager();
	}

	public void updateNotificationAndInitiateThread(Notification notToUpdate) {
		updateNotificationInQueue(notToUpdate);
		initiateThreadManager();
	}

	protected void updateNotificationInQueue(Notification notToUpdate) {
		for (Notification notification : notificationsQueue)
			if (notification.getNotificationId() == notToUpdate.getNotificationId()) {
				notificationsQueue.remove(notification);
				notificationsQueue.add(notToUpdate);
				break;
			}
	}
	
	public void addNotificationAndInitiateThread(Notification notification) throws DaoException {
		insertNotificationToQueue(notification);
		initiateThreadManager();
	}

	protected void insertNotificationToQueue(Notification notification) {
		if (!notification.isDiscontinued())
			this.notificationsQueue.add(notification);
	}
	
	public void addNotifications(List<Notification> notifications) throws DaoException {		
		notifications.stream().forEach(n -> insertNotificationToQueue(n));
		initiateThreadManager();
	}
	
	protected void initiateThreadManager() {
		if (managerThread.isAlive())
			managerThread.interrupt();
		
		startThreadManager();
	}

	// TODO Notification...

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

			Notification queueHead;

			//
			queueHead = notificationsQueue.peek();
			Long delay = getDelayTime(queueHead);

			System.out.println("next notification: " + queueHead);
			System.out.println("sleep for " + delay);

			if (delay > 0)
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					System.out.println("Interupted");
					break;
				}

			User user = managerService.getUserByNotification(queueHead); // TODO name

			// poll here in case user not logged infinity loop
			queueHead = notificationsQueue.poll();

			if (managerService.isUserLogged(user)) {
				executorService.execute(new NotificationTask(queueHead, user, msgConfig));
			} else
				System.out.println("User not logged!"); // TODO remove before end

			managerService.setNotificationsInactive(queueHead);

		}

	}

	protected long getDelayTime(Notification notification) {

		Duration duration = Duration.between(LocalDateTime.now(), notification.getAlertTime());
		long delay = duration.getSeconds();

		// In case something went wrong and the event already occurred.
		if (delay < 0)
			delay = 0;

		return delay;
	}

}
