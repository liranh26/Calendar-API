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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ajbc.doodle.calendar.Application;
import ajbc.doodle.calendar.ServerKeys;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;

@Service
public class UserService {

	@Autowired
	@Qualifier("htUserDao")
	private UserDao userDao;
	


	public void addUser(User user) throws DaoException {
		userDao.addUser(user);
	}

	public User getUserById(Integer userId) throws DaoException {
		return userDao.getUser(userId);	
	}

	public List<User> getAllUsers() throws DaoException {
		return userDao.getAllUsers();
	}

	public void deleteAllUsers() throws DaoException {
		userDao.deleteAllUsers();
	}

	public boolean emailExistInDB(String email) throws DaoException {
		System.out.println(userDao.doesEmailExist(email));
		return userDao.doesEmailExist(email);
	}

	public User getUserByEmail(String email) throws DaoException {
		return userDao.getUserByEmail(email);
	}

	//TODO refactor code
//	public List<User> getUsersForEvent(Integer eventId) throws DaoException {
//		List<User> users = new ArrayList<User>();
//		List<EventUser> eventsForUser = eventUserService.getEventsForUser(eventId);
//
//		eventsForUser.stream().forEach(e -> {
//			try {
//				users.add(getUserById(e.getUserId()));
//
//			} catch (DaoException e1) {
//				e1.printStackTrace();
//			}
//		});
//
//		return users;
//	}

	public void updateUser(User user) throws DaoException {
		
		userDao.updateUser(user);
	}

	public void deleteUser(Integer id) throws DaoException {
		userDao.deleteUser(id);
		
	}

	public void addUserSubscription(String email, Subscription subscription) throws DaoException {
		User user = getUserByEmail(email);
		//set subscription keys
		user.setEndpoint(subscription.getEndpoint());
		user.setExpirationTime(subscription.getExpirationTime());
		user.setP256dh(subscription.getKeys().getP256dh());
		user.setAuth(subscription.getKeys().getAuth());

		userDao.updateUser(user);
	}

	public void unsubscribeUser(String email, SubscriptionEndpoint subscription) throws DaoException {
		User user = getUserByEmail(email); 
		
		user.setEndpoint(null);
		user.setExpirationTime(null);
		user.setP256dh(null);
		user.setAuth(null);
		
		userDao.updateUser(user);
	}


}
