package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.EventUserDao;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.EventUser;
import ajbc.doodle.calendar.entities.User;

@Service
public class UserService {

	@Autowired
	@Qualifier("htUserDao")
	UserDao userDao;
	@Autowired
	@Qualifier("htEventUserDao")
	EventUserDao eventUserDao;

	public void addUser(User user) throws DaoException {
		System.out.println(user);
		userDao.addUser(user);
	}

	public void addUserToEvent(Integer eventId, Integer userId) throws DaoException {
		eventUserDao.addEventToUser(new EventUser(eventId, userId));
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

	public List<Event> getUserEvents(Integer userId) throws DaoException {
		User user = userDao.getUser(userId);
		return user.getEvents();
	}

	public List<Event> getEventsOfUser(Integer userId) throws DaoException {
		User user = userDao.getUser(userId);
		return user.getEvents();
	}
	
	public List<User> getUserGuestsForEvent(Integer userId) throws DaoException{
		User user = userDao.getUser(userId);
		return user.getEvents().get(0).getGuests();
	}


}
