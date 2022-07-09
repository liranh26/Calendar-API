package ajbc.doodle.calendar.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.webpush.PushMessage;

@Service
public class NotificationManager {

	@Autowired
	private NotificationService notificationService;
	
	private final int NUM_THREADS = 3;
	private ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(NUM_THREADS);

	Comparator<Notification> timeComparator = new Comparator<Notification>() {
		@Override
		public int compare(Notification not1, Notification not2) {
			//sort by date time 
			return not1.getEvent().getStartTime().compareTo(not2.getEvent().getStartTime());
		}
	};
	
	private PriorityQueue<Notification> notifications = new PriorityQueue<Notification>(timeComparator);
	
	
	public void addNotification(Notification notification) {
		notification.getEvent().setGuests(null);
		notifications.add(notification);
//		System.out.println(notifications);  // TODO guests fires a exception ! need to fix event.guest
		
		run();
	} 
	
	
	public void run() {

		Notification notification = notifications.peek();
		
		PushMessage msg = new PushMessage("message: ", notification.toString());
		
		try {
			notificationService.sendPushMessageToUser(notification.getUserId(), msg);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
