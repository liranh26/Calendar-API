package ajbc.doodle.calendar.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.enums.EventRepeating;
import ajbc.doodle.calendar.services.EventService;
import ajbc.doodle.calendar.services.NotificationService;
import ajbc.doodle.calendar.services.UserService;

@Component
public class SeedDB {

	@Autowired
	private UserService userService;
	@Autowired
	private EventService eventService;
	@Autowired
	private NotificationService notificationService;

	@EventListener
	public void seedDB(ContextRefreshedEvent event) {
		try {
			seedUsers();
			seedEvents();
			seedNotifications();
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void seedUsers() throws DaoException {
		
		notificationService.deleteAllNotifications();
		eventService.deleteAllEvents();
		userService.deleteAllUsers();

		userService.addUser(
				new User("Liran", "Hadad", "test@test.com", LocalDate.of(1990, 2, 26), LocalDate.of(2022, 1, 1), 0));
		userService.addUser(
				new User("Snir", "Hadad", "test2@test.com", LocalDate.of(1993, 7, 8), LocalDate.of(2022, 5, 5), 0));
		userService.addUser(
				new User("Sapir", "Hadad", "test3@test.com", LocalDate.of(1990, 7, 23), LocalDate.of(2022, 6, 6), 0));

		userService.getAllUsers().stream().forEach(System.out::println);

	}

	public void seedEvents() throws DaoException {
		
		List<User> users = userService.getAllUsers();

		eventService.addEventToDB(new Event(users.get(1).getUserId(), "wedding", 0, LocalDate.of(2022, 8, 7), LocalDate.of(2022, 8, 7),
				LocalTime.of(20, 0), LocalTime.of(23, 30), "Troya", "Tomer getting married", EventRepeating.NONE, 0));

		eventService.addEventToDB(new Event(users.get(2).getUserId(), "shopping", 0, LocalDate.of(2022, 7, 7), LocalDate.of(2022, 7, 7),
				LocalTime.of(16, 0), LocalTime.of(18, 30), "Tel-Aviv", "buying equipment", EventRepeating.WEEKLY, 0));

		eventService.getAllEvents().stream().forEach(System.out::println);
	}
	
	public void seedNotifications() throws DaoException {
		List<Event> events = eventService.getAllEvents();
		
		notificationService.addNotificationToDB(new Notification( events.get(0).getEventId(), 
				"Remember take the check", 90, ChronoUnit.MINUTES, 0));
		
		notificationService.addNotificationToDB(new Notification( events.get(1).getEventId(), 
				"Remember your wallet!", 15, ChronoUnit.MINUTES, 0));
		
		notificationService.addNotificationToDB(new Notification( events.get(1).getEventId(), 
				"Wash the car after", 30, ChronoUnit.MINUTES, 0));
		
		notificationService.getAllNotifications().stream().forEach(System.out::println);
		
	}
		
}
