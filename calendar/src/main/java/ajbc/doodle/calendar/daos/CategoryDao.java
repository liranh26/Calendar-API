package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.Category;


@Repository
public interface CategoryDao {
	
	public List<Category> getAllCategory() throws DaoException;
}
