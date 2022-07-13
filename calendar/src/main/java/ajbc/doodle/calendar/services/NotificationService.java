package ajbc.doodle.calendar.services;

import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.EventDao;
import ajbc.doodle.calendar.daos.interfaces.EventUserDao;
import ajbc.doodle.calendar.daos.interfaces.NotificationDao;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.EventUser;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.webpush.PushMessageConfig;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;

@Service
public class NotificationService {

	@Autowired
	@Qualifier("htNotificationDao")
	NotificationDao dao;

	@Autowired
	@Qualifier("htUserDao")
	UserDao userDao;
	
	@Autowired
	@Qualifier("htEventUserDao")
	EventUserDao eventUserDao;

	@Autowired
	@Qualifier("htEventDao")
	EventDao eventDao;
	
	@Autowired
	PushMessageConfig msgConfig;

	@Autowired
	private NotificationManager manager;

	public byte[] publicSigningKey() {
		return msgConfig.getServerKeys().getPublicKeyUncompressed();
	}
	
	public boolean isSubscribed(SubscriptionEndpoint subscription) throws DaoException {
		return userDao.checkEndPointRegistration(subscription.getEndpoint());
	}
	
	public List<Notification> getAllNotifications() throws DaoException {
		return dao.getAllNotifications();
	}


	public Notification getNotificationById(Integer id) throws DaoException {
		return dao.getNotification(id);
	}

	public Subscription getSubscriptionForUser(Integer userId) throws DaoException {
		return userDao.getSubscriptionByUserId(userId);
	}
	
	public List<Notification> getNotificationsByEvent(Integer eventId) throws DaoException {
		return dao.getNotificationsByEvent(eventId);
	}
	
	/*** UPDATE ***/
	
	public void updateNotification(Notification notification) throws DaoException {
		EventUser eventUser = new EventUser(notification.getUserId() , notification.getEventId());
		eventUser = eventUserDao.getEventForUser(eventUser);
		notification.setEventUser(eventUser);
		setAlertTime(notification, eventDao.getEvent(notification.getEventId()));
		dao.updateNotification(notification);
		manager.updateNotificationAndInitiateThread(notification); //add 1 notification
	}
	
	public void updateListNotifications(List<Notification> notifications) throws DaoException {
		for (Notification notification : notifications) {
			EventUser eventUser = new EventUser(notification.getUserId() , notification.getEventId());
			eventUser = eventUserDao.getEventForUser(eventUser);
			notification.setEventUser(eventUser);
			setAlertTime(notification, eventDao.getEvent(notification.getEventId()));
			dao.updateNotification(notification);
		}
		manager.addNotifications(notifications); //add list of notifications
	}
	

	//TODO change name
	public Notification createNotification(Notification notification, Integer eventId, Integer userId) throws DaoException {

		EventUser eventUser = new EventUser(userId , eventId);
		eventUser = eventUserDao.getEventForUser(eventUser);
		Event event = eventDao.getEvent(eventId);
			
		notification.setEventUser(eventUser);

		setAlertTime(notification, event);
		
		addNotificationToDB(notification); 

		eventUser.addNotifications(notification);

		eventUserDao.updateUserEvent(eventUser);
		
		manager.addNotificationAndInitiateThread(notification);
		return notification;
	}

	
	private void setAlertTime(Notification notification, Event event) {
		notification.setAlertTime(event.getStartTime().minus(notification.getTimeToAlertBefore(),notification.getUnits()));
	}
	
	
	public void addNotificationToDB(Notification notification) throws DaoException {
		dao.addNotification(notification);
	}

	public List<Notification> addListNotificationsToDB(List<Notification> notifications) throws DaoException {

		for (Notification notification : notifications) 
			createNotification(notification, notification.getEventId(), notification.getUserId());
		
		return notifications;
	}
	
	public Notification createDefaultNotification(Event event) throws DaoException {

		Notification not = new Notification(event.getTitle(), 0, ChronoUnit.SECONDS);

		createNotification(not, event.getEventId(), event.getEventOwnerId());
		
		return not;
	}


	
	/*** DELETE ***/
	
	public void softDelete(Notification notification) throws DaoException {
		notification.setDiscontinued(true);
		dao.updateNotification(notification);
		manager.deleteNotificationAndInitiateThread(notification);
	}

	
	public void hardDeleteNotification(Notification notification) throws DaoException {
		dao.deleteNotification(notification);
		manager.deleteNotificationAndInitiateThread(notification);
	}

	
	public void softDeleteListNotification(List<Notification> notifications) throws DaoException {

		for (Notification notification : notifications) {
			notification = dao.getNotification(notification.getNotificationId());
			notification.setDiscontinued(true);
			dao.updateNotification(notification);
		}
		
		manager.deleteListNotificationInQueue(notifications);
	}

	
	public void hardDeleteListNotification(List<Notification> notifications) throws DaoException {

		for (Notification notification : notifications) {
			notification = dao.getNotification(notification.getNotificationId());
			dao.deleteNotification(notification);
		}
		
		manager.deleteListNotificationInQueue(notifications);
	}


}