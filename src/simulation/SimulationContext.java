/**
 * Author: Jack Robbins
 * This class is the context object that is used for passing parameters between methods in Simulation.java
 */

package simulation;

import queueOccupant.Passenger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

/**
 * We pass around the context object as a nice package that allows us to keep
 * track of everything at once. Instead of passing around enormous parameter
 * lists, we just pass one of these objects and access/modify these fields
 * as needed
 */
public class SimulationContext{
	/* Context object keeps track of these objects */
	private int shortestQueueLength;
	private int averageServiceTime;
	private int shortestQueueID;
	private ArrayList<BlockingQueue<Passenger>> queues;
	private HashMap<BlockingQueue<Passenger>, Integer> lengths;
	private long startTime;
	private int numPassengers;
	private Passenger[] passengers;
	private int passengersServed;
	private int randomFactor;


	/**
	 * Basic constructor, just initialize all arguments/data structures
	 */
	public SimulationContext(){
		this.shortestQueueLength = 100;
		this.shortestQueueID = 0;
		this.queues = new ArrayList<>();
		this.lengths = new HashMap<>();
		this.passengersServed = 0;
		this.randomFactor = 0;
	}	

	
	/**
	 * Set a simulation start time
	 */
	public void setStartTime(long startTime){
		this.startTime = startTime;
	}


	/**
	 * Add to the randomness factor that we put into all of our processing times
	 */
	public void addToRandomFactor(int randomAdjustment){
		this.randomFactor += randomAdjustment;
	}


	/**
	 * Set the average service time for convenience
	 */
	public void setAverageServiceTime(int averageServiceTime){
		this.averageServiceTime = averageServiceTime;
	}


	/**
	 * Set the internal array of passengers
	 */
	public void setPassengers(Passenger[] passengers){
		this.passengers = passengers;
	}


	/**
	 * Update the shortest queue length
	 */
	public void setShortestQueueLength(int queueID){
		if(this.queues.get(queueID).size() < this.shortestQueueLength){
			this.shortestQueueLength = this.queues.get(queueID).size();
			this.shortestQueueID = queueID;
		}
	}


	/**
	 * Set the number of passengers
	 */
	public void setNumPassengers(int numPassengers){
		this.numPassengers = numPassengers; 
	}


	/**
	 * Updates the longest queue length
	 */
	public void setLongestQueueLength(int queueID){
		BlockingQueue<Passenger> queue = this.queues.get(queueID);
		if(lengths.get(queue) < queue.size()){
			lengths.put(queue, queue.size());
		}
	}


	/**
	 * Add a blocking queue to the internal storage
	 */
	public void addQueue(BlockingQueue<Passenger> queue){
		this.queues.add(queue);
		this.lengths.put(queue, 0); 
	}


	/**
	 * A simple helpper method to update the number of passengers served
	 */
	public void passengerServed(){
		this.passengersServed++;
	}


	/**
	 * A simple helper method to get the passengers array
	 */
	public Passenger[] getPassengers(){
		return this.passengers;
	}


	/**
	 * A simple helper method to get the start time
	 */
	public long getStartTime(){
		return this.startTime;
	}


	/**
	 * A simple helper method to get the average service time
	 */
	public int getAverageServiceTime(){
		return this.averageServiceTime;
	}

	
	/**
	 * A simple helper method to get the number of passengers
	 */
	public int getNumPassengers(){
		return this.numPassengers;
	}

	
	/**
	 * A simple helper method to get the number of passengers served
	 */
	public int getPassengersServed(){
		return this.passengersServed;
	}

	/**
	 * A simple helper method to get the randomness factor
	 */
	public int getRandomFactor(){
		return this.randomFactor;
	}

	/**
	 * A simple helper method to get the length of the shortest queue
	 */
	public int getShortestQueueLength(){
		return this.shortestQueueLength;
	}


	/**
	 * A simple helper method to get the ID of the shortest queue
	 */
	public int getShortestQueueID(){
		return this.shortestQueueID;
	}


	/**
	 * A simple helper method to get the list of queues
	 */
	public ArrayList<BlockingQueue<Passenger>> getQueues(){
		return this.queues;
	}


	/**
	 * Simple helper method to return longest queue length
	 */
	public int getLongestQueueLength(int queueID){
		return this.lengths.get(this.queues.get(queueID));
	}

}
