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
import ajbc.doodle.calendar.daos.interfaces.EventDao;
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
	@Qualifier("htEventDao")
	EventDao eventDao;
	
	

	@Autowired
	PushMessageConfig msgConfig;
	
	public byte[] publicSigningKey() {
		return msgConfig.getServerKeys().getPublicKeyUncompressed();
	}
	
	
	Comparator<Notification> timeComparator = new Comparator<Notification>() {
		@Override
		public int compare(Notification not1, Notification not2) {
			return not1.getAlertTime().compareTo(not2.getAlertTime());
		}
	};

	
	private PriorityBlockingQueue<Notification> notifications = new PriorityBlockingQueue<Notification>(10, timeComparator);

	
	public void addNotification(Notification notification) throws DaoException {


	}

	

//	public void run(Notification notification) throws DaoException {// TODO list of notifications or multiple calls?
//
//		long delay = getDelayForNotification(notification);
//
//		// In case something went wrong and the event already occurred.
//		if (delay < 0)
//			delay = 0;
//
//
//
//	}

	
	
//	private long getDelayForNotification(Notification notification) {
//		
//		LocalDateTime nextRun = notification.getEvent().getStartTime().minus(notification.getTimeToAlertBefore(),
//				notification.getUnits());
//		System.out.println(nextRun);
//
//		Duration duration = Duration.between(LocalDateTime.now(), nextRun);
//		long delay = duration.getSeconds();
//		
//		return delay;
//	}
	
	
	
	
	
	
	
	
	

	
	
	
//	private final int NUM_THREADS = 1;
//	private ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(NUM_THREADS);;
//	private ScheduledFuture<Notification> scheduledFuture;
	
	
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

	
	
	
	
	
	
	
	
	
//	public void stop() {
//		scheduledService.shutdown();
//		try {
//			scheduledService.awaitTermination(5, TimeUnit.SECONDS);
//		} catch (InterruptedException ex) {
////	            Logger.getLogger(MyTaskExecutor.class.getName()).log(Level.SEVERE, null, ex);
//		}
//	}

	

}
