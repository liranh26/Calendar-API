package ajbc.doodle.calendar.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import ajbc.doodle.calendar.enums.EventRepeating;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
//@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer eventId;

	private Integer eventOwnerId;

	private String title;
	private Integer isAllDay;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private String address;
	private String description;

	@Enumerated(EnumType.STRING)
	private EventRepeating repeating;

	private Integer discontinued;  // TODO change to inactive and bit in db

	@JsonIgnore
	@OneToMany(mappedBy = "events", fetch = FetchType.EAGER)
	private Set<EventUser> guests = new HashSet<>();	
	
	
	@OneToMany( cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinColumn(name = "eventId")
	private Set<Notification> notifications = new HashSet<>();   


	
	public Event(Integer eventOwnerId, String title, Integer isAllDay, LocalDate startDate, LocalDate endDate,
			LocalTime startTime, LocalTime endTime, String address, String description, EventRepeating repeating,
			Integer discontinued) {
		this.eventOwnerId = eventOwnerId;
		this.title = title;
		this.isAllDay = isAllDay;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.address = address;
		this.description = description;
		this.repeating = repeating;
		this.discontinued = discontinued;

	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		return Objects.equals(address, other.address) && Objects.equals(description, other.description)
				&& Objects.equals(discontinued, other.discontinued) && Objects.equals(endDate, other.endDate)
				&& Objects.equals(endTime, other.endTime) && Objects.equals(eventId, other.eventId)
				&& Objects.equals(eventOwnerId, other.eventOwnerId) && Objects.equals(guests, other.guests)
				&& Objects.equals(isAllDay, other.isAllDay) && Objects.equals(notifications, other.notifications)
				&& repeating == other.repeating && Objects.equals(startDate, other.startDate)
				&& Objects.equals(startTime, other.startTime) && Objects.equals(title, other.title);
	}

	
	
}
