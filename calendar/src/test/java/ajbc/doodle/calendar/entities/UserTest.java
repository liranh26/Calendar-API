package ajbc.doodle.calendar.entities;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ajbc.doodle.calendar.enums.EventRepeating;

/**
*@author Liran Hadad
*/

@TestInstance(Lifecycle.PER_CLASS)
public class UserTest {
		
		private static Integer discontinued;
		private static final String firstName, lastName , email;
		private static LocalDate birthDate, joinDate;
	
		static {
			firstName = "Liran";
			lastName = "Hadad";
			email ="test@test.com";
			birthDate =  LocalDate.of(1990, 2, 26);
			joinDate = LocalDate.of(2022, 1, 1);
			discontinued = 0;
		}
		
		User user;
		User emptyUser;
		static String message = "Tests for User class";
		
		
		@BeforeAll
		static void startMessage() {
			System.out.println(message);
		}
		
		UserTest(){
			user = new User("Liran", "Hadad", "test@test.com", LocalDate.of(1990, 2, 26), LocalDate.of(2022, 1, 1), 0);
			emptyUser = new User();
		}
		

		@Test
		@DisplayName("checks custom constructor")
		void checkDefaultConstructor() {
			assertEquals(firstName, user.getFirstName());
			assertEquals(lastName, user.getLastName());
			assertEquals(email, user.getEmail());
			assertEquals(birthDate, user.getBirthDate());
			assertEquals(joinDate, user.getJoinDate());
			assertEquals(discontinued, user.getDiscontinued());
			
			assertEquals(null, user.getEndpoint());
		}
	
		@Test
		@DisplayName("checks firstName setter")
		void checkFirstNameSetter() {
			emptyUser.setFirstName("sapir");
			assertEquals("sapir", emptyUser.getFirstName());
		}
		
		@Test
		@DisplayName("checks lastName setter")
		void checkLastNameSetter() {
			emptyUser.setLastName("hadad");
			assertEquals("hadad", emptyUser.getLastName());
		}
		
		@Test
		@DisplayName("checks email setter")
		void checkEmailSetter() {
			emptyUser.setEmail("sapir@test.com");
			assertEquals("sapir@test.com", emptyUser.getEmail());
		}
		
		@Test
		@DisplayName("checks birthdate setter")
		void checkBirthdateSetter() {
			emptyUser.setBirthDate(LocalDate.of(1992, 7, 23));
			assertEquals(LocalDate.of(1992, 7, 23), emptyUser.getBirthDate());
		}

		@Test
		@DisplayName("checks join date setter")
		void checkJoinDateSetter() {
			emptyUser.setJoinDate(LocalDate.of(2020, 1, 1));
			assertEquals(LocalDate.of(2020, 1, 1), emptyUser.getJoinDate());
		}
		
		@Test
		@DisplayName("checks discontinued setter")
		void checkDiscontinuedSetter() {
			emptyUser.setDiscontinued(1);
			assertEquals(1, emptyUser.getDiscontinued());
		}
		
		@Test
		@DisplayName("checks adding event")
		void checkAddEvent() {
			Event event = new Event(1,"shopping", 0, LocalDateTime.of(2022, 8, 8, 16, 0),
					LocalDateTime.of(2022, 8, 8, 18, 30), "Tel-Aviv", "buying equipment",
					EventRepeating.WEEKLY, 0);
					
			emptyUser.addEvent(event);
			assertTrue(emptyUser.getEvents().contains(event));
		}
		
}

