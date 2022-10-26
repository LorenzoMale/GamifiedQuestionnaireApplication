package entities;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name="answer")
@NamedQueries({ @NamedQuery(name = "Answer.findByUserAndQuestionnaire", query = "SELECT a FROM Answer a WHERE  a.user=?1 and a.question IN (SELECT q FROM Question q WHERE q.questionnaire=?2)")})
public class Answer implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String answerString;
	
	
	
	@ManyToOne
	@JoinColumn(name="question")
	private Question question;

	

	@ManyToOne
	@JoinColumn(name= "user")
	private User user;
	

	public Answer() {}
	
	public Answer(User user, String answer, Question question) {
		this.user=user;
		this.answerString=answer;
		this.question=question;
	}
	
	public Answer(String answerString) {
		this.answerString=answerString;
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAnswerString() {
		return this.answerString;
	}

	public void setAnswerString(String answer) {
		this.answerString = answer;
	}
	
	public Question getQuestion() {
		return this.question;
	}
/*
	public Question getQuestion() {
		return this.question;
	}
	

	public User getUser() {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user=user;
	}
*/


}
