package services;

import java.util.List;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;


import entities.Answer;
import entities.Product;
import entities.Question;
import entities.Questionnaire;
import entities.Review;
import entities.StatisticalQuestionType;
import entities.User;
import exceptions.BadWordException;



@Stateless
public class AnswerService {
	@PersistenceContext(unitName = "MyProject")
	private EntityManager em;
	
	@EJB(name = "services/BadWordService")
	private BadWordService bwService;
	@EJB(name = "services/UserServices")
	private UserServices uService;
	
	public AnswerService() {}
	
	
	
	
	public void createValidQuestionnaire(int userID, int productID,String[] marketingAnswers, String age, String sex, String level, String review)  throws BadWordException{
		
		
		
		try {
			bwService.controlAnswers2(userID, marketingAnswers, age, review);
		}catch(BadWordException e) {
			throw new BadWordException();
		}
		
		User user =em.find(User.class, userID);
		Product product=em.find(Product.class,  productID);
		Questionnaire questionnaire=product.getQuestionnaire();
		
		
		List<Question> marketingQuestions=questionnaire.getQuestions().stream().filter(q -> q.isMarketing()).collect(Collectors.toList());
		
		List<Answer> newAns=new ArrayList<>();
		for(int i=0; i<marketingAnswers.length; i++ ) {
			newAns.add(new Answer(user, marketingAnswers[i], marketingQuestions.get(i)));
		}
		
		
		List<Question> statisticalQuestions=questionnaire.getQuestions().stream().filter(q -> q.isStatistical()).collect(Collectors.toList());
		
	
		//doesn't store empty answers
		if(age!=null && !age.trim().isEmpty()) newAns.add(new Answer(user, age, statisticalQuestions.get(0)));
		if(sex!=null &&!sex.trim().isEmpty()) newAns.add(new Answer(user, sex, statisticalQuestions.get(1)));
		if(level!=null && !level.trim().isEmpty()) newAns.add(new Answer(user, level, statisticalQuestions.get(2)));
		
		
		
		if(!review.trim().isEmpty()) {
		Review newReview = new Review(review, product);
		em.persist(newReview);
		product.addReview(newReview);
		}
		
		newAns.forEach(em::persist);
		newAns.forEach(x-> x.getQuestion().addAnswer(x));
		
	}
	
	
	public List<Answer> findAnswerByUserAndQuestions(int userID, int questionnaireID){
		User user=em.find(User.class,  userID);
		Questionnaire questionnaire=em.find(Questionnaire.class, questionnaireID);
		
		
		List<Answer> answerList=new ArrayList<>();
		
		try {
		answerList=em.createNamedQuery("Answer.findByUserAndQuestionnaire", Answer.class).setParameter(1,  user).setParameter(2, questionnaire).getResultList();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return answerList;
		
	}
	
	
	
	
	
	////////////////////////////////////////////
	


	
}
