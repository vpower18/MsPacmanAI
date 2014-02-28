package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pacman.game.util.IO;

public class Evolution {
	
	private static Evolution instance = new Evolution();
	private List<String> vars;
	private List<String> ops;
	private Random random; 
	
	private Evolution(){
		random = new Random();	
		vars = new ArrayList<String>();
		ops = new ArrayList<String>();
		vars.add("blinkyDis");
		vars.add("inkyDis");
		vars.add("pinkyDis");
		vars.add("sueDis");
		vars.add("blinkyET");
		vars.add("inkyET");
		vars.add("pinkyET");
		vars.add("sueET");
		vars.add("blinkyLair");
		vars.add("inkyLair");
		vars.add("pinkyLair");
		vars.add("sueLair");
		vars.add("nPillDis");
		vars.add("nPowPDis");
		ops.add("==");
		ops.add("!=");
		ops.add(">");
		ops.add(">=");
		ops.add("<");
		ops.add("<=");
	}
	
	private ArrayList<Rule> convertToRules(String ruleset){
		ArrayList<Rule> rules = new ArrayList<Rule>();
		String[] rulesArray = ruleset.split("\n");
		for(String rule: rulesArray){
			rules.add(new Rule(rule));
		}
		return rules;
	}
	public String propogateRules(String rulesString1, String rulesString2){	
		ArrayList<Rule> rulesA = convertToRules(rulesString1);
		ArrayList<Rule> rulesB = convertToRules(rulesString2);
		int posA = random.nextInt(rulesA.size());
		int posB = random.nextInt(rulesB.size());
		ArrayList<Rule> temp1 = new ArrayList<Rule>();
		temp1.addAll(rulesA);
		replaceRules(rulesA.get(posA), rulesB.get(posB), rulesA, rulesB);
		if(random.nextInt(4)==0)
			mutate(rulesA);
		return formatRules(rulesA);	
	}	
	
	private void replaceRules(Rule target, Rule replacement,List<Rule> tarRules, List<Rule> repRules){
		target = replacement;
		while(tarRules.size() < repRules.size())
			tarRules.add(repRules.get(tarRules.size()));
		try{
			replaceRules(tarRules.get(Integer.parseInt(replacement.getLeftHandChild())), repRules.get(Integer.parseInt(replacement.getLeftHandChild())), tarRules, repRules);
		}
		catch(NumberFormatException e){
			try{
				replaceRules(tarRules.get(Integer.parseInt(replacement.getRightHandChild())), repRules.get(Integer.parseInt(replacement.getRightHandChild())), tarRules, repRules);
			}
			catch(NumberFormatException f){}
		}		
	}
	
	protected void mutate(List<Rule> rules){
		Rule rule = rules.get(random.nextInt(rules.size()));
		int target = random.nextInt(3);
		switch(target){
		case 0:
			mutateCondition(rule.getCond());
			break;
		case 1:
			mutateChild(rule.getLeftHandChild(), rules.size());
			break;
		case 2:
			mutateChild(rule.getRightHandChild(), rules.size());
			break;
		}
	}
		
	private void mutateCondition(String condition){
		String[] elements = condition.split("\\.+");
		int position = random.nextInt(3);
		switch(position){
		case 0:
			elements[0] = vars.get(random.nextInt(vars.size()));
			break;
		case 1:
			elements[1] = ops.get(random.nextInt(ops.size()));
			break;
		case 2:
			if(random.nextInt(2) == 0)
				elements[2] = Integer.toString(random.nextInt(200)-100);
			else
				elements[2] = vars.get(random.nextInt(vars.size()));
			break;
		}
		condition = elements[0] + "." + elements[1] + "." + elements[2];
	}
	
	private String formatRules(ArrayList<Rule> rules){
		String output = "";
		for(Rule rule: rules){
			output += Integer.toString(rules.indexOf(rule)) + " " + rule.getCond() + " " + rule.getLeftHandChild() + " " + rule.getRightHandChild() + "\n";
		}
		return output;
	}
	
	private void mutateChild(String child, int limit){
		child = Integer.toString(random.nextInt(limit));
	}
	
	public static Evolution getInstance(){
		return instance;
	}	
}
