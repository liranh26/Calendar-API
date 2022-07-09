package ajbc.doodle.calendar.daos.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;

@Transactional(rollbackFor = { DaoException.class }, readOnly = true)
public interface UserDao {

	// CRUD operations
	@Transactional(readOnly = false)
	public default void addUser(User user) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void updateUser(User user) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default User getUser(Integer userId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default List<Event> getUserEvents(Integer userId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void deleteUser(Integer userId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	// QUERIES
	@Transactional(readOnly = false)
	public default List<User> getAllUsers() throws DaoException {
		throw new DaoException("Method not implemented");
	}
	

	public default List<User> getDiscontinuedUsers() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default long countUsers() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void deleteAllUsers() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default boolean doesEmailExist(String email) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default User getUserByEmail(String email) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default  List<User> getUsersByEventId(String eventId) throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	public default boolean checkEndPointRegistration(String endpoint)throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
}
