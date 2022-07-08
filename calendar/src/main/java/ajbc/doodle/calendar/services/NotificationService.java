package ajbc.doodle.calendar.services;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.NotificationDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;

@Service
public class NotificationService {

	@Autowired
	@Qualifier("htNotificationDao")
	NotificationDao dao;

	public void addNotificationToDB(Notification notification, Event event) throws DaoException {
		notification.setEvent(event);
		dao.addNotification(notification);
	}

	public Notification createDefaultNotification(Event event) throws DaoException {

		Notification not = new Notification(event.getEventOwnerId(), event.getEventId(), event.getTitle(), 0,
				ChronoUnit.SECONDS, 0);

		addNotificationToDB(not, event);
		return not;
	}

	public List<Notification> getAllNotifications() throws DaoException {
		return dao.getAllNotifications();
	}

	public void deleteAllNotifications() throws DaoException {
		dao.deleteAllNotifications();
	}

	public Notification getNotificationById(Integer id) throws DaoException {
		return dao.getNotification(id);
	}

	public void updateNotification(Notification notification) throws DaoException {
		dao.updateNotification(notification);

	}

	public void softDelete(Notification notification) throws DaoException {
		notification.setDiscontinued(1);
		dao.updateNotification(notification);
	}

	public void hardDelete(Notification notification) throws DaoException {
		dao.deleteNotification(notification);
		
	}

}
