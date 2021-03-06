package ajbc.doodle.calendar.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
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
	UserDao userDao;
	
	
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
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public void createTables() {
		String query = "CREATE TABLE Users(\r\n"
				+ "userId int not null identity(1,1),\r\n"
				+ "firstName nvarchar(64),\r\n"
				+ "lastName nvarchar(64),\r\n"
				+ "email nvarchar(64) unique,\r\n"
				+ "birthdate date,\r\n"
				+ "joinDate date,\r\n"
				+ "discontinued bit,\r\n"
				+ "endpoint nvarchar(512),\r\n"
				+ "p256dh nvarchar(128),\r\n"
				+ "auth nvarchar(128),\r\n"
				+ "primary key(userId)\r\n"
				+ ") ";
		jdbc.execute(query);
		
		query = "CREATE TABLE Events("
				+ "eventId int not null identity(1000,1),"
				+ "eventOwnerId int not null,"
				+ "title nvarchar(40),"
				+ "isAllDay BIT,"
				+ "startTime datetime,"
				+ "endTime datetime,"
				+ "address nvarchar(100),"
				+ "description nvarchar(100),"
				+ "repeating nvarchar(40),"
				+ "discontinued bit,"
				+ "foreign key(eventOwnerId) references users(userId),"
				+ "primary key(eventId)"
				+ ") ";
		jdbc.execute(query);
		
		query = "CREATE TABLE Event_Users(\r\n"
				+ "userId int,\r\n"
				+ "eventId int, \r\n"
				+ "primary key(userId, eventId),"
				+ "foreign key(userId) references users(userId),\r\n"
				+ "foreign key(eventId) references events(eventId)\r\n"
				+ ")";
		jdbc.execute(query);
		
		query = "CREATE TABLE Notifications("
				+ "notificationId int not null identity(1,1),"
				+ "userId int not null,"
				+ "eventId int not null,"
				+ "title nvarchar(40),"
				+ "timeToAlertBefore int,"
				+ "units nvarchar(40),"
				+ "alertTime datetime,"
				+ "discontinued bit,"
				+ "foreign key(userId, eventId) references event_users(userId, eventId),"
				+ "primary key(notificationId)"
				+ ") ";
		jdbc.execute(query);
		

		
	}
	
	public void dropTables() {
		
		String query = "drop table Notifications";
		jdbc.execute(query);
		
		 query = "drop table Event_Users";
		jdbc.execute(query);
		
		query = "drop table Events";
		jdbc.execute(query);

		query = "drop table Users";
		jdbc.execute(query);
		
	}
	
	
	public void seedUsers() throws DaoException {

		userDao.addUser(new User("Liran", "Hadad", "test@test.com", LocalDate.of(1990, 2, 26), LocalDate.of(2022, 1, 1)));
		userDao.addUser(new User("Snir", "Hadad", "test2@test.com", LocalDate.of(1993, 7, 8), LocalDate.of(2022, 5, 5)));
		userDao.addUser(new User("Sapir", "Hadad", "test3@test.com", LocalDate.of(1990, 7, 23), LocalDate.of(2022, 6, 6)));

	}

	public void seedEvents() throws DaoException {

		List<User> users;
		users = userService.getAllUsers();
		
		Event event = new Event(users.get(0).getUserId(), "wedding", 0, LocalDateTime.of(2022, 8, 7, 20, 0),
				LocalDateTime.of(2022, 8, 7, 23, 30), "Troya", "Tomer getting married",
				EventRepeating.NONE);

		
		eventService.createEventForUser(users.get(0).getUserId(), event);
		
		List<Integer> guests = new ArrayList<Integer>();
		guests.add(users.get(1).getUserId());
		eventService.addGuestsToEvent(event, guests);
		

		
		Event event2 = new Event(users.get(1).getUserId(), "shopping", 0, LocalDateTime.of(2022, 8, 8, 16, 0),
				LocalDateTime.of(2022, 8, 8, 18, 30), "Tel-Aviv", "buying equipment",
				EventRepeating.WEEKLY);
		
		users = userService.getAllUsers();
		
		eventService.createEventForUser(users.get(1).getUserId(), event2);
		guests = new ArrayList<Integer>();
		guests.add(users.get(2).getUserId());
		eventService.addGuestsToEvent(event2, guests);

	}

	public void seedNotifications() throws DaoException {
		
		User user = userService.getUserById(1);
		Event event = eventService.getEventById(1000);

		EventUser eventUser = new EventUser(user.getUserId() , event.getEventId());
		eventUser = eventUserService.getEventUser(eventUser);
		
		Notification not = new Notification("Remember take the check", 90, ChronoUnit.MINUTES);
	
		not.setEventUser(eventUser);
		not.setAlertTime(event.getStartTime().minus(not.getTimeToAlertBefore(),not.getUnits()));
		
		notificationService.addNotificationToDB(not);
		
		eventUser.addNotifications(not);

		eventUserService.updateEventUser(eventUser);

		
		
		user = userService.getUserById(3);
		event = eventService.getEventById(1001);
		
		eventUser = new EventUser(user.getUserId() , event.getEventId());
		eventUser = eventUserService.getEventUser(eventUser);
		
		not = new Notification("Remember your wallet!", 15, ChronoUnit.MINUTES);

		not.setEventUser(eventUser);
		not.setAlertTime(event.getStartTime().minus(not.getTimeToAlertBefore(),not.getUnits()));
		
		notificationService.addNotificationToDB(not);
		
		eventUser.addNotifications(not);

		eventUserService.updateEventUser(eventUser);


	}


}
