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
import ajbc.doodle.calendar.services.EventService;

@RestController
@RequestMapping("/events")
public class EventController {

	
	@Autowired
	private EventService eventService;
	
	@PostMapping
	public ResponseEntity<?> addUser(Event event) throws DaoException {
		try {
			eventService.addEventToDB(event);
			return ResponseEntity.status(HttpStatus.CREATED).body(event);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add event to DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	

	
}
