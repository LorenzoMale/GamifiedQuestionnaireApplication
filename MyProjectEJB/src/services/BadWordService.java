package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import entities.BadWord;
import exceptions.BadWordException;

@Stateless
public class BadWordService {
	@PersistenceContext(unitName = "MyProject")
	private EntityManager em;

	@EJB(name = "services/UserServices")
	private UserServices uService;
	
	public BadWordService() {}
	
	public void controlAnswers2(int userID, String[] marketingAnswers, String age, String review) throws BadWordException{
		
		List<String> badwordsstring=null;
		try {
			List<BadWord> badWords= em.createNamedQuery("BadWord.getAll", BadWord.class).getResultList();
			badwordsstring=badWords.stream().map(x-> x.getWord()).collect(Collectors.toList());
		}catch(PersistenceException e) {
			//there are no bad words
		}
		if (badwordsstring!=null) {
			List<String> allAnswers=new ArrayList<>();
			
			
			allAnswers.addAll(Arrays.asList(marketingAnswers));
			allAnswers.add(age);
			allAnswers.add(review);
			
			
			for(String phrase: allAnswers) {
				String[] words=phrase.split("\\W+");
				
				for(String word:words) {
			
					if(badwordsstring.contains(word)) {
						uService.blockUser(userID);
						throw new BadWordException();
					}
				}
			}
		}
		
	}
	
	
	
	
	
	//////////////////////////////////////////
	
	public void controlAnswers(int userID, String[] marketingAnswers, String age, String review) throws BadWordException{
		
		//saves all answers to a list and then checks every word of every answer in the list
		List<String> allAnswers=new ArrayList<>();
		
		
		allAnswers.addAll(Arrays.asList(marketingAnswers));
		allAnswers.add(age);
		allAnswers.add(review);
		
		
		for(String phrase: allAnswers) {
			String[] words=phrase.split("\\W+");
			
			for(String word:words) {
			
				try {
					checkBadWords(word);
				}catch(BadWordException bw) {
					uService.blockUser(userID);
					throw new BadWordException();
				}
			}
		}
		
	}
	
	public void checkBadWords(String word) throws BadWordException{	
		
		
		BadWord badWord=null;
		try {
			badWord=em.createNamedQuery("BadWord.checkBadWord", BadWord.class).setParameter("word", word).getSingleResult();
		}catch(PersistenceException e) {
			return;
		}
		throw new BadWordException();
	}
}
