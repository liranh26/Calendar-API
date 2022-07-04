package ajbc.doodle.calendar.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EmailDao;
import ajbc.doodle.calendar.daos.ProductDao;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Product;


@RequestMapping("/emails")
@RestController
public class EmailService {
	
	@Autowired
	EmailDao dao;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<User>> getAllEmails() throws DaoException {
		List<User> emails;

		emails = dao.getAllEmails();

		if (emails == null)
			return ResponseEntity.notFound().build();

		return ResponseEntity.ok(emails);
	}
	
	
	//get product by id via the path variable
	@RequestMapping(method = RequestMethod.GET, path="/{id}")
	public ResponseEntity<?> getEmailById(@PathVariable Integer id) {
		
		User email;
		try {
			email = dao.getEmail(id);
			return ResponseEntity.ok(email);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to get email with id: "+id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg) ;
			
		}
		
	}
}
