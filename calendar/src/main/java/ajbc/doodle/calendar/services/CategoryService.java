package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.CategoryDao;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Category;


@Service
public class CategoryService {

	@Autowired
	@Qualifier("htCatDao")
	CategoryDao catDao;

	public List<Category> getAllCategory() throws DaoException{
		return catDao.getAllCategory();
	}
}
