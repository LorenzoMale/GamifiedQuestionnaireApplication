package services;

import java.sql.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import entities.User;
//import exceptions.CredentialsException;

@Stateless
public class UserServices {
	@PersistenceContext(unitName = "MyProject")
	private EntityManager em;

	public UserServices() {
	}

	
	public User checkCredentials(String usrn, String pwd) /*throws CredentialsException, NonUniqueResultException*/{
		List<User> uList = null;
		try {
			uList = em.createNamedQuery("User.checkCredentials", User.class).setParameter(1, usrn).setParameter(2, pwd)
					.getResultList();
		} catch (PersistenceException e) {
			//throw new CredentialsException("Could not verify credentals");
		}
		if (uList.isEmpty())
			return null;
		else if(uList.size() == 1)
			return uList.get(0);
		else return null;
	}
	
	public User checkExistentEmail(String email){
		List<User> uList = null;
		try {
			uList = em.createNamedQuery("User.checkUsedEmail", User.class).setParameter("email", email).getResultList();
		} catch (PersistenceException e) {
			return null;
		}
		if (uList.isEmpty())
			return null;
		else if(uList.size() == 1)
			return uList.get(0);
		else return null;
	}
	
	public User findById(int id) {
		return em.find(User.class, id);
	}
	
	public User createUser(String email, String psw) {
		// set a default username;
		String username = email.substring(0, email.lastIndexOf("@"));
		
		User newUser= new User(username, psw, email, 'U');
		
		em.persist(newUser);
		
		return newUser;
	}
	
	
	public void blockUser(int userID) {
		User user=em.find(User.class, userID);
		user.setRole('B');
	}

	public void updateLastLogin(int userID) {
		User user=em.find(User.class,  userID);
		
		Date today= new Date(System.currentTimeMillis());
		
		user.setLastAccess(today);
		em.persist(user);
	}
}
