package ajbc.doodle.calendar.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.EventDao;
import ajbc.doodle.calendar.daos.interfaces.EventUserDao;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.EventUser;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;

@Service
public class EventService {

	@Autowired
	@Qualifier("htEventDao")
	EventDao eventDao;

	@Autowired
	@Qualifier("htEventUserDao")
	EventUserDao eventUserDao;

	@Autowired
	@Qualifier("htUserDao")
	UserDao userDao;
	
	@Autowired
	private NotificationManager manager;

	@Autowired
	private NotificationService notificationService;

	
	// CREATE
	public Event createEventForUser(Integer userId, Event event) throws DaoException {

		User user = userDao.getUser(userId);
		
		//setting the owner to the event
		event.setEventOwnerId(user.getUserId());
		event.setOwner(user);

		eventDao.addEvent(event);
		
		//update event by updating the guest (user) after adding him to event 
		event = getEventById(event.getEventId());
		event.addGuests(user);
		userDao.updateUser(user);
		
		//creates default notification in the moment of the event
		notificationService.createDefaultNotification(event);
		
		return event;
	}

	public List<Event> createListEventsToUser(Integer userId, List<Event> events) throws DaoException {
		for (Event event : events)
			createEventForUser(userId, event);

		return events;
	}

	
	
	// GET 
	public List<Event> getAllEvents() throws DaoException {
		return eventDao.getAllEvents();
	}

	public List<Event> getFutureEventsForUser(Integer id) throws DaoException {
		List<Event> events = getEventsByUserId(id);
		return events.stream().filter(e -> e.getStartTime().isAfter(LocalDateTime.now())).toList();
	}

	public List<Event> getEventsOfUserInRange(LocalDateTime start, LocalDateTime end, Integer id) throws DaoException {
		List<Event> events = getEventsByUserId(id);
		return events.stream().filter(e -> e.getStartTime().isAfter(start) && e.getEndTime().isBefore(end)).toList();
	}

	public List<Event> getUserEventsByFollowingTime(Integer id, Integer hours, Integer minutes) throws DaoException {
		return getEventsOfUserInRange(LocalDateTime.now(), LocalDateTime.now().plusHours(hours).plusMinutes(minutes),
				id);
	}

	public List<Event> getEventsByRange(LocalDateTime start, LocalDateTime end) throws DaoException {
		List<Event> events = getAllEvents();
		return events.stream().filter(e -> e.getStartTime().isAfter(start) && e.getEndTime().isBefore(end)).toList();
	}

	public Event getEventById(Integer eventId) throws DaoException {
		return eventDao.getEvent(eventId);
	}

	public List<Event> getEventsByUserId(Integer id) throws DaoException {
		List<Event> events = new ArrayList<Event>();
		List<EventUser> eventUser = eventUserDao.getEventsByUserId(id);

		// get the events for user from the connecting table 'EVENT_USERS'
		for (EventUser tmpEventUser : eventUser)
			events.add(getEventById(tmpEventUser.getEventId()));

		return events;
	}

	public Event addGuestsToEvent(Event event, List<Integer> usersIds) throws DaoException {
		for (Integer userId : usersIds) {
			User user = userDao.getUser(userId);
			event.addGuests(user);
			userDao.updateUser(user);
		}
		
		return event;
	}

	public List<Integer> getUsersIdsToList(List<User> users){
		List<Integer> ids = new ArrayList<Integer>();
		users.stream().forEach(u -> ids.add(u.getUserId()));
		return ids;
	}
	
	
	// UPDATE 
	
	public void updateListOfEvents(List<Event> events) throws DaoException {
		for (Event event : events)
			updateEvent(event);

	}

	public void updateEvent(Event event) throws DaoException {

		User user = userDao.getUser(event.getEventOwnerId());
		event.setOwner(user);

		eventDao.updateEvent(event);
	}

	// DELETE 
	
	public void softDeleteListOfEvents(List<Event> events) throws DaoException {
		for (Event event : events) {
			User user = userDao.getUser(event.getEventOwnerId());
			event.setOwner(user);
			softDeleteEvent(event);
		}

	}

	public void softDeleteEvent(Event event) throws DaoException {

		event.setDiscontinued(true);
		eventDao.updateEvent(event);

		// remove the notifications of the event from the Push Message Queue
		List<Notification> nots = notificationService.getNotificationsByEvent(event.getEventId());
		manager.deleteListNotificationInQueue(nots);
	
	}

	public void hardDeleteListOfEvents(List<Event> events) throws DaoException {
		for (Event event : events)
			hardDeleteEvent(event);
	}

	public void hardDeleteEvent(Event event) throws DaoException {
		User user = userDao.getUser(event.getEventOwnerId());
		event.setOwner(user);

		deleteEventNotifications(event);

		deleteUsersFromEvent(event);

		eventDao.deleteEvent(event);

	}

	private void deleteEventNotifications(Event event) throws DaoException {
		List<Notification> nots = notificationService.getNotificationsByEvent(event.getEventId());
		
		for (Notification notification : nots)
			//delete from DB & from the Push Messages Queue 
			notificationService.hardDeleteNotification(notification);
	}

	private void deleteUsersFromEvent(Event event) throws DaoException {
		List<EventUser> users = eventUserDao
				.getAllUsersForEvent(new EventUser(event.getEventOwnerId(), event.getEventId()));
		for (EventUser eventUser : users)
			eventUserDao.deleteUserEvent(eventUser);
	}



}