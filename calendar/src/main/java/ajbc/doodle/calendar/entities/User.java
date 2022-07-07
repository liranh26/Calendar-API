package ajbc.doodle.calendar.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
	
	private String firstName;
	private String lastName;
	private String email;
	private LocalDate birthDate;
	private LocalDate joinDate;
	private Integer discontinued;

	
	@ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
	@JoinTable(name = "Event_users", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "eventId"))
	List<Event> events = new ArrayList<Event>();
	
	
	public User(String firstName, String lastName, String email, LocalDate birthDate, LocalDate joinDate,
			Integer discontinued) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.birthDate = birthDate;
		this.joinDate = joinDate;
		this.discontinued = discontinued;
//		this.events = events;
	}
}
