package ajbc.doodle.calendar.daos.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.EventUser;

@Transactional(rollbackFor = { DaoException.class }, readOnly = true)
public interface EventUserDao {

	// CRUD operations
	@Transactional(readOnly = false)
	public default void addEventToUser(EventUser eventUser) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void updateUserEvent(EventUser eventUser) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default EventUser getEventForUser(EventUser eventUser) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void deleteUserEvent(EventUser eventUser) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	// QUERIES
	public default List<EventUser> getAllEventsAndUsers() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default List<EventUser> getAllUsersForEvent(EventUser eventUser) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default long countEvents() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void deleteAllEventUsers() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default List<EventUser> getUsersByEventId(Integer eventId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default List<EventUser> getEventsByUserId(Integer userId) throws DaoException {
		throw new DaoException("Method not implemented");
	}
}