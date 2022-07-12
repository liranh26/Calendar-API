package ajbc.doodle.calendar.daos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.daos.interfaces.EventDao;
import ajbc.doodle.calendar.entities.Event;

@SuppressWarnings("unchecked")
@Repository("htEventDao")
public class HTEventDao implements EventDao {

	@Autowired
	private HibernateTemplate template;
	
	@Override
	public void addEvent(Event event) throws DaoException {
		template.persist(event);
	}

	@Override
	public Event getEvent(Integer eventId) throws DaoException {
		Event event = template.get(Event.class, eventId);
		if (event == null)
			throw new DaoException("No such event in the DB");
		return event;
	}
	
	@Override
	public List<Event> getEventsForUser(Integer userId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class);
		criteria.add(Restrictions.eq("eventOwnerId", userId));
		return (List<Event>) template.findByCriteria(criteria);
	}
	

	@Override
	public void updateEvent(Event event) throws DaoException {
		template.merge(event);
	}

	@Override
	public List<Event> getAllEvents() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (List<Event>) template.findByCriteria(criteria);
	}

	@Override
	public void deleteAllEvents() throws DaoException {
		template.deleteAll(getAllEvents());
	}

	@Override
	public void deleteEvent(Event event) throws DaoException {
		template.delete(event);
	}
	
	
}
