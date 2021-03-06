package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.daos.interfaces.EventUserDao;
import ajbc.doodle.calendar.entities.EventUser;

@SuppressWarnings("unchecked")
@Repository("htEventUserDao")
public class HTEventUserDao implements EventUserDao {

	@Autowired
	private HibernateTemplate template;

	// CRUD operations
	@Override
	public void addEventToUser(EventUser eventUser) throws DaoException {
		template.persist(eventUser);
	}

	@Override
	public void updateUserEvent(EventUser eventUser) throws DaoException {
		template.merge(eventUser);
	}

	@Override
	public void deleteUserEvent(EventUser eventUser) throws DaoException {
		template.delete(eventUser);
	}

	// QUERIES operations
	@Override
	public EventUser getEventForUser(EventUser eventUser) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(EventUser.class);
		criteria.add(Restrictions.eq("eventId", eventUser.getEventId()));
		criteria.add(Restrictions.eq("userId", eventUser.getUserId()));

		List<EventUser> res = (List<EventUser>) template.findByCriteria(criteria);
		if (res.isEmpty())
			throw new DaoException("No such event in the DB");

		return res.get(0);
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
	public List<EventUser> getUsersByEventId(Integer eventId) throws DaoException {
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
