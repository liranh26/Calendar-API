package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.daos.interfaces.EventDao;
import ajbc.doodle.calendar.daos.interfaces.EventUserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.EventUser;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;

@SuppressWarnings("unchecked")
@Repository("htEventUserDao")
public class HTEventUserDao implements EventUserDao {

	@Autowired
	private HibernateTemplate template;

	@Override
	public void addEventToUser(EventUser eventUser) throws DaoException {
		template.persist(eventUser);
	}

	@Override
	public void updateUserInEvent(EventUser eventUser, Integer updatedId) throws DaoException {
		template.merge(eventUser);
	}


	@Override
	public void deleteUserFromEvent(EventUser eventUser) throws DaoException {
		template.delete(eventUser);
	}

	@Override
	public List<EventUser> getAllUsersForEvent(EventUser eventUser) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(EventUser.class);
		criteria.add(Restrictions.eq("eventId", eventUser.getEventId()));
		return (List<EventUser>) template.findByCriteria(criteria);
	}
	
	@Override
	public List<EventUser> getAllEventsAndUsers() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(EventUser.class);
		return (List<EventUser>) template.findByCriteria(criteria);
	}

	@Override
	public long countEvents() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(EventUser.class);
		criteria.setProjection(Projections.rowCount());
		return (long) template.findByCriteria(criteria).get(0);
	}

	@Override
	public void deleteAllEventUsers() throws DaoException {
		template.deleteAll(getAllEventsAndUsers());
	}

	@Override
	public List<EventUser> getEventsForUser(Integer eventId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(EventUser.class);
		criteria.add(Restrictions.eq("eventId", eventId));
		return (List<EventUser>) template.findByCriteria(criteria);
	}

	@Override
	public List<EventUser> getEventsByUserId(Integer userId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(EventUser.class);
		criteria.add(Restrictions.eq("userId", userId));
		return (List<EventUser>) template.findByCriteria(criteria);
	}

	

	
	
}
