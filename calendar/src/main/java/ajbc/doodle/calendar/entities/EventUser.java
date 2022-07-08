package ajbc.doodle.calendar.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	private Integer eventId;
	
	@Id
	private Integer userId;

	
//	@ManyToOne
//	@JoinColumn(name = "userId")
//	private User user;
//	
//	@ManyToOne
//	@JoinColumn(name = "eventId")
//	private Event event;
	
}

