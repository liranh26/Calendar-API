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

	// CREATE //
	
	/**
	 * This method creates an event entered by the user and writes it to the DB. 
	 *
	 * @param event - gets an event entered by the user. id is generated automatically for the event.
	 * @param userId - the owner of the created event.
	 * @return in case of success the created event, in case failure a custom error message.
	 */
	@PostMapping(path = "/single/{userId}")
	public ResponseEntity<?> createEventToUser(@RequestBody Event event , @PathVariable Integer userId) {

		try {
			event = eventService.createEventForUser(userId, event);
			return ResponseEntity.status(HttpStatus.CREATED).body(event);
			
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to create event to the user "+ userId);
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	/**
	 * This method creates an List of events entered by the user and writes it to the DB. 
	 * 
	 * @param events - gets List of events entered by the user.
	 * @param userId - the owner of the created events.
	 * @return in case of success the created events, in case failure a custom error message.
	 */
	@PostMapping(path = "/list/{userId}")
	public ResponseEntity<?> createEventsToUser(@RequestBody List<Event> events , @PathVariable Integer userId) {

		try {
			events = eventService.createListEventsToUser(userId, events);
			return ResponseEntity.status(HttpStatus.CREATED).body(events);
			
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to add user to event.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	/**
	 * This method adds List of guests to an event entered by the user and writes it to the DB. 
	 * 
	 * @param users - gets a List of users to add to a specific event.
	 * @param eventId - the event id to add the List of users entered.
	 * @return - in case of success the created events, in case failure a custom error message.
	 * @throws DaoException
	 */
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
			errMsg.setMessage("Failed to add users to event.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	

	// GET //
	
	
	/**
	 * This method sends all events that exist in the DB.
	 *  
	 * @return return a List of all events in DB.
	 */
	@GetMapping
	public ResponseEntity<?> getAllEvents() {
		List<Event> events;
		try {
			events = eventService.getAllEvents();
			return ResponseEntity.ok(events);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get all events from the database.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg);
			
		}
		
	}

	/**
	 * This method sends an event from the DB by entered id from user.
	 * In case such event does not exists an error message is returned.
	 * 
	 * @param id - the id of the requested event.
	 * @return
	 */
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

	/**
	 * This method send custom events for specific user. 
	 * It receives @RequestParam map with keys & values of required operations.
	 * The default is to get all events for a user.
	 * values: 'start' + 'end' represent events of a user in a range between start date and time to end date and time.
	 * value: 'future' represent upcoming events of a user.
	 * values: 'minutes' + 'hours' represent events of a user the next coming num of minutes and hours.
	 * 
	 * @param map
	 * @param id
	 * @return
	 */
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
	
	/**
	 * This method send custom events. 
	 * values: 'start' + 'end' are LocalDateTime variables which represent events in a range between start date and time to end date and time.
	 * 
	 * @param map with keys & values of required operations.
	 * @return range of events between the requested values.
	 */
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
	
	
	// UPDATE //
	
	/**
	 * This method update an event in the database.
	 * NOTE - in case the event does not exist a custom message will be sent.
	 * 
	 * @param event - the event object to enter.
	 * @param eventId - the id of the event to update.
	 * @return the updated event.
	 */
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
			errMsg.setMessage("failed to update event in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg) ;
		}
	}
	
	/**
	 * This method update a List of events in the database.
	 * NOTE - in case the event does not exist a custom message will be sent.
	 *
	 * @param events - the list of the events to update.
	 * @return the updates events.
	 */
	@PutMapping
	public ResponseEntity<?> updateListEvents(@RequestBody List<Event> events){
		
		try {

			eventService.updateListOfEvents(events);
			
			return ResponseEntity.status(HttpStatus.OK).body(events);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to update events in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg) ;
		}
	}
	
	
	
	// DELETE //
	
	/**
	 * This method deletes an event by 2 options: 1. soft delete 2. hard delete.
	 * soft delete - set a flag for this event.
	 * hard delete - erase the event from the DB.
	 * 
	 * @param map contains key and value of the required method type to delete.
	 * @param id is the event id required to delete.
	 * @return the deleted event is case of success, or a costume message error in case of failure.
	 */
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
			errMsg.setMessage("failed to delete event in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	/**
	 * This method deletes List of events by 2 options: 1. soft delete 2. hard delete.
	 * soft delete - set a flag for the events.
	 * hard delete - erase the events from the DB. 
	 * 
	 * @param map contains key and value of the required method type to delete.
	 * @param events is the list of events required to delete.
	 * @return the deleted events is case of success, or a costume message error in case of failure.
	 */
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
			errMsg.setMessage("failed to delete events in DB.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errMsg);
		}
	}
	
	
	
	
}