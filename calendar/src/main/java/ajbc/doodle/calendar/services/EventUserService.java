package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.EventUserDao;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
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

}
