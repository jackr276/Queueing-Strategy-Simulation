/**
 * Author: Jack Robbins 
 * Queueing Simulation Entry Point
 *
 * This class is the runner for the simulation, taking in all of the needed input
 */

import java.util.Scanner;
import simulation.Simulation;


public class Main{
	/**
	 * Runs everything according to user input
	 * @param args is not used
	 */
	public static void main(String[] args){	
		Scanner in = new Scanner(System.in);

		//Display menu of options
		System.out.println("================== Simulation Options ======================");
		System.out.println("\n1. Single queue");
		System.out.println("2. Multiple queues with Round Robin dispatch strategy");
		System.out.println("3. Multiple queues with shortest queue dispatch strategy");
		System.out.println("4. Multiple queues with random queue dispatch strategy");
		System.out.println("\n============================================================");	
		System.out.print("\nWhich simulation would you like to run: ");

		//Grab user selection
		int option = in.nextInt();

		//Get the simulation duration in seconds
		System.out.print("Enter the simulation duration in minutes: ");
		int simulationDuration = in.nextInt() * 60;

		//Get the average arrival time of passengers
		System.out.print("Enter the average arrival time in seconds: ");
		int averageArrivalTime = in.nextInt();

		//Get the average service time of the service stations
		System.out.print("Enter the average service time in seconds: ");
		int averageServiceTime = in.nextInt();

		//Done scanning
		in.close();
	
		System.out.println("\n=============== Displaying Realtime Evolution ==============\n");

		//Switch on user input, call appropriate simulation
		switch(option){
			case 1:
				Simulation.single_QueueSimulation(simulationDuration, averageArrivalTime, averageServiceTime);
				break;
			case 2:
				Simulation.multi_RoundRobinSimulation(simulationDuration, averageArrivalTime, averageServiceTime);
				break;
			case 3:
				Simulation.multi_ShortestQueueSimulation(simulationDuration, averageArrivalTime, averageServiceTime);
				break;
			case 4:
				Simulation.multi_RandomQueueSimulation(simulationDuration, averageArrivalTime, averageServiceTime);
				break;
			default:
				System.out.println("Invalid entry, program will now terminate");
				return;
		}
	}
}
