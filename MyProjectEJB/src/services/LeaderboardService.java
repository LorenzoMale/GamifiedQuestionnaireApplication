package services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import java.util.List;
import java.util.stream.Collectors;

import entities.User;
import entities.Questionnaire;
import entities.LeaderboardRecord;
import entities.Product;


@Stateless
public class LeaderboardService {
	@PersistenceContext(unitName = "MyProject")
	private EntityManager em;

	public LeaderboardService() {}
	
	
	//check if user has already compiled the questionnaire
	public boolean checkUserHasDoneQuestionnaire(int userID, int questionnaireID){
		
		User user=em.find(User.class, userID);
		Questionnaire questionnaire=em.find(Questionnaire.class, questionnaireID);
		
		LeaderboardRecord userRecord=null;
		try {
			userRecord= em.createNamedQuery("LeaderboardRecord.checkUserInLeaderboard", LeaderboardRecord.class)
					.setHint("javax.persistence.cache.storeMode", "REFRESH").setParameter(1, user).setParameter(2, questionnaire).getSingleResult();
		}catch(PersistenceException e) {
			return false;
		}
		if(userRecord!=null && userRecord.getPoints()>0) return true;
		else return false; 
	}
	
	
	public List<LeaderboardRecord> getLeaderboardNoCacheNoDeletedQuestionnaires(int questionnaireID){
		
		List<LeaderboardRecord> allLeaderboard=getLeaderboardNoCache(questionnaireID);
		
		List<LeaderboardRecord> noDeletedQuestionnaireLeaderboard=allLeaderboard.stream().filter(x->x.getPoints()>0).collect(Collectors.toList());
		
		return noDeletedQuestionnaireLeaderboard;
	}
	
	
	//gets the updated leaderboard
	public List<LeaderboardRecord> getLeaderboardNoCache(int questionnaireID){
		Questionnaire questionnaire=em.find(Questionnaire.class, questionnaireID);
		
		//no use of cache
		List<LeaderboardRecord> leaderboard=em.createNamedQuery("Leaderboard.getQuestionnaireLeaderboard", LeaderboardRecord.class)
				.setHint("javax.persistence.cache.storeMode", "REFRESH").setParameter("questionnaire", questionnaire).getResultList();
		
		return leaderboard;
	}
	
	
	//can be used with cache when regarding older questionnaires (admin side, inspection)
	public List<LeaderboardRecord> getLeaderboard(int questionnaireID){
		
		Questionnaire questionnaire=em.find(Questionnaire.class, questionnaireID);
		
		List<LeaderboardRecord> leaderboard=em.createNamedQuery("Leaderboard.getQuestionnaireLeaderboard", LeaderboardRecord.class)
				.setParameter("questionnaire", questionnaire).getResultList();

		return leaderboard;
	}
	
	
	//gets leaderboardrecords where user has more than 0 points(meaning he has compiled questionnaire)
	public List<LeaderboardRecord> getValidQuestionnairesFromLeaderboard(int prodid){
		
		Product product=em.find(Product.class, prodid);
		
		List<LeaderboardRecord> validQuestionnairesRecord = getLeaderboard(product.getQuestionnaire().getID()).stream().filter(lr->(lr.getPoints()>0)).collect(Collectors.toList());
		
		return validQuestionnairesRecord;
	}
	
	
	
	//gets leaderboardrecords where user has 0 points(meaning he has deleted questionnaire)
	public List<LeaderboardRecord> getNotValidQuestionnairesFromLeaderboard(int prodid){
		Product product=em.find(Product.class, prodid);
		
		List<LeaderboardRecord> notValidQuestionnairesRecord = getLeaderboard(product.getQuestionnaire().getID()).stream().filter(lr->(lr.getPoints()==0)).collect(Collectors.toList());
		
		return notValidQuestionnairesRecord;
	}
	
	
	
	//returns true if user has compiled or deleted the questionnaire, false otherwise
	public boolean checkUserInLeaderboard(int userID, int questionnaireID) {
		User user=em.find(User.class, userID);
		Questionnaire questionnaire=em.find(Questionnaire.class, questionnaireID);
		
		LeaderboardRecord userRecord=null;
		try {
			userRecord= em.createNamedQuery("LeaderboardRecord.checkUserInLeaderboard", LeaderboardRecord.class)
					.setHint("javax.persistence.cache.storeMode", "REFRESH").setParameter(1, user).setParameter(2, questionnaire).getSingleResult();
		}catch(PersistenceException e) {
			return false;
		}
		if(userRecord!=null) return true;
		else return false;
	}
	
	
	//creates a leaderboardrecord with 0 points
	public void cancelledQuestionnaire(int userID, int questionnaireID) {
		
		if(!checkUserInLeaderboard(userID, questionnaireID)){
			User user=em.find(User.class,  userID);
			Questionnaire questionnaire=em.find(Questionnaire.class, questionnaireID);
			
			LeaderboardRecord nullQuestionnaire=new LeaderboardRecord(user, questionnaire, 0);
			
			em.persist(nullQuestionnaire);
		}
	}
	/////////////////////////
	
	

	
}
