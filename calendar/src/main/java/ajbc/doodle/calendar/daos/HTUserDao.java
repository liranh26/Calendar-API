package ajbc.doodle.calendar.daos;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;

@SuppressWarnings("unchecked")
@Repository("htUserDao")
public class HTUserDao implements UserDao {

	@Autowired
	private HibernateTemplate template;

	@Override
	public void addUser(User user) throws DaoException {
		template.persist(user);
	}

	@Override
	public void updateUser(User user) throws DaoException {
		template.merge(user);
	}

	
	
	@Override
	public User getUser(Integer userId) throws DaoException {
		User user = template.get(User.class, userId);
		if (user == null)
			throw new DaoException("No such product in the DB");
		return user;
	}

	@Override
	public void deleteUser(Integer userId) throws DaoException {	
		User product = getUser(userId);
		product.setDiscontinued(1);
		updateUser(product);
	}

	
	@Override
	public List<User> getAllUsers() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		return (List<User>) template.findByCriteria(criteria);
	}
	
	@Override
	public void deleteAllUsers() throws DataAccessException, DaoException {
		template.deleteAll(getAllUsers());
	}

	@Override
	public boolean doesEmailExist(String email) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		criteria.add(Restrictions.eq("email", email));
		List<User> user = (List<User>) template.findByCriteria(criteria);
		System.out.println(user.size());
		return user.size() > 0;
	}

//	@Override
//	public List<Event> getUserEvents(Integer userId) throws DaoException {
//		User user = getUser(userId);
//		return user.getEvents();
//	}
	


}
