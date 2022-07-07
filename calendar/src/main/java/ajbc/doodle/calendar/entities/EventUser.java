package ajbc.doodle.calendar.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
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
@Table(name = "Event_Users")
public class EventUser implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private Integer eventId;
	@Id
	private Integer userId;

}

//@IdClass(EventUsersPK.class)