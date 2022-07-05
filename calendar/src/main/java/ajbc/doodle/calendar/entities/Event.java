package ajbc.doodle.calendar.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "events")
public class Event {

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
	private Integer discontinued;

}
