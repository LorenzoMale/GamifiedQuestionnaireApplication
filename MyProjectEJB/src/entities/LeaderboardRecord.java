package entities;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="leaderboard")
@NamedQueries({@NamedQuery(name="LeaderboardRecord.checkUserInLeaderboard", query="SELECT lr FROM LeaderboardRecord lr WHERE lr.user=?1 and lr.questionnaire=?2"), 
	@NamedQuery(name="Leaderboard.getQuestionnaireLeaderboard", query="SELECT lr FROM LeaderboardRecord lr WHERE lr.questionnaire= :questionnaire ORDER BY lr.points DESC")})
public class LeaderboardRecord implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name="questionnaire")
	private Questionnaire questionnaire;
	
	
	@ManyToOne
	@JoinColumn(name="user")
	private User user;
	
	
	private int points;
	
	
	public LeaderboardRecord() {}
	
	public LeaderboardRecord(User user, Questionnaire questionnaire, int points) {
		this.user=user;
		this.questionnaire=questionnaire;
		this.points=points;
	}
	
	public User getUser() {
		return this.user;
	}
	
	public int getPoints() {
		return this.points;
	}
	
}
