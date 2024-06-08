import java.util.Scanner;

/**
 * Author: Jack Robbins
 * CS 610 Programming Assignment 1
 *
 * This class is the runner for the simulation, taking in all of the needed input
 */


public class Main{
	public static void main(String[] args){	
		Scanner in = new Scanner(System.in);

		//Display menu of options
		System.out.println("================== Simulation Options ======================");
		System.out.println("\n1. Single queue");
		System.out.println("2. Multiple queues with Round Robin dispatch strategy");
		System.out.println("3. Multiple queues with shortest queue dispatch strategy");
		System.out.println("4. Multiple queues with random queue dispatch strategy");
		System.out.print("\nWhich simulation would you like to run: ");

		//Grab user selection
		int option = in.nextInt();
		in.close();



		switch(option){
			case 1:
				Simulation.single_QueueSimulation();
				break;
			case 2:
				Simulation.multi_RoundRobinSimulation();
				break;
			case 3:
				Simulation.multi_ShortestQueueSimulation();
				break;
			case 4:
				Simulation.multi_RandomQueueSimulation();
				break;
			default:
				System.out.println("Invalid entry, program will now terminate");
				return;
		}
	}	
}
