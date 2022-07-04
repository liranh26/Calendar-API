package ajbc.doodle.calendar.daos;


import java.util.List;


import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.Category;

@SuppressWarnings("unchecked")
@Repository("htCatDao")
public class HTCategoryDao implements CategoryDao {
	
	@Autowired
	private HibernateTemplate template;
	

	@Override
	public List<Category> getAllCategory() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Category.class);
		List<Category> catList = (List<Category>)template.findByCriteria(criteria);
		if(catList==null)
			throw new DaoException("No categories found in DB");
		return catList;
		

	}

	
}
