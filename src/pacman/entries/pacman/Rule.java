package pacman.entries.pacman;

public class Rule {

	private String mName, mCondition, mLhc, mRhc;	
	
	public Rule(String info){
		String[] elements = info.split("\\s+");
		mName = elements[0];
		mCondition = elements[1];
		mLhc = elements[2];
		mRhc = elements[3];
	}
	
	public String getName(){return mName;}
	public String getCond(){return mCondition;}
	public String getLeftHandChild(){return mLhc;}
	public String getRightHandChild(){return mRhc;}
	
}
