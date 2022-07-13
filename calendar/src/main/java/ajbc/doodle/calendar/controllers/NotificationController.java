package ajbc.doodle.calendar.controllers;

import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ajbc.doodle.calendar.ServerKeys;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;
import ajbc.doodle.calendar.services.CryptoService;
import ajbc.doodle.calendar.services.EventService;
import ajbc.doodle.calendar.services.NotificationManager;
import ajbc.doodle.calendar.services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private NotificationManager manager;

	
	@PostMapping("/isSubscribed")
	public boolean isSubscribed(@RequestBody SubscriptionEndpoint subscription) {
		try {
			return notificationService.isSubscribed(subscription);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to check subscription in the DB.");
			return false;
		}
	}
	

	@PostMapping(path = "/{userId}/{eventId}")
	public ResponseEntity<?> addNotification(@RequestBody Notification notification, @PathVariable Integer userId, @PathVariable Integer eventId)
			throws DaoException {
		try {
		
			notification = notificationService.createNotification(notification, eventId, userId);
						
			return ResponseEntity.status(HttpStatus.CREATED).body(notification);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add event to DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}

	
	
	@PostMapping
	public ResponseEntity<?> addListOfNotifications(@RequestBody List<Notification> notifications)
			throws DaoException {
		try {
	
			notifications = notificationService.addListNotificationsToDB(notifications);
						
			return ResponseEntity.status(HttpStatus.CREATED).body(notifications);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add event to DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("No notification found" , e.getMessage()));	

		}
	}
	
	
	//when opening the site
	//To pass the public key from the server to the JavaScript application, the Spring Boot application provides a GET endpoint that returns the public key.
	@GetMapping(path = "/publicSigningKey", produces = "application/octet-stream")
	public byte[] publicSigningKey() {
		return notificationService.publicSigningKey();
	}
	
	
	@GetMapping
	public ResponseEntity<?> getAllNotifications() {
		List<Notification> notifications;
		try {
			notifications = notificationService.getAllNotifications();
			return ResponseEntity.ok(notifications);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get notifications from DB");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);

		}

	}
	
	@GetMapping(path = "/{id}")
	public ResponseEntity<?> getNotificationById(@PathVariable Integer id) {
		Notification notification;
		try {
			notification = notificationService.getNotificationById(id);
			return ResponseEntity.ok(notification);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get user with id: " + id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);

		}

	}

	@GetMapping(path = "/event/{eventId}")
	public ResponseEntity<?> getNotificationByEvent(@PathVariable Integer eventId) {
		List<Notification> notifications;
		try {
			notifications = notificationService.getNotificationsByEvent(eventId);
			return ResponseEntity.ok(notifications);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get notifications for event with id: " + eventId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);

		}

	}

	@PutMapping(path = "/{id}")
	public ResponseEntity<?> updateNotification(@RequestBody Notification notification, @PathVariable Integer id) {

		try {

			notification.setNotificationId(id);
			
			notificationService.updateNotification(notification);
						
			return ResponseEntity.status(HttpStatus.OK).body(notification);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update user in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}

	
	@PutMapping
	public ResponseEntity<?> updateNotifications(@RequestBody List<Notification> notifications) {

		try {
	
			notificationService.updateListNotifications(notifications);
			
			return ResponseEntity.status(HttpStatus.OK).body(notifications);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update user in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}

	
	
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteNotification(@RequestParam Map<String, String> map, @PathVariable Integer id) {
		Collection<String> values = map.values();

		Notification notification;
		try {
			notification = notificationService.getNotificationById(id);
			
			if (values.contains("soft"))
				notificationService.softDelete(notification);
			else
				notificationService.hardDeleteNotification(notification);
			
			manager.deleteNotificationQueue(notification);

			return ResponseEntity.ok(notification);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update user in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}

	
	@DeleteMapping 
	public ResponseEntity<?> deleteListNotificatioins(@RequestParam Map<String, String> map, @RequestBody List<Notification> notifications) {
		Collection<String> values = map.values();
		try {
			
			if (values.contains("soft"))
				notificationService.softDeleteListNotification(notifications);
			else
				notificationService.hardDeleteListNotification(notifications);

			return ResponseEntity.ok(notifications);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update user in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	
	
}
