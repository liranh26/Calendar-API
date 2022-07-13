package ajbc.doodle.calendar.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.NotificationDao;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.webpush.Subscription;

@Service
public class NotificationManagerService {

	@Autowired
	@Qualifier("htUserDao")
	private UserDao userDao;

	@Autowired
	@Qualifier("htNotificationDao")
	NotificationDao notificationDao;

	public User getUserByNotification(Notification notification) throws DaoException {
		return userDao.getUser(notification.getUserId());
	}

	public Subscription getSubscriptionByUserId(Integer userId) throws DaoException {
		return userDao.getSubscriptionByUserId(userId);
	}

	public boolean isUserLogged(User user) throws DaoException {
		return user.getEndpoint() != null;
	}

	public void setNotificationsInactive(Notification notification) throws DaoException {
		notification.setDiscontinued(true);
		notificationDao.updateNotification(notification);
	}

}
