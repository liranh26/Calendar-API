package ajbc.doodle.calendar.utils;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.UserService;

@Component
public class SeedDB {


	@Autowired
	private UserService userService;
	
	
	@EventListener
	public void seedDB(ContextRefreshedEvent event) {
		try {
			seedUsers();
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void seedUsers() throws DaoException {
		
		userService.deleteAllUsers();
		
		userService.addUser(new User("Liran", "Hadad", "test@test.com", 
						LocalDate.of(1990, 2, 26), 
						LocalDate.of(2022, 1, 1), 0));
		userService.addUser(new User("Snir", "Hadad", "test2@test.com", 
				LocalDate.of(1993, 7, 8), 
				LocalDate.of(2022, 5, 5), 0));
		userService.addUser(new User("Sapir", "Hadad", "test3@test.com", 
				LocalDate.of(1990, 7, 23), 
				LocalDate.of(2022, 6, 6), 0));
		
		userService.getAllUsers().stream().forEach(System.out::println);
		
	}
	
	
	
	
}
