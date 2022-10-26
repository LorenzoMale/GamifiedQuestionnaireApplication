package entities;

import java.io.Serializable;
import java.util.*;
import javax.persistence.*;

@Entity
@Table(name="question")
@NamedQueries({@NamedQuery(name="Question.findByQuestionnaire", query="SELECT q from Question q WHERE q.questionnaire= :questionnaire")})
public class Question implements Serializable {
	

	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String questionString;

	private String questionType;
	
	
	@ManyToOne
	@JoinColumn(name="questionnaire")
	private Questionnaire questionnaire;
	
	//to display the answers on the initial page
	@OneToMany(mappedBy="question", cascade=CascadeType.REMOVE, orphanRemoval=true)
	private List<Answer> answers;
	
	
	
	public Question() {}
	
	
	public Question(String questionType, String questionString, Questionnaire questionnaire) {
		this.questionType=questionType;
		this.questionString=questionString;
		this.questionnaire=questionnaire;
	}
	
	

	
	public boolean isStatistical() {
		if(this.questionType.equals("statistical")) return true;
		return false;
	}
	
	public boolean isMarketing() {
		if(this.questionType.equals("marketing")) return true;
		return false;
	}
	
	public String getQuestionString() {
		return this.questionString;
	}
	
	
	public void addAnswer(Answer answer) {
		this.answers.add(answer);
	}
	//////////////////////////////

	public Question(Product product, String questionString) {
		this.questionString=questionString;
	}
	
	
	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire=questionnaire;
	}
	
	
	public int getID() {
		return this.id;
	}


	public void setQuestionString(String questionString) {
		this.questionString=questionString;
	}
	
	public String getType() {
		return this.questionType;
	}
	
	public void setType(String type) {
		 this.questionType=type;
	}
	
}
