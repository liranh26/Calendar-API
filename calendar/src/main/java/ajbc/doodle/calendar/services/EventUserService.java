package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.EventUserDao;
import ajbc.doodle.calendar.entities.EventUser;

@Service
public class EventUserService {

	@Autowired
	@Qualifier("htEventUserDao")
	EventUserDao dao;

	public void addUserToEvent(EventUser eventUser) throws DaoException {
		dao.addEventToUser(eventUser);
	}

	public List<EventUser> getAllEventUsersList() throws DaoException {
		return dao.getAllEventsAndUsers();
	}

	public void deleteAllEvents() throws DaoException {
		dao.deleteAllEventUsers();
	}

	public List<EventUser> getUsersByEventId(Integer eventId) throws DaoException {
		return dao.getUsersByEventId(eventId);
	}

	public List<EventUser> getEventsByUserId(Integer id) throws DaoException {
		return dao.getEventsByUserId(id);

	}

	public EventUser getEventUser(EventUser eventUser) throws DaoException {
		return dao.getEventForUser(eventUser);
	}

	public void updateEventUser(EventUser eventUser) throws DaoException {
		dao.updateUserEvent(eventUser);
	}

}
