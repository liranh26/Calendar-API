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

	/**
	 * This method gets a list of users assigned to an event.
	 * 
	 * @param eventId the event of the users.
	 * @return list of users assigned to the event in case of success, or a costume
	 *         message error in case of failure.
	 */
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

	/**
	 * This method gets all users in the DB.
	 * 
	 * @return list of users from the DB, or a costume message error in case of
	 *         failure.
	 */
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

	/**
	 * This method gets a user by hit email.
	 * 
	 * @param email the user email is unique in the database.
	 * @return user with the required email, or a costume message error in case of
	 *         failure.
	 */
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

	/**
	 * This method get a user from the DB.
	 * 
	 * @param id the user required to fetch from the DB.
	 * @return user, or a costume message error in case of failure.
	 */
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

	/**
	 * This method gets all users that have an event between start date and time to
	 * end date and time. values: 'start' + 'end' are LocalDateTime variables which
	 * represent users with events in a range between start date and time to end
	 * date and time.
	 *
	 * @param map with keys & values of required operations.
	 * @return range of users with events between the requested values.
	 */
	@GetMapping(path = "/range")
	public ResponseEntity<?> getUserById(@RequestParam Map<String, String> map) {
		List<User> users = null;
		Collection<String> keys = map.keySet();

		try {
			if (keys.contains("start") && keys.contains("end")) {
				LocalDateTime start = LocalDateTime.parse(map.get("start"));
				LocalDateTime end = LocalDateTime.parse(map.get("end"));

				users = userService.getUsersWithEventsInRange(start, end);
			}
			return ResponseEntity.ok(users);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get users in dates range.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);

		}

	}

	
	
	// CREATE //

	
	/**
	 * This method creates a new user.
	 * 
	 * @param user the body of the user content.
	 * @return new user, or a costume message error in case of failure.
	 */
	@PostMapping
	public ResponseEntity<?> addUser(@RequestBody User user) {
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

	/**
	 * This method adds a list of users to the DB.
	 * 
	 * @param users list containsUser objects with the body of the user content.
	 * @return a list of the added users, or a costume message error in case of failure.
	 */
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

	/**
	 * This method sends the subscription of a user.
	 * 
	 * @param subscription the data from browser.
	 * @param email the user email which trys to login.
	 * @return the email of the user.
	 */
	@PostMapping("/login/{email}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> subscribe(@RequestBody Subscription subscription,
			@PathVariable(required = false) String email) {

		try {
			userService.addUserSubscription(email, subscription);

			return ResponseEntity.status(HttpStatus.OK).body(email);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to subscribe user with email: " + email);
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}

	}

	/**
	 * This method logs out a user.
	 * 
	 * @param subscription the user data.
	 * @param email the logged user email.
	 * @return email of the user.
	 */
	@PostMapping("/logout/{email}")
	public ResponseEntity<?> unsubscribe(@RequestBody SubscriptionEndpoint subscription,
			@PathVariable(required = false) String email) {

		try {
			// throws exception if null
			userService.unsubscribeUser(email, subscription);

			return ResponseEntity.status(HttpStatus.OK).body(email);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to unsubscribe user with email: " + email);
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}

	}

	
	
	// UPDATE //

	
	/**
	 * This method updates a user by id.
	 * 
	 * @param user the body of the object to update.
	 * @param id the user id to update.
	 * @return the user after update.
	 */
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

	/**
	 * This method updates a list of users.
	 * 
	 * @param users is the list of objects to update.
	 * @return the updated list of users.
	 */
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

	
	
	// DELETE //

	
	/**
	 * This method deletes a user from DB.
	 * soft delete - set a flag for this user.
	 * hard delete - erase the user from the DB.
	 * 
	 * @param map contains key and value of the required method type to delete.
	 * @param id is the user id required to delete.
	 * @return the deleted user is case of success, or a costume message error in case of failure.
	 */
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