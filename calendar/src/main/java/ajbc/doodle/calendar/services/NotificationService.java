package ajbc.doodle.calendar.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ajbc.doodle.calendar.Application;
import ajbc.doodle.calendar.ServerKeys;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.EventDao;
import ajbc.doodle.calendar.daos.interfaces.EventUserDao;
import ajbc.doodle.calendar.daos.interfaces.NotificationDao;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.EventUser;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
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

	public Subscription getSubscriptionForUser(Integer userId) throws DaoException {
		return userDao.getSubscriptionByUserId(userId);
	}
	
	/*** UPDATE ***/
	
	public void updateNotification(Notification notification) throws DaoException {
		EventUser eventUser = new EventUser(notification.getUserId() , notification.getEventId());
		eventUser = eventUserDao.getEventForUser(eventUser);
		notification.setEventUser(eventUser);
		dao.updateNotification(notification);
	}
	
	public void updateListNotifications(List<Notification> notifications) throws DaoException {
		for (Notification notification : notifications) 
			updateNotification(notification);
		
	}
	
	
	public Notification createNotification(Notification notification, Integer eventId, Integer userId) throws DaoException {

		EventUser eventUser = new EventUser(userId , eventId);
		eventUser = eventUserDao.getEventForUser(eventUser);
		Event event = eventDao.getEvent(eventId);
			
		notification.setEventUser(eventUser);
		notification.setAlertTime(event.getStartTime().minus(notification.getTimeToAlertBefore(),notification.getUnits()));

		addNotificationToDB(notification);
		
		eventUser.addNotifications(notification);

		eventUserDao.updateUserEvent(eventUser);
		
		return notification;
	}

	
	public void addNotificationToDB(Notification notification) throws DaoException {
		dao.addNotification(notification);
	}

	
	public Notification createDefaultNotification(Event event) throws DaoException {

		Notification not = new Notification(event.getTitle(), 0, ChronoUnit.SECONDS, 0);

		createNotification(not, event.getEventId(), event.getEventOwnerId());
		
		manager.addNotifications(getAllNotifications());
		
		return not;
	}

	public List<Notification> getAllNotifications() throws DaoException {
		return dao.getAllNotifications();
	}


	public Notification getNotificationById(Integer id) throws DaoException {
		return dao.getNotification(id);
	}

	/*** DELETE ***/
	
	public void softDelete(Notification notification) throws DaoException {
		notification.setDiscontinued(1);
		dao.updateNotification(notification);
	}

	public void hardDelete(Notification notification) throws DaoException {
		dao.deleteNotification(notification);

	}

	public List<Notification> getNotificationsByEvent(Integer eventId) throws DaoException {
		
		return dao.getNotificationsByEvent(eventId);
	}

	public List<Notification> addListNotificationsToDB(List<Notification> notifications) throws DaoException {

		for (Notification notification : notifications) 
			createNotification(notification, notification.getEventId(), notification.getUserId());
		
		return notifications;
	}

	public void softDeleteListNotification( List<Notification> notifications) throws DaoException {

		for (Notification notification : notifications) {
			notification = dao.getNotification(notification.getNotificationId());
			softDelete(notification);
		}
	}

	public void hardDeleteListNotification(List<Notification> notifications) throws DaoException {

		for (Notification notification : notifications) {
			notification = dao.getNotification(notification.getNotificationId());
			softDelete(notification);
		}
	}

}