package ajbc.doodle.calendar.entities;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

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
		@DisplayName("checks custom constuctor")
		void checkDefaultConstructor() {
			assertEquals(firstName, user.getFirstName());
			assertEquals(lastName, user.getLastName());
			assertEquals(email, user.getEmail());
			assertEquals(birthDate, user.getBirthDate());
			assertEquals(joinDate, user.getJoinDate());
			assertEquals(discontinued, user.getDiscontinued());
			
			assertEquals(null, user.getEndpoint());
		}
	
		
		
		
		
		
}
