package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.Category;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.Product;

@SuppressWarnings("unchecked")
@Repository("htUserDao")
public class HibernateTemplateUserDao implements UserDao {

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
		User prod = template.get(User.class, userId);
		if (prod == null)
			throw new DaoException("No such product in the DB");
		return prod;
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


}
