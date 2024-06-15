/**
 * Author: Jack Robbins
 * CS 610 Programming Assignment 1
 *
 * This class file contains the implementation of the Passenger object, who will occupy the queue
 */


public class Passenger{
	//Keep track of waiting time for each passenger	
	private long waitingStartTime;
	private long waitingEndTime;


	/**
	 * Keep track of when the passenger starts waiting in line
	 */
	public void startWaiting(){
		this.waitingStartTime = System.currentTimeMillis();
	}


	/**
	 * Keep track of when the passenger stops waiting(dequeued)
	 */
	public void stopWaiting(){
		this.waitingEndTime = System.currentTimeMillis();
	}


	/**
	 * Get the waiting time in seconds
	 */
	public long getWaitingTime(){
		return (this.waitingEndTime - this.waitingStartTime) / 1000;
	}
}
