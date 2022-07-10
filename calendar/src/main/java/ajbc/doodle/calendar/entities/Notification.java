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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString
@Entity
@Table(name = "notifications")
public class Notification {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer notificationId;
	
	@Column(insertable = false, updatable = false)
	private Integer userId;
	
	@Column(insertable = false, updatable = false)
	private Integer eventId;
	
	private String title;
	private Integer timeToAlertBefore;
	
	@Enumerated(EnumType.STRING)
	private ChronoUnit units;
	
	private Integer discontinued;


	@JsonIgnore
	@ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
	@JoinColumns({@JoinColumn(name="eventId"), @JoinColumn(name="userId")})
	private EventUser eventUser;
	

	public Notification(Integer userId, Integer eventId, String title, Integer timeToAlertBefore, ChronoUnit units,
			Integer discontinued) {
		this.userId = userId;
		this.eventId = eventId;
		this.title = title;
		this.timeToAlertBefore = timeToAlertBefore;
		this.units = units;
		this.discontinued = discontinued;
	}


	public void setEventUser(EventUser eventUser) {
		this.eventUser = eventUser;
		this.userId = eventUser.getUserId();
		this.eventId = eventUser.getEventId();
	}
	
	

	@Override
	public String toString() {
		return "Notification [notificationId=" + notificationId + ", userId=" + userId + ", eventId=" + eventId
				+ ", title=" + title + ", timeToAlertBefore=" + timeToAlertBefore + ", units=" + units
				+ ", discontinued=" + discontinued + "]";
	}

	
}
