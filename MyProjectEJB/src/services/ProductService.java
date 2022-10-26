package services;


import java.sql.Date;
import java.util.List;
import java.util.ArrayList;



import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.openjpa.util.NoResultException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import entities.Product;
import entities.Question;
import entities.Questionnaire;
import entities.User;
import exceptions.MissingProductException;
import entities.StatisticalQuestionType;

@Stateless
public class ProductService {
	@PersistenceContext(unitName = "MyProject")
	private EntityManager em;
	
	public ProductService(){}
	
	public Product getTodayProduct() throws MissingProductException{
		
		Product todayProduct=null;
		Date today= new Date(System.currentTimeMillis());
		
		try {
			todayProduct=em.createNamedQuery("Product.findByDate", Product.class)
					.setHint("javax.persistence.cache.storeMode", "REFRESH").setParameter("date", today).getSingleResult();
		}catch(NoResultException e) {

			throw new MissingProductException();
		}catch(PersistenceException e) {
			
			throw new MissingProductException();
		}
		
		return todayProduct;
	}
	
	
	public boolean isDateAvailable(Date date) {
		Product prod=null;
		
		try {
			prod=em.createNamedQuery("Product.findByDate", Product.class).setParameter("date", date).getSingleResult();
		}catch(PersistenceException e) {
			return true;
		}
		if(prod!=null) return false;
		else return true;
	}
	////////////////////////////////////////
	
	
	
	public List<Product> getOldProducts(){
		
		Date today= new Date(System.currentTimeMillis());
		
		List<Product> oldProducts =  em.createNamedQuery("Product.searchOldProducts", Product.class).setParameter("date", today).getResultList();
	
		return oldProducts;
	}
	
	
	public void deleteProduct(int prodID) {
		Product product = em.find(Product.class, prodID);
		
		em.remove(product);
		em.flush();
	}
	
	
	public Product findProductByID(int prodID) {
		Product chosenProduct=em.find(Product.class, prodID);
		return chosenProduct;
	}


}
