package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
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
	public List<Event> getAllEvents() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (List<Event>) template.findByCriteria(criteria);
	}

	@Override
	public void deleteAllEvents() throws DaoException {
		template.deleteAll(getAllEvents());
	}


	
	
}
