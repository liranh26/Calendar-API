package ajbc.doodle.calendar.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.EventDao;
import ajbc.doodle.calendar.daos.interfaces.EventUserDao;
import ajbc.doodle.calendar.daos.interfaces.NotificationDao;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.EventUser;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.enums.EventRepeating;

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
	private NotificationService notificationService;

	public Event createEventForUser(Integer userId, Event event) throws DaoException {

		User user = userDao.getUser(userId);

		event.setEventOwnerId(user.getUserId());
		event.setOwner(user);

		addEventToDB(event);

		return event;
	}

	public List<Event> createEventsToUser(Integer userId, List<Event> events) throws DaoException {
		for (Event event : events)
			createEventForUser(userId, event);

		return events;
	}

	// TODO fix default notification
	public void addEventToDB(Event event) throws DaoException {
		eventDao.addEvent(event);
//		notificationService.createDefaultNotification(event);
	}

	public List<Event> getAllEvents() throws DaoException {
		return eventDao.getAllEvents();
	}

	public void deleteAllEvents() throws DaoException {
		eventDao.deleteAllEvents();
	}

	public Event getEventById(Integer eventId) throws DaoException {
		return eventDao.getEvent(eventId);
	}

	//TODO check this function! ********
	public List<Event> getEventsByUserId(Integer id) throws DaoException {
		List<Event> events = new ArrayList<Event>();
		List<EventUser> eventUser = eventUserDao.getEventsByUserId(id);

		for (EventUser tmpEventUser : eventUser)
			events.add(getEventById(tmpEventUser.getEventId()));

		return events;
	}

	// TODO fix dates between in dao
	public List<Event> getEventsOfUserInRange(LocalDate startDate, LocalDate endDate, LocalTime startTime,
			LocalTime endTime, Integer id) throws DaoException {

		List<Event> events = eventDao.getEventsOfUserInRange(startDate, endDate, startTime, endTime);
		List<EventUser> eventUser = eventUserDao.getUsersByEventId(id);

		return events.stream().filter(e -> eventUser.contains(e.getEventId())).toList();
	}

	// TODO add guests to event
//	public void addGuestToEvent(Event event, Integer userId) throws DaoException {
//		event.getGuests().add(userDao.getUser(userId));
//		eventUserService.addUserToEvent(new EventUser(event.getEventId(), userId));
//		
//	}

	
	public void updateListOfEvents(List<Event> events) throws DaoException {
		for (Event event : events) 
			updateEvent(event);
	
	}
	
	
	public void updateEvent(Event event) throws DaoException {

		User user = userDao.getUser(event.getEventOwnerId());
		event.setOwner(user);

		eventDao.updateEvent(event);
	}

	
	public void softDeleteListOfEvents(List<Event> events) throws DaoException {
		for (Event event : events) {
			User user = userDao.getUser(event.getEventOwnerId());
			event.setOwner(user);			
			softDelete(event);
		}
			
	}

	public void softDelete(Event event) throws DaoException {

		event.setDiscontinued(1);
		eventDao.updateEvent(event);

	}

	public void hardDeleteListOfEvents(List<Event> events) throws DaoException {
		for (Event event : events)
			hardDelete(event);
	}
	
	public void hardDelete(Event event) throws DaoException {
		User user = userDao.getUser(event.getEventOwnerId());
		event.setOwner(user);

		deleteEventNotifications(event);

		deleteUsersFromEvent(event);

		eventDao.deleteEvent(event);

	}

	private void deleteEventNotifications(Event event) throws DaoException {
		List<Notification> nots = notificationService.getNotificationsByEvent(event.getEventId());
		for (Notification notification : nots)
			notificationService.hardDelete(notification);
	}

	private void deleteUsersFromEvent(Event event) throws DaoException {
		List<EventUser> users = eventUserDao
				.getAllUsersForEvent(new EventUser(event.getEventOwnerId(), event.getEventId()));
		for (EventUser eventUser : users)
			eventUserDao.deleteUserEvent(eventUser);
	}



}