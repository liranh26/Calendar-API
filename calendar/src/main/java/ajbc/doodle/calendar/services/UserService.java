package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.User;

@Service
public class UserService {

	@Autowired
	@Qualifier("htUserDao")
	UserDao dao;

	public void addUser(User user) throws DaoException {
		dao.addUser(user);
	}

	public List<User> getAllUsers() throws DaoException {
		return dao.getAllUsers();
	}

	public void deleteAllUsers() throws DaoException {
		dao.deleteAllUsers();
	}

}
