package entities;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name="review")
@NamedQueries({ @NamedQuery(name = "Review.findAll", query = "SELECT r FROM Review r"),
	})
public class Review implements Serializable{
	private static final long serialVersionUID = 1L;



	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String reviewString;
	
	
	@ManyToOne
	@JoinColumn(name="product")
	private Product product;

	public Review() {}
	
	public Review(String reviewString, Product prod) {
		this.reviewString=reviewString;
		this.product=prod;
	}
	
	public String getReviewString() {
		return this.reviewString;
	}
}
