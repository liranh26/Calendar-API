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

//	@GetMapping(path = "/event/{eventId}")
//	public ResponseEntity<?> getUsersForEvent(@PathVariable Integer eventId) {
//		List<User> users;
//		try {
//			users = userService.getUsersForEvent(eventId);
//			return ResponseEntity.ok(users);
//		} catch (DaoException e) {
//			ErrorMessage errMsg = new ErrorMessage();
//			errMsg.setData(e.getMessage());
//			errMsg.setMessage("Failed to get users with event id: " + eventId);
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);
//
//		}
//
//	}

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

	@PutMapping(path = "/{id}")
	public ResponseEntity<?> updateProduct(@RequestBody User user, @PathVariable Integer id) {

		try {
			user.setUserId(id);
			userService.updateUser(user);
			user = userService.getUserById(user.getUserId());
			return ResponseEntity.status(HttpStatus.OK).body(user);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update user in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> softDeleteUser(@PathVariable Integer id) {

		try {
			userService.deleteUser(id);

			User user = userService.getUserById(id);
			return ResponseEntity.status(HttpStatus.OK).body(user);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to delete user from DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	
	
	
	private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();
	
	@PostMapping("/login/{email}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> subscribe(@RequestBody Subscription subscription, @PathVariable(required = false) String email) {
		
		try {
			userService.addUserSubscription(email, subscription);
			
			return ResponseEntity.status(HttpStatus.OK).body(email);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to subscribe user with email: "+email);
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	
//		this.subscriptions.put(subscription.getEndpoint(), subscription);
//		System.out.println(subscription);
//		System.out.println("Subscription added with email "+email);
	}
	
	@PostMapping("/logout/{email}")
	public ResponseEntity<?> unsubscribe(@RequestBody SubscriptionEndpoint subscription, @PathVariable(required = false) String email) {
		
		try {
			//throws exception if null
			userService.unsubscribeUser(email, subscription);
			
			return ResponseEntity.status(HttpStatus.OK).body(email);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to unsubscribe user with email: "+email);
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
		
//		this.subscriptions.remove(subscription.getEndpoint());
//		
//		System.out.println("Subscription with email "+email+" got removed!");
	}
	
	
	
	
	
	
//
//	private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<String, Subscription>();
//	private int counter;
//
//
//
//
//
//	@GetMapping(path = "/publicSigningKey", produces = "application/octet-stream")
//	public byte[] publicSigningKey() {
//		return userService.publicSigningKey();
//	}
//
//	@GetMapping(path = "/publicSigningKeyBase64")
//	public String publicSigningKeyBase64() {
//		return userService.publicSigningKeyBase64();
//	}
//
//	@PostMapping("/subscribe/{email}")
//	@ResponseStatus(HttpStatus.CREATED)
//	public void subscribe(@RequestBody Subscription subscription, @PathVariable(required = false) String email) {
//		// if user is registered allow subscription
//		this.subscriptions.put(subscription.getEndpoint(), subscription);
//		System.out.println("Subscription added with email " + email);
//	}
//
//	@PostMapping("/unsubscribe/{email}")
//	public void unsubscribe(@RequestBody SubscriptionEndpoint subscription,
//			@PathVariable(required = false) String email) {
//		this.subscriptions.remove(subscription.getEndpoint());
//		System.out.println("Subscription with email " + email + " got removed!");
//	}
//
//	@PostMapping("/isSubscribed")
//	public boolean isSubscribed(@RequestBody SubscriptionEndpoint subscription) {
//		return this.subscriptions.containsKey(subscription.getEndpoint());
//	}
//
//	@Scheduled(fixedDelay = 3_000)
//	public void testNotification() {
//		if (this.subscriptions.isEmpty()) {
//			return;
//		}
//		counter++;
//		try {
//
//			not notification = new not(counter, LocalDateTime.now(), "Test notification", "Test message");
//			userService.sendPushMessageToAllSubscribers(this.subscriptions,
//					new PushMessage("message: " + counter, notification.toString()));
//			System.out.println(notification);
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}





}