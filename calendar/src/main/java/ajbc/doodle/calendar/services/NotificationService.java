package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.NotificationDao;
import ajbc.doodle.calendar.entities.Notification;

@Service
public class NotificationService {

	@Autowired
	@Qualifier("htNotificationDao")
	NotificationDao dao;

	public void addNotificationToDB(Notification notification) throws DaoException {
		dao.addNotification(notification);
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

}
