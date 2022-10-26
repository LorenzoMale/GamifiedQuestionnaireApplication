package services;

import java.util.List;
import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.persistence.*;


import entities.Product;
import entities.Question;
import entities.StatisticalQuestionType;

@Stateless
public class QuestionService {
	@PersistenceContext(unitName = "MyProject")
	private EntityManager em;
	
	public QuestionService() {}
	
	
	
	
	public List<Question> getQuestionnaireQuestions(int questionnaireID){
		List<Question> allQuestions=em.createNamedQuery("Question.findByQuestionnaire", Question.class).getResultList();
		return allQuestions;
	}
	
	
	
	public List<Question> getMarketingQuestions(int questionnaireID){
		
		List<Question> allQuestions=getQuestionnaireQuestions(questionnaireID);
		
		List<Question> marketingQuestions=new ArrayList<Question>();
		for( Question q : allQuestions) {
			if(q.getType().equals("marketing")) {
				marketingQuestions.add(q);
			}
		}
		return marketingQuestions;	
	}
	
public List<Question> getStatisticalQuestions(int questionnaireID){
		
		List<Question> allQuestions=getQuestionnaireQuestions(questionnaireID);
		
		List<Question> marketingQuestions=new ArrayList<Question>();
		for( Question q : allQuestions) {
			if(q.getType().equals("statistical")) {
				marketingQuestions.add(q);
			}
		}
		return marketingQuestions;	
	}
	
	
	
	///////////////////////////////////
	
	

	
	public List<Question> getStatisticalQuestions(List<Question> allQuestions){
		List<Question> statisticalQuestions=new ArrayList<Question>();
		for( Question q : allQuestions) {
			if(q.getType().equals("statistical")) {
				statisticalQuestions.add(q);
			}
		}
		
		return statisticalQuestions;	
	}

	/*
	public List<Question> createStatisticalQuestions(Product prod){
		List<Question> questionList=new ArrayList<>();
		
		//adds the statistical questions
		questionList.add(new Question("statistical", StatisticalQuestionType.AGE.getQuestionString(), prod));
		questionList.add(new Question("statistical", StatisticalQuestionType.SEX.getQuestionString(), prod));
		questionList.add(new Question("statistical", StatisticalQuestionType.LEVEL.getQuestionString(), prod));
		
		questionList.stream().forEach(em::persist);
		em.flush();		
		return questionList;
	}
	*/
	
}
