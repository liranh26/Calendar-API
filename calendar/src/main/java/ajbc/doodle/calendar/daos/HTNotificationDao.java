package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.daos.interfaces.NotificationDao;
import ajbc.doodle.calendar.entities.Event;
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
	

//	@Override
//	public void addListNotifications(List<Notification> notifications) throws DaoException {
//		//TODO
//	}


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
	public void deleteNotification(Notification notification) throws DaoException {	
		template.delete(notification);
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


	@Override
	public List<Notification> getNotificationsByEvent(Integer eventId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Notification.class);
		criteria.add(Restrictions.eq("eventId", eventId));
		return (List<Notification>) template.findByCriteria(criteria);
	}

	@Override
	public List<Notification> getNotificationsByUser(Integer userId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Notification.class);
		criteria.add(Restrictions.eq("userId", userId));
		return (List<Notification>) template.findByCriteria(criteria);
	}

}