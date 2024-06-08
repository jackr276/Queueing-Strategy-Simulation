import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Author: Jack Robbins
 * CS 610 Programming Assignment 1
 *
 * This class contains the simulation of queueing strategies
 */



public class Simulation{

	private static ServiceStation[] stations;
	private static final long startTime = System.currentTimeMillis();


	/**
	 * A simulation in which all passengers are taken from a single queue when ready
	 */
	public static void single_QueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		BlockingQueue<Passenger> passengers = new LinkedBlockingQueue<>(duration / averageArrivalTime);
		//5 service stations
		ScheduledExecutorService servicePool = Executors.newScheduledThreadPool(5);
		//1 queue for passengers
		ScheduledExecutorService passengerPool = Executors.newScheduledThreadPool(1);

		//Immediately begin dequeueing from the pool
		for(int i = 0; i < duration / averageArrivalTime; i++){
			int delaySeconds = i * averageServiceTime;
			servicePool.schedule(dequeue(passengers), delaySeconds, TimeUnit.SECONDS);	
		}
	

		stations = new ServiceStation[5];

		
	}

	
	public static void multi_RoundRobinSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		stations = new ServiceStation[5];
	}


	public static void multi_ShortestQueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		stations = new ServiceStation[5];
	}


	public static void multi_RandomQueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		stations = new ServiceStation[5];
	}


	private static void enqueue(BlockingQueue<Passenger> queue, Passenger p){
		try{
			queue.put(p);
			p.startWaiting();
		} catch(InterruptedException ie){
			System.out.println(ie.getMessage());
		}


	}	

	private static void dequeue(BlockingQueue<Passenger> queue){
		try{
			Passenger dequeued = queue.take();
			dequeued.stopWaiting();
		} catch(InterruptedException ie){
			System.out.println(ie.getMessage());
		}

	}

}
