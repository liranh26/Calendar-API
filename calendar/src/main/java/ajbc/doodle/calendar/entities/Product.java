package ajbc.doodle.calendar.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer productId;
	private String productName;
	
	@JsonIgnore
	@Column(insertable = false, updatable = false)
	private Integer supplierId;
	
	
	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name="supplierId")
	private Supplier supplier;
	
	@JsonIgnore
	@Column(insertable = false, updatable = false)
	private Integer categoryId;
	
	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name="categoryId")
	private Category category;
	
	private String quantityPerUnit;
	private Double unitPrice;
	private Integer unitsInStock;
	private Integer unitsOnOrder;
	private Integer reorderLevel;
	private Integer discontinued;
}
