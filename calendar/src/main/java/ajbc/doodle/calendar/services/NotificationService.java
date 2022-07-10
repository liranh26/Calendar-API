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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import ajbc.doodle.calendar.daos.interfaces.NotificationDao;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
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


	public boolean isSubscribed(SubscriptionEndpoint subscription) throws DaoException {
		return userDao.checkEndPointRegistration(subscription.getEndpoint());
	}

	public Subscription getSubscriptionForUser(Integer userId) throws DaoException {
		return userDao.getSubscriptionByUserId(userId);
	}
	


	public void addNotificationToDB(Notification notification, Event event) throws DaoException {
//		notification.setEvent(event);
		dao.addNotification(notification);
	}

	public Notification createDefaultNotification(Event event) throws DaoException {

		Notification not = new Notification(event.getEventOwnerId(), event.getEventId(), event.getTitle(), 0,
				ChronoUnit.SECONDS, 0);

		addNotificationToDB(not, event);
		return not;
	}

	public List<Notification> getAllNotifications() throws DaoException {
		return dao.getAllNotifications();
	}

	public void deleteAllNotifications() throws DaoException {
		dao.deleteAllNotifications();
	}

	public Notification getNotificationById(Integer id) throws DaoException {
		return dao.getNotification(id);
	}

	public void updateNotification(Notification notification) throws DaoException {
		dao.updateNotification(notification);

	}

	public void softDelete(Notification notification) throws DaoException {
		notification.setDiscontinued(1);
		dao.updateNotification(notification);
	}

	public void hardDelete(Notification notification) throws DaoException {
		dao.deleteNotification(notification);

	}

//	public void addListNotificationsToDB(List<Notification> notifications) {
//		// TODO Auto-generated method stub
//	}

}
