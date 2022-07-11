package ajbc.doodle.calendar.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@EqualsAndHashCode
@Table(name = "Event_Users")
public class EventUser implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private Integer userId;
	
	@Id
	private Integer eventId;
	
	
	@OneToMany(mappedBy = "eventUser", fetch = FetchType.EAGER)
	private Set<Notification> notifications = new HashSet<>();
	
	
	public void addNotifications(Notification... notifications) {
		for (Notification notification : notifications) {
			this.notifications.add(notification);
		}
	}


	public EventUser(Integer userId , Integer eventId) {
		this.eventId = eventId;
		this.userId = userId;
	}
	
	
	
}

