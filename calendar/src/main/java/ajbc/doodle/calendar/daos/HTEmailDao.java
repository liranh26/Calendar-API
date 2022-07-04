package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.entities.Category;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.Product;



@SuppressWarnings("unchecked")
@Component(value = "emailhtDao")
public class HTEmailDao implements EmailDao {

	// getting hibernate
	@Autowired
	private HibernateTemplate template;

	@Override
	public void addEmail(User email) throws DaoException {
		// open session , connection to db
		template.persist(email);
		// close session

	}

	@Override
	public void updateEmail(User email) throws DaoException {
		template.merge(email);
	}

	@Override
	public User getEmail(Integer emailId) throws DaoException {
		User prod = template.get(User.class, emailId);
		if (prod == null)
			throw new DaoException("No such product in the DB");
		return prod;
	}

	@Override
	public void deleteEmail(Integer productId) throws DaoException {
		User product = getEmail(productId);
		product.setDiscontinued(1);
		updateEmail(product);
	}

	@Override
	public List<User> getAllEmails() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		return (List<User>) template.findByCriteria(criteria);
	}

//	@Override
//	public List<Product> getProductsByPriceRange(Double min, Double max) throws DaoException {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
//		Criterion criterion = Restrictions.between("unitPrice", min, max);
//		criteria.add(criterion);
//		return (List<Product>) template.findByCriteria(criteria);
//	}
//
//	@Override
//	public List<Product> getProductsInCategory(Integer categoryId) throws DaoException {
//
//		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
//		criteria.add(Restrictions.eq("categoryId", categoryId));
//		return (List<Product>) template.findByCriteria(criteria);
//	}
//
//	@Override
//	public List<Product> getProductsNotInStock() throws DaoException {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
//		criteria.add(Restrictions.eq("unitsInStock", 0));
//		return (List<Product>) template.findByCriteria(criteria);
//	}
//
//	@Override
//	public List<Product> getProductsOnOrder() throws DaoException {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
//		criteria.add(Restrictions.gt("unitsOnOrder", 0));
//		return (List<Product>) template.findByCriteria(criteria);
//	}
//
//	@Override
//	public List<Product> getDiscontinuedProducts() throws DaoException {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
//		criteria.add(Restrictions.eq("discontinued", 1));
//		return (List<Product>) template.findByCriteria(criteria);
//	}
//
//	@Override
//	public long count() throws DaoException {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
//		criteria.setProjection(Projections.rowCount());
//		return (long) template.findByCriteria(criteria).get(0);
//	}
//
//	@Override
//	public void deleteCategoryInProducts(Integer categoryId) throws DaoException {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
//		criteria.add(Restrictions.eq("categoryId", categoryId));
//		List<Product> products = (List<Product>) template.findByCriteria(criteria);
//		for (Product product : products) {
//			product.setDiscontinued(1);
//			product.setCategory(null);
//			System.out.println(product);
//			template.merge(product);
//		}
//
//	}
//
//
//	@Override
//	public void deleteSupplierInProducts(Integer supplierId) throws DaoException {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
//		criteria.add(Restrictions.eq("supplierId", supplierId));
//		List<Product> products = (List<Product>) template.findByCriteria(criteria);
//		for (Product product : products) {
//			product.setDiscontinued(1);
//			product.setSupplier(null);
//			System.out.println(product);
//			template.merge(product);
//		}
//	}
//
//	
//	@Override
//	public Category getProductCategory(Integer productId, Integer categoryId) throws DaoException {
//		Product product = getProduct(productId);
//		Category category = product.getCategory();
//		return category;
//	}

}
