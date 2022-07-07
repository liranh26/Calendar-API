package ajbc.doodle.calendar.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.EventDao;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;

@Service
public class EventService {

	@Autowired
	@Qualifier("htEventDao")
	EventDao dao;
	@Autowired
	@Qualifier("htUserDao")
	UserDao userDao;

	public void addEventToDB(Event event) throws DaoException {
//		System.out.println(event);
		//TODO add the eventid + userid to the table event_user
		dao.addEvent(event);
	}
	
	
	public List<Event> getAllEvents() throws DaoException {
		return dao.getAllEvents();
	}

	public void deleteAllEvents() throws DaoException {
		dao.deleteAllEvents();
	}

	public Event getEventById(Integer eventId) throws DaoException {
		return dao.getEvent(eventId);
	}
	
//	@Transactional
//	public List<Event> getEventsOfUser(Integer userId) throws DaoException
//	{
//		User user = userDao.getUser(userId);
//		System.out.println(user);
//		return user.getEvents();
//	}
}
