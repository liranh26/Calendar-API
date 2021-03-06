package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
	private LocalDateTime alertTime;

	private boolean discontinued;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "userId"), @JoinColumn(name = "eventId") })
	private EventUser eventUser;

	public Notification(String title, Integer timeToAlertBefore, ChronoUnit units) {
		this.title = title;
		this.timeToAlertBefore = timeToAlertBefore;
		this.units = units;
		this.discontinued = false;
	}

	public void setEventUser(EventUser eventUser) {
		this.eventUser = eventUser;
		this.userId = eventUser.getUserId();
		this.eventId = eventUser.getEventId();
	}

	public void updateAlertTime(Integer timeToAlertBefore, ChronoUnit units, LocalDateTime alertTime) {
		this.timeToAlertBefore = timeToAlertBefore;
		this.units = units;
		this.alertTime = alertTime;
	}

	@Override
	public String toString() {
		return "Notification [notificationId=" + notificationId + ", userId=" + userId + ", eventId=" + eventId
				+ ", title=" + title + ", timeToAlertBefore=" + timeToAlertBefore + ", units=" + units + ", alertTime="
				+ alertTime + ", discontinued=" + discontinued + "]";
	}

}
