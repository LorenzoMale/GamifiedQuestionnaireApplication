package entities;


import java.io.Serializable;
import javax.persistence.*;
import java.sql.Date;
import java.util.Base64;
import java.util.List;

@Entity
@Table(name="product")
@NamedQueries({ @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p"),
	@NamedQuery(name="Product.findByDate", query="SELECT p FROM Product p WHERE p.date= :date "),
	@NamedQuery(name="Product.searchOldProducts", query="SELECT p FROM Product p WHERE p.date< :date")
	})
public class Product implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	
	private String name;
	
	private String description;
	
	@Basic(fetch= FetchType.LAZY)
	@Lob
	private byte[] image;
	
	private Date date;
	

	@OneToOne(mappedBy="product", cascade= CascadeType.REMOVE)
	private Questionnaire questionnaire;
	
	
	@OneToMany(mappedBy="product", fetch=FetchType.EAGER)
	private List<Review> reviews;
	
	
	public Product() {}
	
	
	public List<Review> getReviews(){
		return this.reviews;
	}
	

	public Product( String name, String desc, byte[] image, Date date) {
		this.name=name;
		this.description=desc;
		this.image=image;
		this.date=date;
	}
	
	public Questionnaire getQuestionnaire() {
		return this.questionnaire;
	}
	
	public Date getQuestionnaireDate() {
		return this.date;
	}
	
	
	public void addReview(Review review) {
		this.reviews.add(review);
	}
	
	public void setQuestionnaire(Questionnaire quest) {
		this.questionnaire=quest;
	}
	
	///////////////////////////////////////
	

	
	
	public int getID() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getImageData() {
		return Base64.getMimeEncoder().encodeToString(image);
	}
	
	public String getDescription() {
		return this.description;
	}


}
