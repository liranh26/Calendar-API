package ajbc.doodle.calendar.services;

import java.util.ArrayList;
import java.util.Iterator;
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
	private EventService eventService;
	
	@Autowired
	private EventUserService eventUserService;

	

	public void addUser(User user) throws DaoException {
		userDao.addUser(user);
	}

	public void addUserToEvent(Integer eventId, Integer userId) throws DaoException {
		eventUserService.addUserToEvent(new EventUser(eventId, userId));
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

	//TODO refactor code
	public List<User> getUsersForEvent(Integer eventId) throws DaoException {
		List<User> users = new ArrayList<User>();
		List<EventUser> eventsForUser = eventUserService.getEventsForUser(eventId);

		eventsForUser.stream().forEach(e -> {
			try {
				users.add(getUserById(e.getUserId()));

			} catch (DaoException e1) {
				e1.printStackTrace();
			}
		});

		return users;
	}

	public void updateUser(User user) throws DaoException {
		userDao.updateUser(user);
	}

	public void deleteUser(Integer id) throws DaoException {
		userDao.deleteUser(id);
		
	}


}
