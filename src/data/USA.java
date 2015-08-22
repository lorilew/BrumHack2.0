package data;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JOptionPane;

import esriData.StateData;


public class USA {
	private HashMap<String, State> states = new HashMap<String, State>();
	private int minProtection = Integer.MAX_VALUE;
	private int maxProtection = Integer.MIN_VALUE;
	private double avgProtection;


	public USA(String[] startingStates){
		final long startTime = System.currentTimeMillis();
		//gets state names and data
		List<String> names = StateData.getStateNames();
		Map<String, Object> stateData;
		Map<String, Integer> protections = bloomberg.StateData.stateData();
		
		//declare stuff
		String code;
		int humanPop;
		int bunnyPop = 0;
		int sumProtection = 0;
		double protection;
		
		//create and add State objects
		for(String state : names){
			stateData = StateData.getStateData(state);
			code = (String) stateData.get("STATE_ABBR");
			humanPop = (Integer) stateData.get("POP2007");
			if(code.equals("DC"))
				protection = 74800;
			else
				protection = protections.get(state);
//			protection = (Integer) stateData.get("AGE_22_29");
			bunnyPop = 0;
			for(int i = 0; i < startingStates.length; i++){
				if(startingStates[i].equals(code))
					bunnyPop = 500;
			}
			sumProtection += protection;
			if(protection > maxProtection)
				maxProtection = (int) protection;
			if(protection < minProtection)
				minProtection = (int) protection;
			states.put(code, new State(state, humanPop, bunnyPop, protection));
			avgProtection = sumProtection/51;
		}
		
		//sets correct protection values
		Iterator<State> it = states.values().iterator();
		while(it.hasNext()){
			it.next().setProtection(avgProtection, minProtection, maxProtection);
		}
		 
		try {
			BufferedReader n = new BufferedReader(new FileReader("neighbours.txt"));
			String neighbourStates;
			String[] neighbours;
			//iterates through each state
			while((neighbourStates = n.readLine()) != null){
				neighbours = neighbourStates.split(" ");
				
				//iterates through each neighbour adding them
				for(int i = 1; i < neighbours.length; i++){
					states.get(neighbours[0]).addNeighbour(states.get(neighbours[i]));
				}
				
			}
			n.close();
		} catch (FileNotFoundException e) {
			System.out.println("shit");
		} catch (IOException e) {
			System.out.println("more shit");
			e.printStackTrace();
		}
		System.out.print("Time to load online data: ");
		System.out.println((System.currentTimeMillis() - startTime)/1000);
	}
	
	public void HappyNewYearToAll(){
		
		int activeStates = 0;
		State temp;
		Iterator<State> it = states.values().iterator();
		while(it.hasNext()){
			temp = it.next();
			if(temp.getHumanPop() > 0)
				activeStates++;

			temp.HappyNewYear();
		}
		if(activeStates == 0){
			JOptionPane.showMessageDialog(null,"The USA is dead. Overrun by mutant bunnies.");
		}
	}
	
	public HashMap<String, State> getStates(){
		return this.states;
	}
	
//	public static void main(String[] args) {
//		String[] s = new String[]{"HI", "OK", "NY"};
//		USA murica = new USA(s);
//
//		double p = murica.states.get("DC").getProtection();
//		System.out.println(p);
//		
//	}
}
