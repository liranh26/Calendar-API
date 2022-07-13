package ajbc.doodle.calendar.controllers;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.EventService;

@RestController
@RequestMapping("/events")
public class EventController {

	@Autowired
	private EventService eventService;

	/*** CREATE ***/
	

	@PostMapping(path = "/single/{userId}")
	public ResponseEntity<?> createEventToUser(@RequestBody Event event , @PathVariable Integer userId) throws DaoException {

		try {
			event = eventService.createEventForUser(userId, event);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(event);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add user to event.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	@PostMapping(path = "/list/{userId}")
	public ResponseEntity<?> createEventsToUser(@RequestBody List<Event> events , @PathVariable Integer userId) throws DaoException {

		try {
			
			events = eventService.createEventsToUser(userId, events);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(events);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add usssssser to event.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	@PostMapping(path = "/guests/{eventId}")
	public ResponseEntity<?> addGuestsToEvent(@RequestBody List<User> users , @PathVariable Integer eventId) throws DaoException {
		
		Event event;
		try {
			List<Integer> usersIds = eventService.getUsersIdsToList(users);
			event = eventService.getEventById(eventId);
			event = eventService.addGuestsToEvent(event, usersIds);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(event);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add usssssser to event.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	

	/*** GET ***/
	
	@GetMapping
	public ResponseEntity<?> getAllEvents() {
		List<Event> events;
		try {
			events = eventService.getAllEvents();
			return ResponseEntity.ok(events);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get events.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);
			
		}
		
	}

	
	@GetMapping(path = "/{id}")
	public ResponseEntity<?> getEventById(@PathVariable Integer id) {
		Event event;
		try {
			event = eventService.getEventById(id);
			return ResponseEntity.ok(event);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get user with id: " + id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);

		}
	}


	@GetMapping(path = "/user/{id}")
	public ResponseEntity<?> getEventsForUser(@RequestParam Map<String, String> map, @PathVariable Integer id) {
		List<Event> events;
		Set<String> keys = map.keySet();

		try {
			if (keys.contains("start") && keys.contains("end")) {
				LocalDateTime start = LocalDateTime.parse(map.get("start"));
				LocalDateTime end = LocalDateTime.parse(map.get("end"));
				events = eventService.getEventsOfUserInRange(start, end, id);				
			}
			else if(keys.contains("future"))
				events = eventService.getFutureEventsForUser(id);
			else if(keys.contains("minutes") && keys.contains("hours")) {
				Integer hours = Integer.parseInt(map.get("hours"));
				Integer minutes = Integer.parseInt(map.get("minutes"));
				events = eventService.getUserEventsByFollowingTime(id, hours, minutes);
			}
			else
				events = eventService.getEventsByUserId(id);
			return ResponseEntity.ok(events);
			
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get user with id: " + id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);
		}
	}
	
	@GetMapping(path = "/range")
	public ResponseEntity<?> getEventsByRange(@RequestParam Map<String, String> map) {
		Set<String> keys = map.keySet();

		List<Event> events;
		try {
			if(keys.contains("start") && keys.contains("end")) {
				LocalDateTime start = LocalDateTime.parse(map.get("start"));
				LocalDateTime end = LocalDateTime.parse(map.get("end"));
				events = eventService.getEventsByRange(start, end);				
				return ResponseEntity.ok(events);
			}
			throw new DaoException("keys doesnt match");
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get events in range.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);
		}
	}
	
	
	/*** UPDATE ***/
	
	@PutMapping(path = "/{eventId}")
	public ResponseEntity<?> updateEvent(@RequestBody Event event, @PathVariable Integer eventId){
		
		try {
			event.setEventId(eventId);
			eventService.updateEvent(event);
			
			event = eventService.getEventById(event.getEventId());
			return ResponseEntity.status(HttpStatus.OK).body(event);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update user in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg) ;
		}
	}
	
	@PutMapping
	public ResponseEntity<?> updateListEvents(@RequestBody List<Event> events){
		
		try {

			eventService.updateListOfEvents(events);
			
			return ResponseEntity.status(HttpStatus.OK).body(events);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update user in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg) ;
		}
	}
	
	
	
	/*** DELETE ***/
	
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteEvent(@RequestParam Map<String, String> map, @PathVariable Integer id) {
		Collection<String> values = map.values();

		Event event;
		try {
			event = eventService.getEventById(id);
			
			if (values.contains("soft"))
				eventService.softDeleteEvent(event);
			else
				eventService.hardDeleteEvent(event);

			return ResponseEntity.ok(event);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update user in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	
	@DeleteMapping 
	public ResponseEntity<?> deleteListOfEvents(@RequestParam Map<String, String> map, @RequestBody List<Event> events) {
		Collection<String> values = map.values();
		
		try {
			if (values.contains("soft"))
				eventService.softDeleteListOfEvents(events);
			else
				eventService.hardDeleteListOfEvents(events);

			return ResponseEntity.ok(events);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update user in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	
	
	
}