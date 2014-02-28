package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.util.IO;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE>
{	
	//args are: rule condition leftchild rightchild
	String info = "0 blinkyDis.<.10 1 Flee"; 
	/*inputs are:
	 blinkyDis, inkyDis, pinkyDis, sueDis,
	 blinkyET, inkyET, pinkyET, sueET,
	 blinkyLair, inkyLair, pinkyLair, sueLair,
	 nPillDis,  nPowPDis
	*/
	Map<String, Integer> mGaInputs = new HashMap<String, Integer>();
	List<Rule> mRules = new ArrayList<Rule>();
	int mNearPillNode, mNearPowPillNode;
	String mFile;
	
	public MyPacMan(String file){
		mFile = file;
	}
	
	private void readFile(String filename){
		String fileData = IO.loadFile(filename);
		String[] rules = fileData.split("\n");
		for(String rule: rules){
			mRules.add(new Rule(rule));
		}
	}
	
	private MOVE executeRule(Game game, Rule rule){
		String[] conditions = rule.getCond().split("\\.+");		
		float lhs = mGaInputs.get(conditions[0]);
		float rhs = 0;
		try{
			rhs = Float.parseFloat(conditions[2]);
		}
		catch(NumberFormatException nfe){
			rhs = mGaInputs.get(conditions[2]);
		}
		if(compareValues(lhs,rhs,conditions[1])){
			//lhs
			try{
				return executeRule(game, mRules.get(Integer.parseInt(rule.getLeftHandChild())));
			}
			catch(NumberFormatException nfe){
				return performAction(game, rule.getLeftHandChild(), conditions[0]);
			}
		}			
		else{
			//rhs
			try{
				return executeRule(game, mRules.get(Integer.parseInt(rule.getRightHandChild())));
			}
			catch(NumberFormatException nfe){
				return performAction(game, rule.getRightHandChild(), conditions[0]);
			}
		}
	}
	
	private MOVE performAction(Game game, String action, String target){
		int targetNode = 0;
		if(target.contains("blinky")) 
			targetNode = game.getGhostCurrentNodeIndex(GHOST.BLINKY);
		else if(target.contains("pinky"))
			targetNode = game.getGhostCurrentNodeIndex(GHOST.PINKY);
		else if(target.contains("inky"))
			targetNode = game.getGhostCurrentNodeIndex(GHOST.INKY);
		else if(target.contains("sue"))
			targetNode = game.getGhostCurrentNodeIndex(GHOST.SUE);
		else if(target.contains("Pill"))
			targetNode = mNearPillNode;
		else if(target.contains("Pow"))
			targetNode = mNearPowPillNode;
			
		if(action.equals("Flee"))
			return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), targetNode, DM.PATH);
		else
			return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), targetNode, DM.PATH);
	}
	
	private boolean compareValues(float lhs, float rhs, String comparison){
		boolean result = false;
		switch(comparison){
		case "==":
			if(lhs==rhs) result = true;
			break;
		case "!=":
			if(lhs!=rhs) result = true;
			break;
		case "<":
			if(lhs<rhs) result = true;
			break;
		case "<=":
			if(lhs<=rhs) result = true;
			break;
		case ">":
			if(lhs>rhs) result = true;
			break;
		case ">=":
			if(lhs>=rhs) result = true;
			break;
		}
		return result;
	}	
	
	public MOVE getMove(Game game, long timeDue) 
	{
		int current=game.getPacmanCurrentNodeIndex();		
		this.getGhostDistances(game, current);		
		this.getGhostEdibleTimes(game);				
		this.getGhostLairTimes(game);		
		this.getNearestPillData(game, current);
		this.getNearestPowerPData(game, current);	
		this.readFile(mFile);
		return executeRule(game, mRules.get(0));	
	}
	
	

	private void getGhostDistances(Game game, int current){
		mGaInputs.put("blinkyDis", game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(GHOST.BLINKY)));
		mGaInputs.put("inkyDis", game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(GHOST.INKY)));
		mGaInputs.put("pinkyDis", game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(GHOST.PINKY)));
		mGaInputs.put("sueDis", game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(GHOST.SUE)));		
	}
	
	private void getGhostEdibleTimes(Game game){
		mGaInputs.put("blinkyET", game.getGhostEdibleTime(GHOST.BLINKY));
		mGaInputs.put("inkyET", game.getGhostEdibleTime(GHOST.INKY));
		mGaInputs.put("pinkyET", game.getGhostEdibleTime(GHOST.PINKY));
		mGaInputs.put("sueET", game.getGhostEdibleTime(GHOST.SUE));
	}
	
	private void getGhostLairTimes(Game game){
		mGaInputs.put("blinkyLair", game.getGhostLairTime(GHOST.BLINKY));
		mGaInputs.put("inkyLair", game.getGhostLairTime(GHOST.INKY));
		mGaInputs.put("pinkyLair", game.getGhostLairTime(GHOST.PINKY));
		mGaInputs.put("sueLair", game.getGhostLairTime(GHOST.SUE));		
	}
	
	private void getNearestPillData(Game game, int current){
		int[] activePills=game.getActivePillsIndices();
		int[] targetNodeIndices=new int[activePills.length];
		
		for(int i=0;i<activePills.length;i++)
			targetNodeIndices[i]=activePills[i];
		
		int mNearPillNode = game.getClosestNodeIndexFromNodeIndex(current,targetNodeIndices,DM.PATH);
		mGaInputs.put("nPillDis", game.getShortestPathDistance(current, mNearPillNode));	
	}
	
	private void getNearestPowerPData(Game game, int current){
		int[] activePowerPills=game.getActivePowerPillsIndices();	
		int[] targetNodeIndices=new int[activePowerPills.length];
		for(int i=0;i<activePowerPills.length;i++)
			targetNodeIndices[i]=activePowerPills[i];
		
		int mNearPowPillNode = game.getClosestNodeIndexFromNodeIndex(current, targetNodeIndices, DM.PATH);
		mGaInputs.put("nPowPDis", game.getShortestPathDistance(current, mNearPowPillNode));
	}
	
}