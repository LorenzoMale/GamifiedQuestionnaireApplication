	package entities;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

@Entity
@Table(name = "user_table")
@NamedQueries({ @NamedQuery(name = "User.checkCredentials", query = "SELECT r FROM User r  WHERE r.username = ?1 and r.password = ?2"), 
	@NamedQuery(name = "User.checkUsedEmail", query = "SELECT r FROM User r  WHERE r.email = :email") })

public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String username;

	private String password;
	
	private String email;
	
	//U user, A admin, B blocked
	private char role;
	
	private Date lastAccess;
	

	@OneToMany(mappedBy="user")
	private List<LeaderboardRecord> leaderboardRecords;



	// Bidirectional many-to-one association to Mission
	/*
	 * Fetch type EAGER allows resorting the relationship list content also in the
	 * client Web servlet after the creation of a new mission. If you leave the
	 * default LAZY policy, the relationship is sorted only at the first access but
	 * then adding a new mission does not trigger the reloading of data from the
	 * database and thus the sort method in the client does not actually re-sort the
	 * list of missions. MERGE is not cascaded because we will modify and merge only
	 * username and surname attributes of the user and do not want to cascade
	 * detached changes to relationship.
	 */
	
	public User() {
	}
	
	public User(String username, String psw, String email, char role) {
		this.username=username;
		this.password=psw;
		this.email=email;
		this.role=role;
		this.lastAccess = Calendar.getInstance().getTime();
		
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email=email;
	}
	
	public char getRole() {
		return this.role;
	}
	
	public void setRole(char newRole) {
		this.role=newRole;
	}

	public void setLastAccess(Date date) {
		this.lastAccess=date;
	}

}
