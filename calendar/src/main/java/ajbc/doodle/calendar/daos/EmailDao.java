package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.User;

@Transactional(rollbackFor = {DaoException.class}, readOnly = true)
public interface EmailDao {

	//CRUD operations
	@Transactional(readOnly = false)
	public default void addEmail(User email) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void updateEmail(User email) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default User getEmail(Integer emailId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void deleteEmail(Integer emailId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	//Queries
	
	// QUERIES
		public default List<User> getAllEmails() throws DaoException {
			throw new DaoException("Method not implemented");
		}

		public default List<User> getDiscontinuedEmails() throws DaoException {
			throw new DaoException("Method not implemented");
		}

		public default long countEmails() throws DaoException {
			throw new DaoException("Method not implemented");
		}

}