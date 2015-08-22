package statistics;

import data.State;

public class Main {
	   public static void main(String[] args) {
		   	data.State state = new State("DisneyLand", 1299555, 5473,0.5);
	          PieChart demo = new PieChart(state);
	          demo.pack();
	          demo.setVisible(true);
	      }
	} 