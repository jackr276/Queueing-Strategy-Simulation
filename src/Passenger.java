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
	private int queueID;
	private int processedBy;

	/**
	 * Keep track of when the passenger starts waiting in line
	 */
	public void startWaiting(int queueID){
		this.waitingStartTime = System.currentTimeMillis();
		this.queueID = queueID;
	}


	/**
	 * Keep track of when the passenger stops waiting(dequeued)
	 */
	public void stopWaiting(int queueID){
		this.waitingEndTime = System.currentTimeMillis();
		this.processedBy = 	queueID;
		System.out.println("Stopped waiting");
	}

	
	public int getProcessedBy(){
		return this.processedBy;
	}

	public int getQueueID(){
		return this.queueID;
	}

	/**
	 * Get the waiting time in seconds
	 */
	public long getWaitingTime(){
		return (this.waitingEndTime - this.waitingStartTime) / 1000;
	}
}
