package ajbc.doodle.calendar.services;

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

	EventService(){
		
	}
	
	public void addEventToDB(Event event) throws DaoException {
		
		eventDao.addEvent(event);		
		
		System.out.println(event);
		event.getNotifications().add(
				notificationService.createDefaultNotification(event)); 
		
//		eventDao.updateEvent(event);
		
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
	
//	@Transactional
//	public List<Event> getEventsOfUser(Integer userId) throws DaoException
//	{
//		User user = userDao.getUser(userId);
//		System.out.println(user);
//		return user.getEvents();
//	}
}
