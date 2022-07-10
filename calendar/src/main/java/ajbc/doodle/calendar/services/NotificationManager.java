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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.services.threads.NotificationTask;

@Service
public class NotificationManager {

//	@Autowired
//	private NotificationService notificationService;

	Comparator<Notification> timeComparator = new Comparator<Notification>() {
		@Override
		public int compare(Notification not1, Notification not2) {
			LocalDateTime alertTime1 = not1.getEvent().getStartTime().minus(not1.getTimeToAlertBefore(),
					not1.getUnits());
			LocalDateTime alertTime2 = not2.getEvent().getStartTime().minus(not2.getTimeToAlertBefore(),
					not2.getUnits());
			return alertTime1.compareTo(alertTime2);
		}
	};

	private PriorityQueue<Notification> notifications = new PriorityQueue<Notification>(timeComparator);

	public void addNotification(Notification notification) {
		notification.getEvent().setGuests(null); // to avoid exception for now..

		List<Notification> notsToRun = new ArrayList<Notification>();

		// need to take in care the time before
		LocalDateTime alertTime = notification.getEvent().getStartTime().minus(notification.getTimeToAlertBefore(),
				notification.getUnits());

		// 3 options for new notification: smaller , equal, bigger
		// smaller update thread
		// equal add to poll thread
		// bigger add to queue

		Notification curr = notifications.peek();
		if (curr != null) {
			
			LocalDateTime currAlertTime = curr.getEvent().getStartTime().minus(curr.getTimeToAlertBefore(),
					curr.getUnits());

			Duration diffTime = Duration.between(alertTime, currAlertTime);
			long difference = diffTime.getSeconds();
			if (difference > 0) {
				// nothing to change
				notifications.add(notification);
			} else {
				// update - reschedule the thread

				run(notification);
			}

		}else { 
			notifications.add(notification);
			run(notification);
		}
		
		System.out.println("Number of notification in the list: " + notifications.size());

	}

	private final int NUM_THREADS = 1;
	private ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(NUM_THREADS);;
	private ScheduledFuture<?> scheduledFuture;

	public void run(Notification notification) {// TODO list of notifications or multiple calls?

		LocalDateTime nextRun = notification.getEvent().getStartTime().minus(notification.getTimeToAlertBefore(),
				notification.getUnits());
		System.out.println(nextRun);

		Duration duration = Duration.between(LocalDateTime.now(), nextRun);
		long delay = duration.getSeconds();

		// In case something went wrong and the event already occurred.
		if (delay < 0)
			delay = 0;

//		if (scheduledFuture != null && !scheduledFuture.isDone()) {
//			scheduledFuture.cancel(false);
//		}

		Callable<Notification> task = new NotificationTask(notification);

//		scheduledFuture = scheduledService.schedule( myCallable , delay, TimeUnit.SECONDS); // in real usage 
		scheduledFuture = scheduledService.schedule(task, 3, TimeUnit.SECONDS); // for testing

		if (scheduledFuture.isDone()) {
			System.out.println("Task done ! : " + scheduledFuture);
		}

	}

	/* simple runnable to test using executor service */
//	Runnable myRunnable = new Runnable() {
//
//		@Override
//		public void run() {
//			System.out.println("entered!");
//
//			Notification notification = notifications.peek();
//
//			PushMessage msg = new PushMessage("message: ", notification.toString());
//
//			try {
//				notificationService.sendPushMessageToUser(notification.getUserId(), msg);
//			} catch (JsonProcessingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//	};

	public void stop() {
		scheduledService.shutdown();
		try {
			scheduledService.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
//	            Logger.getLogger(MyTaskExecutor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

//	/* simple test to see connection to PushMessage api */
//	public void run() {
//
//		Notification notification = notifications.peek();
//
//		PushMessage msg = new PushMessage("message: ", notification.toString());
//
//		try {
//			notificationService.sendPushMessageToUser(notification.getUserId(), msg);
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
