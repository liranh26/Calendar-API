package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonInclude(Include.NON_NULL)
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer eventId;

	@Column(insertable = false, updatable = false)
	private Integer eventOwnerId;

	@OneToOne
	@JoinColumn(name = "eventOwnerId")
	private User owner;

	private String title;
	private Integer isAllDay;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String address;
	private String description;

	@Enumerated(EnumType.STRING)
	private EventRepeating repeating;

	private boolean discontinued;

	@JsonIgnore
	@ManyToMany(mappedBy = "events")
	private List<User> guests = new ArrayList<>();

	public Event(Integer eventOwnerId, String title, Integer isAllDay, LocalDateTime startTime, LocalDateTime endTime,
			String address, String description, EventRepeating repeating) {
		this.eventOwnerId = eventOwnerId;
		this.title = title;
		this.isAllDay = isAllDay;
		this.startTime = startTime;
		this.endTime = endTime;
		this.address = address;
		this.description = description;
		this.repeating = repeating;
		this.discontinued = false;
	}

	public void addGuests(User... users) {
		for (User user : users) {
			user.addEvent(this);
		}
	}

}
