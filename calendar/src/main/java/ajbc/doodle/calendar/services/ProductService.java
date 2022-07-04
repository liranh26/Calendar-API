package ajbc.doodle.calendar.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.ProductDao;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Product;


@RequestMapping("/products")
@RestController
public class ProductService {
	
	@Autowired
	ProductDao dao;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Product>> getProductsByRange(@RequestParam Map<String, String> map) throws DaoException {
		List<Product> products;
		Set<String> keys = map.keySet();

		if (keys.contains("min") && keys.contains("max"))
			products = dao.getProductsByPriceRange(Double.parseDouble(map.get("min")),
					Double.parseDouble(map.get("max")));
		else
			products = dao.getAllProducts();

		if (products == null)
			return ResponseEntity.notFound().build();

		return ResponseEntity.ok(products);
	}
	
	
	//get product by id via the path variable
	@RequestMapping(method = RequestMethod.GET, path="/{id}")
	public ResponseEntity<?> getProductsById(@PathVariable Integer id) {
		
		Product prod;
		try {
			prod = dao.getProduct(id);
			return ResponseEntity.ok(prod);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("failed to get product with id: "+id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errMsg) ;
			
		}
		
	}
}
