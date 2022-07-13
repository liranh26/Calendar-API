package ajbc.doodle.calendar.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;

@Service
public class UserService {

	@Autowired
	@Qualifier("htUserDao")
	private UserDao userDao;
	
	@Autowired
	@Qualifier("htEventDao")
	EventDao eventDao;
	
	@Autowired
	@Qualifier("htEventUserDao")
	EventUserDao eventUserdao;

	@Autowired
	@Qualifier("htNotificationDao")
	NotificationDao notificationDao;
	
	public void updateListOfUsers(List<User> users) throws DaoException {
		for (User user : users) 
			updateUser(user);
		
	}
	
	public void updateUser(User user) throws DaoException {
		List<EventUser> eventUsers = eventUserdao.getEventsByUserId(user.getUserId());
		
		Set<Event> events = new HashSet<Event>();
		for (EventUser eventUser : eventUsers) {
			events.add(eventDao.getEvent(eventUser.getEventId()));
		}
		
		user.setEvents(events);
		userDao.updateUser(user);
	}
	
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


	public List<User> getUsersByEventId(Integer eventId) throws DaoException {
		List<User> users = new ArrayList<User>();
		List<EventUser> eventsForUser = eventUserdao.getUsersByEventId(eventId);
		for (EventUser eventUser : eventsForUser) 
			users.add(getUserById(eventUser.getUserId()));

		return users;
	}	

	
	public List<User> getUsersWithEventsInRange(LocalDateTime start, LocalDateTime end) throws DaoException {
		Set<User> users = new HashSet<User>();
		
		List<Event> events = eventDao.getAllEvents().stream()
				.filter(e -> e.getStartTime().isAfter(start) && e.getEndTime().isBefore(end)).toList();
	
		
		events.stream().forEach(e -> users.addAll(e.getGuests()));
		
		
		for (Event event : events) {
			 List<EventUser> eventUsers = eventUserdao.getUsersByEventId(event.getEventId());
			 for (EventUser eventUser : eventUsers) {
				users.add(userDao.getUser(eventUser.getUserId()));
			}
		}

		return users.stream().toList();
	}
	


	public void addUserSubscription(String email, Subscription subscription) throws DaoException {
		User user = getUserByEmail(email);
		//set subscription keys
		user.setEndpoint(subscription.getEndpoint());
		user.setP256dh(subscription.getKeys().getP256dh());
		user.setAuth(subscription.getKeys().getAuth());

		userDao.updateUser(user);
	}

	public void unsubscribeUser(String email, SubscriptionEndpoint subscription) throws DaoException {
		User user = getUserByEmail(email); 
		
		user.setEndpoint(null);
		user.setP256dh(null);
		user.setAuth(null);
		
		userDao.updateUser(user);
	}

	public void addListOfUsers(List<User> users) throws DaoException {
		for (User user : users) 
			addUser(user);	
	}

	
	/*** DELETE ***/
	
	public void softDeleteUser(User user) throws DaoException {
		user.setDiscontinued(true);
		updateUser(user);
	}

	public void hardDeleteUser(User user) throws DaoException {
		
		hardDeleteUserNotifications(user);
		
		hardDeleteEventUserOfUser(user);
		
		hardDeleteEventsOfUser(user);
		
		userDao.deleteUser(user);
	}

	private void hardDeleteEventsOfUser(User user) throws DaoException {
		List<Event> events = eventDao.getEventsForUser(user.getUserId());
		
		for (Event event : events) {
			event.setOwner(user);
			List<EventUser> eventUsers = eventUserdao.getUsersByEventId(event.getEventId());
			for (EventUser eventUser : eventUsers) 
				eventUserdao.deleteUserEvent(eventUser);
			eventDao.deleteEvent(event);
		}
		
	}

	private void hardDeleteEventUserOfUser(User user) throws DaoException {
		List<EventUser> eventUsers = eventUserdao.getEventsByUserId(user.getUserId());
		
		for (EventUser eventUser : eventUsers) 
			eventUserdao.deleteUserEvent(eventUser);
	}

	private void hardDeleteUserNotifications(User user) throws DaoException {
		List<Notification> userNotifications = notificationDao.getNotificationsByUser(user.getUserId());
		for (Notification notification : userNotifications) 
			notificationDao.deleteNotification(notification);
		
	}




}
