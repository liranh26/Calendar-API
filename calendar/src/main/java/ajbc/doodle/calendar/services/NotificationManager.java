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
public class NotificationManager implements Runnable {


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
	
	private PriorityBlockingQueue<Notification> notifications = new PriorityBlockingQueue<Notification>(10, timeComparator);

	public byte[] publicSigningKey() {
		return msgConfig.getServerKeys().getPublicKeyUncompressed();
	}
	

	
	public void addNotification(Notification notification) throws DaoException {
		notifications.add(notification);
		
		run();
	
	}


	ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	@Override
	public void run() {
		
		Notification notification = notifications.poll();
		

		Duration duration = Duration.between(LocalDateTime.now(), notification.getAlertTime());
		long delay = duration.getSeconds();

		// In case something went wrong and the event already occurred.
		if (delay < 0)
			delay = 0;

		Subscription sub;
		
		try {
			
			sub = userDao.getSubscriptionByUserId(notification.getUserId());
			

			executorService.execute(new NotificationTask(notification, sub, msgConfig));
			
			
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
	}

	
	

}
