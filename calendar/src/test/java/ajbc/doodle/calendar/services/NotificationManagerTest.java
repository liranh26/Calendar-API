package ajbc.doodle.calendar.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ajbc.doodle.calendar.entities.Notification;

@TestInstance(Lifecycle.PER_METHOD)
public class NotificationManagerTest {

	NotificationManager manager;
	Notification testNotification1;
	Notification testNotification2;

	NotificationManagerTest() {
		manager = new NotificationManager();
		testNotification1 = new Notification("Remember take the check", 90, ChronoUnit.MINUTES);
		testNotification1.setAlertTime(LocalDateTime.of(2022, 8, 8, 18, 30));
		testNotification1.setNotificationId(1);
		testNotification2 = new Notification("Remember take the check", 90, ChronoUnit.MINUTES);
		testNotification2.setAlertTime(LocalDateTime.of(2022, 6, 6, 10, 00));
		testNotification2.setNotificationId(2);
	}

	// insert default notifiication for tests
	@BeforeEach
	void insertIntoQueue() {
		manager.insertNotificationToQueue(testNotification1);
	}

	@Test
	@DisplayName("checks queue is not empty")
	void isPriorityBlockingQueueInitialized() {
		assertFalse(manager.notificationsQueue.isEmpty());
	}

	@Test
	@DisplayName("checks insert test notification")
	void checkInsertList() {
		manager.insertNotificationToQueue(testNotification2);
		assertTrue(manager.notificationsQueue.contains(testNotification2));
	}

	@Test
	@DisplayName("check comparator sorting notification in queue")
	void comparatorSortingNotifications() {
		// insert by LocalDateTime.of(2022, 8, 8, 18, 30)
		assertEquals(testNotification1, manager.notificationsQueue.peek());

		// insert by LocalDateTime.of(2022, 6, 6, 10, 00)
		manager.insertNotificationToQueue(testNotification2);

		assertEquals(testNotification2, manager.notificationsQueue.peek());
	}

	@Test
	@DisplayName("check update notification in queue")
	void updateNotification() {
		String newTitle = "this is a test";
		testNotification1.setTitle(newTitle);
		manager.updateNotificationInQueue(testNotification1);
		assertTrue(manager.notificationsQueue.contains(testNotification1));
	}

	@Test
	@DisplayName("checks delay time")
	void checkDelayTime() {
		assertTrue(manager.getDelayTime(testNotification2) == 0);
		assertTrue(manager.getDelayTime(testNotification1) > 0);
	}

	@Test
	@AfterEach
	@DisplayName("delete default notifiication inserted")
	void delete1() {
		manager.deleteNotificationQueue(testNotification1);
		assertFalse(manager.notificationsQueue.contains(testNotification1));
	}

}
