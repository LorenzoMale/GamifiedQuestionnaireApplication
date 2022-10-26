package entities;

public enum StatisticalQuestionType {
	
	SEX("sex"), AGE("age"), LEVEL("level");
	
	private final String questionString;
	
	private StatisticalQuestionType(String questionString) {
		this.questionString=questionString;
	}
	
	public String getQuestionString() {
		return questionString;
	}

}
