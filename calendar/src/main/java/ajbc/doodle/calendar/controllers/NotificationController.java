package ajbc.doodle.calendar.controllers;

import java.util.Collection;
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
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;
import ajbc.doodle.calendar.services.NotificationManager;
import ajbc.doodle.calendar.services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private NotificationManager manager;

	// CREATE //
	
	/**
	 * This method creates an notification for a event assigned to user.
	 * In case a event for a user does not exist an error will sent no such event exist.
	 * 
	 * @param notification the body of the notification to add.
	 * @param userId the user to receive the notification.
	 * @param eventId the event for the notification to alert.
	 * @return the notification added to the DB, in case of failure a custom error message.
	 * @throws DaoException
	 */
	@PostMapping(path = "/{userId}/{eventId}")
	public ResponseEntity<?> addNotification(@RequestBody Notification notification, @PathVariable Integer userId, @PathVariable Integer eventId) {
		try {
		
			notification = notificationService.createNotificationAndUpdateManger(notification, eventId, userId);
						
			return ResponseEntity.status(HttpStatus.CREATED).body(notification);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add notification to DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}

	
	/**
	 * This method adds a list of notification with events assigned to user.
	 * In case a event for a user does not exist an error will sent no such event exist.
	 * 
	 * @param notifications the list to add the DB.
	 * @return the list of notifications added in case of success, in case of failure a custom error message.
	 */
	@PostMapping
	public ResponseEntity<?> addListOfNotifications(@RequestBody List<Notification> notifications) {
		try {
	
			notifications = notificationService.addListNotificationsToDB(notifications);
						
			return ResponseEntity.status(HttpStatus.CREATED).body(notifications);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add notifications to DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	/**
	 * This method is used the check whether exist an subscription for user.
	 * 
	 * @param subscription information to the server
	 * @return true in case subscribed, false if not.
	 */
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
	
	
	
	// GET //
	
	
	/**
	 * This method provides a GET endpoint that returns the public key, To pass the public key from the server to the JavaScript application.
	 * 
	 * @return public key.
	 */
	@GetMapping(path = "/publicSigningKey", produces = "application/octet-stream")
	public byte[] publicSigningKey() {
		return notificationService.publicSigningKey();
	}
	
	/**
	 * This method returns all notifications in the DB.
	 * 
	 * @return list of notifications, in case of failure a custom error message.
	 */
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
	
	/**
	 * This method returns a notification by its id from the DB.
	 * 
	 * @param id notification required.
	 * @return the desired notification, in case of failure a custom error message.
	 */
	@GetMapping(path = "/{id}")
	public ResponseEntity<?> getNotificationById(@PathVariable Integer id) {
		Notification notification;
		try {
			notification = notificationService.getNotificationById(id);
			return ResponseEntity.ok(notification);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get notification with id: " + id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);

		}

	}

	/**
	 * This method returns list of notifications by event id. 
	 * 
	 * @param eventId the event to required.
	 * @return list of notifications, in case of failure a custom error message.
	 */
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

	
	
	// UPDATE //
	
	
	/**
	 * This method updates notification by id.
	 * 
	 * @param notification the body of the notification to update.
	 * @param id the notification id to update.
	 * @return the updated notification, in case of failure a custom error message.
	 */
	@PutMapping(path = "/{id}")
	public ResponseEntity<?> updateNotification(@RequestBody Notification notification, @PathVariable Integer id) {

		try {
			notification.setNotificationId(id);
			
			notificationService.updateNotification(notification);
						
			return ResponseEntity.status(HttpStatus.OK).body(notification);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update notification in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}

	
	/**
	 * This method updates a list of notification.
	 * 
	 * @param notifications the notifications required to update.
	 * @return the list of the updated notifications, in case of failure a custom error message.
	 */
	@PutMapping
	public ResponseEntity<?> updateNotifications(@RequestBody List<Notification> notifications) {

		try {
	
			notificationService.updateListNotifications(notifications);
			
			return ResponseEntity.status(HttpStatus.OK).body(notifications);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update notifications in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}

	
	// DELETE //
	
	
	/**
	 * This method deletes an notification by 2 options: 1. soft delete 2. hard delete.
	 * soft delete - set a flag for this notification.
	 * hard delete - erase the notification from the DB.
	 * 
	 * @param map contains key and value of the required method type to delete.
	 * @param id is the notification id required to delete.
	 * @return the deleted notification is case of success, or a costume message error in case of failure.
	 */
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
			
			manager.deleteNotificationAndInitiateThread(notification);

			return ResponseEntity.ok(notification);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to delete notification from DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}

	/**
	 * This method deletes List of notifications by 2 options: 1. soft delete 2. hard delete.
	 * soft delete - set a flag for the notifications.
	 * hard delete - erase the notifications from the DB. 
	 * 
	 * @param map contains key and value of the required method type to delete.
	 * @param events is the list of notifications required to delete.
	 * @return the deleted notifications is case of success, or a costume message error in case of failure.
	 */
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
			errMsg.setMessage("failed to delete notifications from DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	
	
}
