package services;

import java.util.List;
import java.sql.Date;
import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.apache.openjpa.util.NoResultException;

import entities.Product;
import entities.Question;
import entities.User;
import exceptions.MissingProductException;
import entities.Questionnaire;
import entities.StatisticalQuestionType;

@Stateless
public class QuestionnaireService {
	@PersistenceContext(unitName = "MyProject")
	private EntityManager em;
	
	public QuestionnaireService(){}
	
	
	
	
	public void createQuestionnaireAndProduct(String name, String description, Date date, byte[] image, String[] questions) {
		Product newProd= new Product(name, description, image, date);
		
		Questionnaire newQuest = new Questionnaire(newProd);
		
		List<Question> questionList= new ArrayList<>();
		for(int i=0; i<questions.length; i++) {
			Question question=new Question("marketing", questions[i], newQuest);
			questionList.add(question);
		}
		
		questionList.addAll(createStatisticalQuestions(newQuest));
		//update other side of relation
		newQuest.setQuestions(questionList);
		newProd.setQuestionnaire(newQuest);
		
		em.persist(newQuest);
	}
	
	
	public List<Question> createStatisticalQuestions(Questionnaire questionnaire){
		List<Question> statisticalQuestions=new ArrayList<>();;
		
		statisticalQuestions.add(new Question("statistical", StatisticalQuestionType.AGE.getQuestionString(), questionnaire));
		statisticalQuestions.add(new Question("statistical", StatisticalQuestionType.SEX.getQuestionString(), questionnaire));
		statisticalQuestions.add(new Question("statistical", StatisticalQuestionType.LEVEL.getQuestionString(), questionnaire));

		return statisticalQuestions;
	}
	
	
	public Questionnaire getQuestionnaire(int questionnaireID) {
		return em.find(Questionnaire.class, questionnaireID);
	}
	

	
	
	
	///////////

/*	
	public void insertQuestionnaire(User user, Product product, boolean completed) {
		Questionnaire questionnaire= new Questionnaire(user, product, completed);
		em.persist(questionnaire);
		em.flush();
	}
*/
	
	public boolean checkUserQuestionnaire(User user, Product product) {
		Questionnaire quest=null;
		try {
			quest=em.createNamedQuery("Questionnaire.findByProductAndUser",Questionnaire.class).setParameter(1, product).setParameter(2, user).getSingleResult();
		}catch(NoResultException e) {
			return false;
		}catch(PersistenceException e) {
			return false;
		}
		//user has compiled questionnaire for product 
		return true;
	}
	
}
