package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.EventDao;
import ajbc.doodle.calendar.entities.Event;

@Service
public class EventService {

	@Autowired
	@Qualifier("htEventDao")
	EventDao dao;

	//TODO check valid events date?
	public void addEventToDB(Event event) throws DaoException {
		dao.addEvent(event);
	}

	public List<Event> getAllEvents() throws DaoException {
		return dao.getAllEvents();
	}

	public void deleteAllEvents() throws DaoException {
		dao.deleteAllEvents();
	}

}
