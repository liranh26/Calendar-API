package ajbc.doodle.calendar.entities;

import java.time.temporal.ChronoUnit;

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
import javax.persistence.ManyToOne;
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
@Table(name = "notifications")
public class Notification {

	public Notification(Integer userId, Integer eventId, String title, Integer timeToAlertBefore, ChronoUnit units,
			Integer discontinued) {
		this.userId = userId;
		this.eventId = eventId;
		this.title = title;
		this.timeToAlertBefore = timeToAlertBefore;
		this.units = units;
		this.discontinued = discontinued;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Integer notificationId;
	
//	@JsonIgnore
//	@Column(insertable = false, updatable = false)
	private Integer userId;
	
//	@JsonIgnore
//	@Column(insertable = false, updatable = false)
	private Integer eventId;
	
	private String title;
	private Integer timeToAlertBefore;
	
	@Enumerated(EnumType.STRING)
	private ChronoUnit units;
	
	private Integer discontinued;

//	@ManyToOne
//	@JoinColumn(name="eventId")
//	@JsonIgnore
//	private Event event;
	
	
	
	

	
	
}
