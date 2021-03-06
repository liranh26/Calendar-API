package ajbc.doodle.calendar.daos.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Event;

@Transactional(rollbackFor = { DaoException.class }, readOnly = true)
public interface EventDao {

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
	public default void deleteEvent(Event event) throws DaoException {
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

	public default List<Event> getEventsForUser(Integer userId) throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	
}