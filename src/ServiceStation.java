/**
 * Author: Jack Robbins
 * CS 610 Programming Assignment 1
 *
 * This class contains the service station that addresses passengers in the queue
 * This object is simply used to keep track of the idle time
 */


public class ServiceStation{
	private int timeOccupied;

	public ServiceStation(){
		this.timeOccupied = 0;
	}

	public void occupy(int delaySeconds){
		this.timeOccupied += delaySeconds;
	}			

	public int getTimeOccupied(){
		return this.timeOccupied;
	}

}

