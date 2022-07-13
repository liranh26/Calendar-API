package ajbc.doodle.calendar.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
		
		private static boolean discontinued;
		private static final String firstName, lastName , email, demoString;
		private static LocalDate birthDate, joinDate;
	
		static {
			firstName = "Liran";
			lastName = "Hadad";
			email ="test@test.com";
			birthDate =  LocalDate.of(1990, 2, 26);
			joinDate = LocalDate.of(2022, 1, 1);
			discontinued = true;
			demoString = "demo";
		}
		
		User user;
		User emptyUser;
		static String message = "Tests for User class";
		
		UserTest(){
			user = new User("Liran", "Hadad", "test@test.com", LocalDate.of(1990, 2, 26), LocalDate.of(2022, 1, 1));
			emptyUser = new User();
		}
		

		
		
		@Test
		@DisplayName("checks first name getter")
		void checkFirstNameGetter() {
			assertEquals(firstName, user.getFirstName());
		}
		
		@Test
		@DisplayName("checks last name getter")
		void checkLastNameGetter() {
			assertEquals(lastName, user.getLastName());
		}
		
		@Test
		@DisplayName("checks email getter")
		void checkEmailGetter() {
			assertEquals(email, user.getEmail());
		}
		
		@Test
		@DisplayName("checks birthdate getter")
		void checkBirthDateGetter() {
			assertEquals(LocalDate.of(1990, 2, 26), user.getBirthDate());
		}
		
		@Test
		@DisplayName("checks join date getter")
		void checkJoinDateGetter() {
			assertEquals(LocalDate.of(2022, 1, 1), user.getJoinDate());
		}
		
		@Test
		@DisplayName("checks discontinued getter")
		void checkDiscontinuedGetter() {
			assertFalse(user.isDiscontinued());
		}
		
		@Test
		@DisplayName("checks events getter")
		void checkEventsGetter() {
			assertTrue(user.getEvents().isEmpty());
		}
		
		@Test
		@DisplayName("checks end point getter")
		void checkEndPointGetter() {
			assertTrue(user.getEndpoint() == null);
		}
		
		@Test
		@DisplayName("checks browser public key p256dh getter")
		void checkP256dhGetter() {
			assertTrue(user.getP256dh() == null);
		}
		
		@Test
		@DisplayName("checks browser auth key p256dh getter")
		void checkAuthGetter() {
			assertTrue(user.getAuth() == null);
		}
		
		@BeforeAll
		@DisplayName("checks custom constructor")
		void checkDefaultConstructor() {
			assertEquals(firstName, user.getFirstName());
			assertEquals(lastName, user.getLastName());
			assertEquals(email, user.getEmail());
			assertEquals(birthDate, user.getBirthDate());
			assertEquals(joinDate, user.getJoinDate());
			
			assertEquals(null, user.getEndpoint());
		}
	
		@Test
		@DisplayName("checks firstName setter")
		void checkFirstNameSetter() {
			emptyUser.setFirstName(firstName);
			assertEquals(firstName, emptyUser.getFirstName());
		}
		
		@Test
		@DisplayName("checks lastName setter")
		void checkLastNameSetter() {
			emptyUser.setLastName(lastName);
			assertEquals(lastName, emptyUser.getLastName());
		}
		
		@Test
		@DisplayName("checks email setter")
		void checkEmailSetter() {
			emptyUser.setEmail(email);
			assertEquals(email, emptyUser.getEmail());
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
			emptyUser.setDiscontinued(discontinued);
			assertTrue(emptyUser.isDiscontinued());
		}
		
		@Test
		@DisplayName("checks end point setter")
		void checkEndPointSetter() {
			user.setEndpoint(demoString);
			assertTrue(demoString.equals(user.getEndpoint()));
		}
		
		@Test
		@DisplayName("checks browser public key p256dh setter")
		void checkP256dhSetter() {
			user.setP256dh(demoString);
			assertTrue(demoString.equals(user.getP256dh()));
		}
		
		@Test
		@DisplayName("checks browser auth key auth setter")
		void checkAuthSetter() {
			user.setAuth(demoString);
			assertTrue(demoString.equals(user.getAuth()));
		}
		
		
		@Test
		@DisplayName("checks adding event")
		void checkAddEvent() {
			Event event = new Event(1,"shopping", 0, LocalDateTime.of(2022, 8, 8, 16, 0),
					LocalDateTime.of(2022, 8, 8, 18, 30), "Tel-Aviv", "buying equipment",
					EventRepeating.WEEKLY);
					
			emptyUser.addEvent(event);
			assertTrue(emptyUser.getEvents().contains(event));
		}
		
}

