package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.daos.interfaces.NotificationDao;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;

@SuppressWarnings("unchecked")
@Repository("htNotificationDao")
public class HTNotificationDao implements NotificationDao {

	@Autowired
	private HibernateTemplate template;

	@Override
	public void addNotification(Notification notification) throws DaoException {
		template.persist(notification);
	}

	@Override
	public void updateNotification(Notification notification) throws DaoException {
		template.merge(notification);
	}

	@Override
	public Notification getNotification(Integer notificationId) throws DaoException {
		Notification notification = template.get(Notification.class, notificationId);
		if (notification == null)
			throw new DaoException("No such product in the DB");
		return notification;
	}

	@Override
	public void deleteNotification(Integer notificationId) throws DaoException {	
		Notification notification = getNotification(notificationId);
		notification.setDiscontinued(1);
		updateNotification(notification);
	}

	@Override
	public List<Notification> getAllNotifications() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Notification.class);
		return (List<Notification>) template.findByCriteria(criteria);
	}
	
	@Override
	public void deleteAllNotifications() throws DataAccessException, DaoException {
		template.deleteAll(getAllNotifications());
	}


}
