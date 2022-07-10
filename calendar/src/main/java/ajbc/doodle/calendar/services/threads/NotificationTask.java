package ajbc.doodle.calendar.services.threads;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.services.NotificationService;

public class NotificationTask implements Callable<Notification> {
	
	@Autowired
	private NotificationService notificationService;

	Notification notification;
	
	public NotificationTask(Notification notification){
		this.notification = notification;
	}
	
	@Override
	public Notification call() throws Exception {
		
		System.out.println("entered!");
		
		PushMessage msg = new PushMessage("message: ", notification.toString());
		
		notificationService.sendPushMessageToUser(notification.getUserId(), msg);

		return notification;
	}

}
