package ajbc.doodle.calendar.daos.interfaces;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Event;

@Transactional(rollbackFor = { DaoException.class }, readOnly = true)
public interface EventDao {

//	extends JpaRepository<Event, Integer>

	// CRUD operations
	@Transactional(readOnly = false)
	public default void addEvent(Event event) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void updateEvent(Event event) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default Event getEvent(Integer eventId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void deleteEvent(Integer eventId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	// QUERIES
	@Transactional(readOnly = false)
	public default List<Event> getAllEvents() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default List<Event> getDiscontinuedEvents() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default long countEvents() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void deleteAllEvents() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default List<Event> getEventsOfUserInRange(LocalDate startDate, 
			LocalDate endDate, LocalTime startTime, LocalTime endTime) throws DaoException {
		throw new DaoException("Method not implemented");
	}

}