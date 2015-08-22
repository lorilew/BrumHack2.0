package data;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;


public class StateTests {
	State state = new State("DisneyLand", 1299555, 5473,0.5);
	State state2 = new State("Hollywood", 1000000, 100,0.9);
	State state3 = new State("Bollywood", 2000000, 200,0.1);
	/*
	 * generate Bunny
	 */
	@Test
	public void generateBunnyTest() {
		
		int total = State.sum(state.getBunniesF())+ State.sum(state.getBunniesM()) + State.sum(state.getMutantZombies());
		System.out.println("At the beginning...");
		System.out.println(Arrays.toString(state.getBunniesF()));
		System.out.println(Arrays.toString(state.getBunniesM()));
		System.out.println(Arrays.toString(state.getMutantZombies()));
		assertEquals(total, 5473);
	}
	/*
	 * age bunnies
	 */
	@Test
	public void ageTest() {
		state.age();
		state.age();
		System.out.println("After 2 years ...");
		System.out.println(Arrays.toString(state.getBunniesF()));
		System.out.println(Arrays.toString(state.getBunniesM()));
		System.out.println(Arrays.toString(state.getMutantZombies()));
	}
	/*
	 * age bunnies
	 */
	@Test
	public void ageTest2() {
		state.age();
		state.age();
		state.age();
		state.age();
		state.age();
		state.age();
		state.age();
		state.age();
		state.age();
		System.out.println("After 9 years ...");
		System.out.println(Arrays.toString(state.getBunniesF()));
		System.out.println(Arrays.toString(state.getBunniesM()));
		System.out.println(Arrays.toString(state.getMutantZombies()));
	}
	/*
	 * infect
	 */
	@Test
	public void infectTest(){
		state.age();
		state.infect();
		state.age();
		state.infect();
		state.age();
		state.infect();
		state.age();
		state.infect();
		state.age();
		state.infect();
		state.age();
		state.infect();
		state.age();
		state.infect();
		state.age();
		state.infect();
		state.age();
		state.infect();
		System.out.println("9 years with infection!");
		System.out.println(Arrays.toString(state.getBunniesF()));
		System.out.println(Arrays.toString(state.getBunniesM()));
		System.out.println(Arrays.toString(state.getMutantZombies()));
	}
	/*
	 * Kill people 
	 */
	@Test
	public void killPeopleTest(){
		int start = state.getHumanPop();
		state.age();
		state.infect();
		state.killPeople();
		int end = state.getHumanPop();
		assertTrue(start>end);
	}
	/*
	 * Testing migration
	 */
	@Test
	public void migrationTest(){
		System.out.println("STATS - start");
		System.out.println(state);
		System.out.println(state2);
		System.out.println(state3);
		state.addNeighbour(state2);
		state.addNeighbour(state3);
		state.HappyNewYear();
		state.HappyNewYear();
		state.HappyNewYear();
		state.HappyNewYear();
		state.HappyNewYear();
		System.out.println("STATS - after 5 years");
		System.out.println(state);
		System.out.println(state2);
		System.out.println(state3);
		//state.openPieChart();
	}

}
