package ajbc.doodle.calendar.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

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
	private NotificationService notificationService;

	@Autowired
	private EventUserService eventUserService;

	EventService() {

	}

	public void addEventToDB(Event event) throws DaoException {

		eventDao.addEvent(event);

		event.getNotifications().add(notificationService.createDefaultNotification(event));

		EventUser eventUser = new EventUser(event.getEventId(), event.getEventOwnerId());
		eventUserDao.addEventToUser(eventUser);
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

	public List<Event> getEventsByUserId(Integer id) throws DaoException {
		List<Event> events = new ArrayList<Event>();
		List<EventUser> eventUser = eventUserService.getEventsByUserId(id);

		for (EventUser tmpEventUser : eventUser)
			events.add(getEventById(tmpEventUser.getEventId()));

		return events;
	}

	// TODO fix dates between in dao
	public List<Event> getEventsOfUserInRange(LocalDate startDate, LocalDate endDate, LocalTime startTime,
			LocalTime endTime, Integer id) throws DaoException {

		List<Event> events = eventDao.getEventsOfUserInRange(startDate, endDate, startTime, endTime);
		List<EventUser> eventUser = eventUserService.getEventsForUser(id);

		return events.stream().filter(e -> eventUser.contains(e.getEventId())).toList();
	}

	public void addGuestToEvent(Integer eventId, Integer userId) throws DaoException {
		eventUserService.addUserToEvent(new EventUser(eventId, userId));
	}

	public void updateEvent(Event event, Integer userId) throws DaoException {
		Event oldEvent = getEventById(event.getEventId());
		if(oldEvent.getEventOwnerId() != userId)
			throw new DaoException("The user is not the owner of the event!");
		
//		event.setGuests(oldEvent.getGuests());
		event.setNotifications(oldEvent.getNotifications());
		
		eventDao.updateEvent(event);
	}


}
