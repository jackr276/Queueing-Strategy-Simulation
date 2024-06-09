/**
 * Author: Jack Robbins
 * CS 610 Programming Assignment 1
 *
 * This class file contains the implementation of the Passenger object, who will occupy the queue
 */


public class Passenger{
	
	private long waitingStartTime;
	private long waitingEndTime;


	Passenger(){

	}


	public void startWaiting(){
		this.waitingStartTime = System.currentTimeMillis();
	}

	public void stopWaiting(){
		this.waitingEndTime = System.currentTimeMillis();
	}
	
	public long getWaitingTime(){
		return (this.waitingEndTime - this.waitingStartTime) / 1000;
	}
}
