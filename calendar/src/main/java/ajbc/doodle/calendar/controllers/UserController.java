package ajbc.doodle.calendar.controllers;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.algorithms.Algorithm;

import ajbc.doodle.calendar.ServerKeys;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.UserService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ajbc.doodle.calendar.Application;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.not;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;
import ajbc.doodle.calendar.services.CryptoService;

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