package ajbc.doodle.calendar.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.EventUser;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.enums.EventRepeating;
import ajbc.doodle.calendar.services.EventService;
import ajbc.doodle.calendar.services.EventUserService;
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
	@Autowired
	private EventUserService eventUserService;
	@Autowired
	private JdbcTemplate jdbc;

	@EventListener
	public void seedDB(ContextRefreshedEvent event) {
		try {
			
			dropTables();
			createTables();
			
			seedUsers();
			seedEvents();
			seedNotifications();
//			seedEventUsers();
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public void createTables() {
		String query = "CREATE TABLE Users(\r\n"
				+ "userId int not null identity(1,1),\r\n"
				+ "firstName nvarchar(40),\r\n"
				+ "lastName nvarchar(40),\r\n"
				+ "email nvarchar(40) unique,\r\n"
				+ "birthdate date,\r\n"
				+ "joinDate date,\r\n"
				+ "discontinued int,\r\n"
				+ "primary key(userId)\r\n"
				+ ") ";
		jdbc.execute(query);
		
		query = "CREATE TABLE Events("
				+ "eventId int not null identity(1000,1),"
				+ "eventOwnerId int not null,"
				+ "title nvarchar(40),"
				+ "isAllDay BIT,"
				+ "startDate date,"
				+ "endDate date,"
				+ "startTime time,"
				+ "endTime time,"
				+ "address nvarchar(100),"
				+ "description nvarchar(100),"
				+ "repeating nvarchar(40),"
				+ "discontinued int,"
				+ "foreign key(eventOwnerId) references users(userId),"
				+ "primary key(eventId)"
				+ ") ";
		jdbc.execute(query);
		
		query = "CREATE TABLE Notifications(\r\n"
				+ "notificationId int not null identity(1,1),\r\n"
				+ "userId int not null, \r\n"
				+ "eventId int not null, \r\n"
				+ "title nvarchar(40),\r\n"
				+ "timeToAlertBefore int,\r\n"
				+ "units nvarchar(40),\r\n"
				+ "discontinued int,\r\n"
				+ "foreign key(userId) references users(userId),\r\n"
				+ "foreign key(eventId) references events(eventId),\r\n"
				+ "primary key(notificationId)\r\n"
				+ ") ";
		jdbc.execute(query);
		
		query = "CREATE TABLE Event_Users(\r\n"
				+ "eventId int not null, \r\n"
				+ "userId int not null,\r\n"
				+ "foreign key(eventId) references events(eventId),\r\n"
				+ "foreign key(userId) references users(userId)\r\n"
				+ ")";
		jdbc.execute(query);
		
	}
	
	public void dropTables() {
		
		String query = "drop table Event_Users";
		jdbc.execute(query);
		
		query = "drop table Notifications";
		jdbc.execute(query);
		
		query = "drop table Events";
		jdbc.execute(query);

		query = "drop table Users";
		jdbc.execute(query);
		
	}
	
	
	public void seedUsers() throws DaoException {

		userService.addUser(
				new User("Liran", "Hadad", "test@test.com", LocalDate.of(1990, 2, 26), LocalDate.of(2022, 1, 1), 0));
		userService.addUser(
				new User("Snir", "Hadad", "test2@test.com", LocalDate.of(1993, 7, 8), LocalDate.of(2022, 5, 5), 0));
		userService.addUser(
				new User("Sapir", "Hadad", "test3@test.com", LocalDate.of(1990, 7, 23), LocalDate.of(2022, 6, 6), 0));

//		userService.getAllUsers().stream().forEach(System.out::println);

	}

	public void seedEvents() throws DaoException {

		List<User> users = userService.getAllUsers();

		eventService.addEventToDB(new Event(users.get(1).getUserId(), "wedding", 0, LocalDate.of(2022, 8, 7),
				LocalDate.of(2022, 8, 7), LocalTime.of(20, 0), LocalTime.of(23, 30), "Troya", "Tomer getting married",
				EventRepeating.NONE, 0));

		eventService.addEventToDB(new Event(users.get(2).getUserId(), "shopping", 0, LocalDate.of(2022, 7, 7),
				LocalDate.of(2022, 7, 7), LocalTime.of(16, 0), LocalTime.of(18, 30), "Tel-Aviv", "buying equipment",
				EventRepeating.WEEKLY, 0));

	}

	public void seedNotifications() throws DaoException {
		List<User> users = userService.getAllUsers();
		List<Event> events = eventService.getAllEvents();
		
		Notification not = new Notification(users.get(1).getUserId(), events.get(0).getEventId(), "Remember take the check", 90, ChronoUnit.MINUTES, 0);
		not.setEvent(events.get(0));
		notificationService.addNotificationToDB(not, events.get(0));
		
		not = new Notification(users.get(2).getUserId(), events.get(1).getEventId(), "Remember your wallet!", 15, ChronoUnit.MINUTES, 0);
		not.setEvent(events.get(1));
		notificationService.addNotificationToDB(not, events.get(1));

//		notificationService.addNotificationToDB(
//				new Notification(users.get(1).getUserId(), events.get(1).getEventId(), "Wash the car after", 30, ChronoUnit.MINUTES, 0));

	}

	public void seedEventUsers() throws DaoException {
		List<Event> events = eventService.getAllEvents();
		List<User> users = userService.getAllUsers();

		userService.addUserToEvent(events.get(0).getEventId(), users.get(0).getUserId());
		userService.addUserToEvent(events.get(0).getEventId(), users.get(1).getUserId());
		userService.addUserToEvent(events.get(0).getEventId(), users.get(2).getUserId());
		userService.addUserToEvent(events.get(1).getEventId(), users.get(1).getUserId());
		userService.addUserToEvent(events.get(1).getEventId(), users.get(2).getUserId());
	

	}

}
