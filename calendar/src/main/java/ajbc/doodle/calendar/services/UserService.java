package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.EventUserDao;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.User;

@Service
public class UserService {

	@Autowired
	@Qualifier("htUserDao")
	UserDao userDao;

	public void addUser(User user) throws DaoException {
		userDao.addUser(user);
	}

	public List<User> getAllUsers() throws DaoException {
		return userDao.getAllUsers();
	}

	public void deleteAllUsers() throws DaoException {
		userDao.deleteAllUsers();
	}

}
