package ajbc.doodle.calendar.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.daos.ProductDao;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Product;

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
