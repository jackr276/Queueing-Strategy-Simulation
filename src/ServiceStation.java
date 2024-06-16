/**
 * Author: Jack Robbins
 * CS 610 Programming Assignment 1
 *
 * This class contains the service station that addresses passengers in the queue
 * This object is simply used to keep track of the idle time
 */


public class ServiceStation{
	private int averageServiceTime;
	private int passengersServed;

	public ServiceStation(int averageServiceTime){
		this.averageServiceTime = averageServiceTime;
		this.passengersServed = 0;
	}

	public void servePassenger(){
		this.passengersServed++;
	}	

	public double getTimeOccupied(){
		return this.passengersServed * this.averageServiceTime;
	}

}

