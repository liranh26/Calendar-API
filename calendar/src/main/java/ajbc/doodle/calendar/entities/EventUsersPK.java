package ajbc.doodle.calendar.entities;
import java.util.Objects;

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
public class EventUsersPK {

	private Integer eventId;
	
	private Integer userId;

	@Override
	public int hashCode() {
		return Objects.hash(eventId, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventUsersPK other = (EventUsersPK) obj;
		return Objects.equals(eventId, other.eventId) && Objects.equals(userId, other.userId);
	}
	
}
