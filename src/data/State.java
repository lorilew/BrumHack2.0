package data;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;




public class State {
	public statistics.PieChart p;
	private static final double MUTANTBABY = 0.005;
	private static final int INFECT_RATE = 1;
	private static final int KILL_RATE = 25;
	private int humanPop;
	private int startingHumanPop;
	private int[] bunniesF;
	private int[] bunniesM;
	private int[] mutantZombies;
	private double protection;
	private List<State> neighours;
	private String name;
	private int totalKilledBunnies;
	private int totalKilledHumans;

	/**
	 * Create a new state!
	 * @param name
	 * @param humanPop
	 * @param bunniesPop
	 * @param protection
	 */
	public State(String name, int humanPop, int bunniesPop, double protection){
		
		this.neighours = new ArrayList<State>();
		this.protection = protection;
		bunniesF = new int[10];
		bunniesM = new int[10];
		mutantZombies = new int[50];
		this.name = name;
		this.humanPop = humanPop;
		this.startingHumanPop = humanPop;
		//create bunniespop bunnies
		for(int i=0; i<bunniesPop; i++) generateBunny();
	
	}
	private void getStats(String stats){
		try {
			StringBuffer buffer = new StringBuffer();
			File file = new File(name+".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
		    
		   
		    BufferedReader reader = new BufferedReader(new FileReader(file));
		    
		    

		    String line;
		    while ((line = reader.readLine()) != null) {
		        buffer.append(line);
		        buffer.append("\n");
		    }
		    reader.close();
		    buffer.append(stats);
		    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		     writer.write(buffer.toString());
			 writer.close();
			
			
			

		} catch (IOException e) {
			System.out.println("Coundn't open gameStats.txt");
			e.printStackTrace();
		}
	}
	/**
	 * Normalised protection - must be done after construction!
	 * @param average
	 * @param min
	 * @param max
	 */
	public void setProtection(double average, double min, double max){
		this.protection= (protection-average)/Math.abs(max-min);
	}
	/**
	 * Add a state to the list of neighbours
	 * @param State neighbouring state
	 */
	public void addNeighbour(State state){
		this.neighours.add(state);
	}
	/**
	 * Increment year by one - Enjoy  - it may be your last.
	 */
	public void HappyNewYear(){
		
		this.age();
		this.infect();
		this.killPeople();
		this.migration();
		this.getStats(this.name+":"+this.getHumanPop() + "#" + this.getTotalBunnies() + "#" + this.getTotalmutantZombies()+"#");
		if(p!=null){
			this.closePieChart();
		}
	}

	public int getTotalBunnies() {
		return (sum(this.bunniesM) + sum(this.bunniesF) + sum(this.mutantZombies));
	}
	public int getTotalmutantZombies() {
		return sum(this.mutantZombies);
	}
	public int getHumanPop() {
		return humanPop;
	}
	public int[] getBunniesF() {
		return bunniesF;
	}
	public int[] getBunniesM() {
		return bunniesM;
	}
	public int[] getMutantZombies() {
		return mutantZombies;
	}
	public double getProtection() {
		return protection;
	}
	public int getStartingHumanPop() {
		return startingHumanPop;
	}
	public List<State> getNeighours() {
		return neighours;
	}
	public String getName() {
		return name;
	}
	public String toString(){
		String totalmutantBunnies;
		return this.name + " HumanPop:" + this.humanPop + " BunnyPop:" 
				+ this.getTotalBunnies() + " ZombiePop:" + this.getTotalmutantZombies()
				+ " HumanLosses: " + this.totalKilledHumans + " BunniesLosses:"+ this.totalKilledBunnies;
	}
	public void openPieChart(){
		p = new statistics.PieChart(this);
		p.pack();
        p.setVisible(true);
	}
	public void closePieChart(){
		p.dispose();
	}
	//****PRIVATE METHODS****
	void generateBunny(){
		Random rand = new Random();
		double x = rand.nextDouble();
		if(x<=this.MUTANTBABY) mutantZombies[0]++;
		else if(x>this.MUTANTBABY && x<0.52) bunniesF[0]++;
		else bunniesM[0]++;
	}
	void generateBunnies(int bunnies)
	{
		Random rand = new Random();
		double x = rand.nextDouble() * bunnies;
		int mu = (int)(x * MUTANTBABY + (MUTANTBABY/2));
		int y = bunnies - mu;
		int f = y/2;
		int m = y - f;
		mutantZombies[0] += mu;
		bunniesF[0] += f;
		bunniesM[0] += m;
	}
	void age(){
		// move all bunnies up one in the array
		for(int i=8; i>=0; i--){
			bunniesM[i+1] = bunniesM[i];
			bunniesF[i+1] = bunniesF[i];
		}
		for(int i=48; i>=0; i--){
			mutantZombies[i+1] = mutantZombies[i];
		}
		bunniesM[0]=0;
		bunniesF[0]=0;
		mutantZombies[0] = 0;
		// make babies
		int x = Math.min(this.sum(bunniesF), this.sum(bunniesM));
		if(x == this.sum(bunniesM)) x = x - bunniesM[1];
		if(x == this.sum(bunniesF)) x = x - bunniesF[1];
		//for(int i=0; i<x; i++){
		double multiplier = (int)(Math.random()*3.0);
			//for(int j = 0; j < multiplier; j++)
			//{
			//	this.generateBunny();
			//}
			
		//}
		this.generateBunnies((int)(x*multiplier));
	}
	void infect(){
		int z = this.sum(mutantZombies);
		int i = Math.min(z*this.INFECT_RATE, this.sum(bunniesF) + this.sum(bunniesM));
		Random r = new Random();
		double f = Math.min((i*r.nextDouble()), this.sum(bunniesF));// % of females infected
		double m = Math.min((i-f), this.sum(bunniesM));
		for(int j=0; j<f; j++){
			int k = j%10;
			if(bunniesF[k]!=0){
				bunniesF[k]--;
				mutantZombies[k]++;
			}
		}
		for(int j=0; j<m; j++){
			int k = j%10;
			if(bunniesM[k]!=0){
				bunniesM[k]--;
				mutantZombies[k]++;
			}
		}
	}
	void killPeople(){
		if(humanPop!=0){
			int k = Math.min(this.sum(mutantZombies)*this.KILL_RATE,this.humanPop);
			this.humanPop = this.humanPop - k;
			this.totalKilledHumans += k;
		}
	}
	void migrateBunny(int type, int age){
			
	}
	void migrateHelper(int type, int age){
		Random rand = new Random();
		int choice = rand.nextInt(this.neighours.size());
		double r = rand.nextDouble();
		if(r>this.neighours.get(choice).protection){
			if(type==1)this.neighours.get(choice).bunniesF[age]++;
			else if(type==2) this.neighours.get(choice).bunniesM[age]++;
			else this.neighours.get(choice).mutantZombies[age]++;
		}else{
			this.totalKilledBunnies++;
			if(type==1) this.bunniesF[age]--;
			else if(type==2) this.bunniesM[age]--;
			else this.mutantZombies[age]--;
		}
	}
	void migration(){
		for(int i=2; i<10; i++){
			int f = (int)(bunniesF[i]*0.25);
			int m = (int)(bunniesM[i]*0.25);
			for(int j=0; j<f; j++) migrateHelper(1,i);
			for(int j=0; j<m; j++) migrateHelper(2,i);
		}
		for(int i=2; i<50; i++){
			int z = (int)mutantZombies[i]/10;
			for(int j=0; j<z; j++) migrateHelper(0,i);
		}
	}
	static int sum(int[] list){
		int count=0;
		for(int i=0; i<list.length; i++){
			count+=list[i];
		}
		return count;
	}
	
}