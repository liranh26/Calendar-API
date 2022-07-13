package ajbc.doodle.calendar.daos.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Notification;

@Transactional(rollbackFor = { DaoException.class }, readOnly = true)
public interface NotificationDao {

	// CRUD operations
	@Transactional(readOnly = false)
	public default void addNotification(Notification notification) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void updateNotification(Notification notification) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default Notification getNotification(Integer notificationId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void deleteNotification(Notification notification) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	// QUERIES
	public default List<Notification> getAllNotifications() throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	public default List<Notification> getNotificationsByEvent(Integer eventId) throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	public default List<Notification> getNotificationsByUser(Integer userId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default List<Notification> getDiscontinuedNotifications() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default long countNotifications() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	@Transactional(readOnly = false)
	public default void deleteAllNotifications() throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	@Transactional(readOnly = false)
	public default void addListNotifications(List<Notification> notifications) throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
}