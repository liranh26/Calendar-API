package ajbc.doodle.calendar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.services.EventService;
import ajbc.doodle.calendar.services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	
	@Autowired
	private NotificationService notificationService;
	
	@PostMapping
	public ResponseEntity<?> addUser(Notification notification) throws DaoException {
		try {
			notificationService.addNotificationToDB(notification);
			return ResponseEntity.status(HttpStatus.CREATED).body(notification);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add event to DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	

	
}
