package ajbc.doodle.calendar.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.EventService;
import ajbc.doodle.calendar.services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private EventService eventService;
	
	@PostMapping(path = "/{eventId}")
	public ResponseEntity<?> addNotification(@RequestBody Notification notification, @PathVariable Integer eventId) throws DaoException {
		try {
			notification.setEventId(eventId);
			
			notificationService.addNotificationToDB(notification, eventService.getEventById(eventId));
			return ResponseEntity.status(HttpStatus.CREATED).body(notification);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add event to DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	
	@GetMapping(path="/{id}")
	public ResponseEntity<?> getNotificationById(@PathVariable Integer id){
		Notification notification;
		try {
			notification = notificationService.getNotificationById(id);
			return ResponseEntity.ok(notification);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get user with id: "+id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg) ;
			
		}
		
	}
	

	@GetMapping(path="/event/{eventId}")
	public ResponseEntity<?> getNotificationByEvent(@PathVariable Integer eventId){
		Set<Notification> notifications;
		try {
			notifications = eventService.getEventById(eventId).getNotifications();
			return ResponseEntity.ok(notifications);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get notifications for event with id: "+eventId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg) ;
			
		}
		
	}

	
}
