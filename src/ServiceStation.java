/**
 * Author: Jack Robbins
 * CS 610 Programming Assignment 1
 *
 * This class contains the service station that addresses passengers in the queue
 */


public class ServiceStation{
	//To crowd the system, service rate per customer will be arbitrarily large
	private int serviceTime;

	public ServiceStation(int serviceTime){
		this.serviceTime = serviceTime;
	}

	public void servePassenger(Passenger passenger){
		//Passenger is now done waiting
		passenger.stopWaiting();

		try{
			//Wait for the service to be done
			wait(serviceTime);
		} catch(Exception E){
			System.out.println(E.getMessage());
		}
	}


}
