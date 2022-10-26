package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="bad_words")
@NamedQueries({@NamedQuery(name="BadWord.getAll", query="SELECT w FROM BadWord w"), @NamedQuery(name = "BadWord.checkBadWord", query = "SELECT w FROM BadWord w WHERE  w.word= :word")})
public class BadWord implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	private String word;
	
	
	public BadWord() {}
	
	public BadWord(String word) {
		this.word=word;
	}
	
	public void setBadWord(String word) {
		this.word=word;
	}
	
	public String getWord() {
		return this.word;
	}
}
