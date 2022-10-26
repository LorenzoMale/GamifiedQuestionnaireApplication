package entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="questionnaire")
@NamedQueries({ @NamedQuery(name = "Questionnaire.findByProduct", query = "SELECT q FROM Questionnaire q WHERE q.product=?1")})
public class Questionnaire implements Serializable{
	private static final long serialVersionUID = 1L;



	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	
	@OneToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="product")
	private Product product;

	
	
	@OneToMany(mappedBy="questionnaire", orphanRemoval=true)
	private List<LeaderboardRecord> leaderboard;
	
	@OneToMany(mappedBy="questionnaire", cascade= {CascadeType.PERSIST, CascadeType.REMOVE}, fetch=FetchType.LAZY)
	private List<Question> questions;
	
	
	public Questionnaire() {}
	
	public Questionnaire(Product product) {
		this.product=product;
	}
	
	
	public int getID() {
		return this.id;
	}
	
	public Product getProduct() {
		return this.product;
	}
	
	public void setQuestions(List<Question> questions) {
		this.questions=questions;
	}
	
	public List<Question> getQuestions() {
		return this.questions;
	}
	
	
	/////////////////
	
	public Questionnaire(Product product, boolean YorN) {
		this.product=product;
	
	}
	
	

	
}
