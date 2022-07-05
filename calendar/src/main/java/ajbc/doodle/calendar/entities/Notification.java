package ajbc.doodle.calendar.entities;

import java.time.temporal.ChronoUnit;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "notifications")
public class Notification {



	public Notification(Integer eventId, String title, Integer timeToAlertBefore, ChronoUnit units,
			Integer discontinued) {
		this.eventId = eventId;
		this.title = title;
		this.timeToAlertBefore = timeToAlertBefore;
		this.units = units;
		this.discontinued = discontinued;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private Integer eventId;
	private String title;
	private Integer timeToAlertBefore;
	@Enumerated(EnumType.STRING)
	private ChronoUnit units;
	private Integer discontinued;

}
