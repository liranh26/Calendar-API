package ajbc.doodle.calendar.services;

import java.time.Duration;
import java.time.LocalDateTime;
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

@Service
public class NotificationManager {

	@Autowired
	private NotificationService notificationService;

	Comparator<Notification> timeComparator = new Comparator<Notification>() {
		@Override
		public int compare(Notification not1, Notification not2) {
			return not1.getEvent().getStartTime().compareTo(not2.getEvent().getStartTime());
		}
	};

	private PriorityQueue<Notification> notifications = new PriorityQueue<Notification>(timeComparator);

	
	
	public void addNotification(Notification notification) {
		
		notification.getEvent().setGuests(null);
		notifications.add(notification);

		System.out.println("Number of notification in the list: " + notifications.size());

		//TODO check replace head - update the thread 
		openThreadForNotification(notifications.peek());
		
//		run();
	}

	
	
	final int NUM_THREADS = 2;
	private ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(NUM_THREADS);;
	private ScheduledFuture<?> scheduledFuture;

//    private volatile boolean isStopIssued; needed ?

	
	public void openThreadForNotification(Notification notification) {// TODO list of notifications or multiple calls?

		LocalDateTime nextRun = notification.getEvent().getStartTime().minus(notification.getTimeToAlertBefore(),
				notification.getUnits());
		System.out.println(nextRun);

		//TODO check a day in past  
		Duration duration = Duration.between(nextRun, LocalDateTime.now());
		long delay = duration.getSeconds();

		//In case comething went wrong and the event already occurred.
		if(delay < 0)
			delay = 0;
		
//		scheduledFuture = scheduledService.schedule( myCallable , delay, TimeUnit.SECONDS); // in real usage 
		scheduledFuture = scheduledService.schedule(myRunnable, 3, TimeUnit.SECONDS); // for testing
		


	}

	Runnable myRunnable = new Runnable() {


		@Override
		public void run() {
			System.out.println("entered!");
			
			Notification notification = notifications.peek();
			
			PushMessage msg = new PushMessage("message: ", notification.toString());

			 try {
				notificationService.sendPushMessageToUser(notification.getUserId(), msg);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	};

	
	public void stop() {
		scheduledService.shutdown();
		try {
			scheduledService.awaitTermination(1, TimeUnit.DAYS);
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
