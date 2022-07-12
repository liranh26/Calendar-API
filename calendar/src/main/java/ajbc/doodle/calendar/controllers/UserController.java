package ajbc.doodle.calendar.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import org.springframework.web.bind.annotation.ResponseStatus;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	
	/*** GET ***/
	
	@GetMapping(path = "/event/{eventId}")
	public ResponseEntity<?> getUsersForEvent(@PathVariable Integer eventId) {
		List<User> users;
		try {
			users = userService.getUsersByEventId(eventId);
			return ResponseEntity.ok(users);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get users with event id: " + eventId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);

		}

	}

	@GetMapping(path = "/allUsers")
	public ResponseEntity<?> getAllUser() {
		List<User> users;
		try {
			users = userService.getAllUsers();
			return ResponseEntity.ok(users);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to get users");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);
		}
	}
	
	@GetMapping(path = "/email/{email}")
	public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
		User user;
		try {
			user = userService.getUserByEmail(email);
			return ResponseEntity.ok(user);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get user with email: " + email);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);

		}

	}

	@GetMapping(path = "/id/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Integer id) {
		User user;
		try {
			user = userService.getUserById(id);
			return ResponseEntity.ok(user);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get user with id: " + id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);

		}
	}
	

	@GetMapping(path = "/range")
	public ResponseEntity<?> getUserById(@RequestParam Map<String, String> map) {
		List<User> users;
		Collection<String> values = map.values();
		Collection<String> keys = map.keySet();
		
		try {
			
			LocalDateTime start = LocalDateTime.parse(map.get("start"));
			LocalDateTime end = LocalDateTime.parse(map.get("end"));
			
			users = userService.getUsersWithEventsInRange(start, end);
			
			return ResponseEntity.ok(users);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get users in dates range." );
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);

		}

	}


	/*** CREATE ***/

	@PostMapping
	public ResponseEntity<?> addUser(@RequestBody User user) throws DaoException {
		try {
			userService.addUser(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(user);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add user to DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	
	@PostMapping(path = "/list")
	public ResponseEntity<?> addListUsers(@RequestBody List<User> users) throws DaoException {
		try {
			userService.addListOfUsers(users);
			return ResponseEntity.status(HttpStatus.CREATED).body(users);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add user to DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	@PostMapping("/login/{email}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> subscribe(@RequestBody Subscription subscription, @PathVariable(required = false) String email) {
		
		try {
			userService.addUserSubscription(email, subscription);
			
			System.out.println("Subscription added with email "+email);
			return ResponseEntity.status(HttpStatus.OK).body(email);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to subscribe user with email: "+email);
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}

	}
	
	@PostMapping("/logout/{email}")
	public ResponseEntity<?> unsubscribe(@RequestBody SubscriptionEndpoint subscription, @PathVariable(required = false) String email) {
		
		try {
			//throws exception if null
			userService.unsubscribeUser(email, subscription);
			
			System.out.println("Subscription with email "+email+" got removed!");
			
			return ResponseEntity.status(HttpStatus.OK).body(email);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to unsubscribe user with email: "+email);
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	
	}
	
	
	

	/*** UPDATE ***/
	
	@PutMapping(path = "/{id}")
	public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable Integer id) {

		try {
			user.setUserId(id);
			userService.updateUser(user);
			
			return ResponseEntity.status(HttpStatus.OK).body(user);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update user in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}

	@PutMapping
	public ResponseEntity<?> updateListOfUsers(@RequestBody List<User> users) {

		try {

			userService.updateListOfUsers(users);

			return ResponseEntity.status(HttpStatus.OK).body(users);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update user in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	/*** DELETE ***/
	
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> softDeleteUser(@RequestParam Map<String, String> map, @PathVariable Integer id) {
		Collection<String> values = map.values();
		try {
			User user = userService.getUserById(id);
			
			if (values.contains("soft"))
				userService.softDeleteUser(user);
			else
				userService.hardDeleteUser(user);

			
			return ResponseEntity.ok(user);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to delete user from DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	

}