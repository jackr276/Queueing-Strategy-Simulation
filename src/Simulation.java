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

	private static final long startTime = System.currentTimeMillis();


	/**
	 * A simulation in which all passengers are taken from a single queue when ready
	 */
	public static void single_QueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		int numPassengers = duration / averageArrivalTime;
		BlockingQueue<Passenger> line = new LinkedBlockingQueue<>(numPassengers);
		//5 service stations
		ScheduledExecutorService servicePool = Executors.newScheduledThreadPool(5);
		//1 queue for passengers
		ScheduledExecutorService passengerPool = Executors.newScheduledThreadPool(1);

		int delaySeconds;
		//Immediately begin dequeueing from the pool
		for(int i = 0; i < duration / averageArrivalTime; i++){
			if(i < 5){
				delaySeconds = 0;
			} else {
				delaySeconds = i * averageServiceTime;
			}
			servicePool.schedule(() -> dequeue(line), delaySeconds, TimeUnit.SECONDS);	
		}
			
		Passenger[] passengers = new Passenger[numPassengers];

		for(int i = 0; i < numPassengers; i++){
			delaySeconds = averageArrivalTime * 2 + i * averageArrivalTime;
			passengers[i] = new Passenger();
			Passenger entrant = passengers[i];
			passengerPool.schedule(() -> enqueue(line, entrant), delaySeconds, TimeUnit.SECONDS);
		}

		servicePool.shutdown();
		passengerPool.shutdown();

		int sumWaitingTime = 0;
		for(int i = 0; i < numPassengers; i++){
			sumWaitingTime += passengers[i].getWaitingTime();
		}


		System.out.println("Average waiting time: " + (double)sumWaitingTime / numPassengers );
	}

	
	public static void multi_RoundRobinSimulation(int duration, int averageArrivalTime, int averageServiceTime){
	}


	public static void multi_ShortestQueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
	}


	public static void multi_RandomQueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
	}


	private static void enqueue(BlockingQueue<Passenger> queue, Passenger p){
		System.out.println("Enqueueing");
		try{
			queue.put(p);
			p.startWaiting();
		} catch(InterruptedException ie){
			System.out.println(ie.getMessage());
		}
		System.out.println(Thread.currentThread().getName());
	}	

	private static void dequeue(BlockingQueue<Passenger> queue){
		System.out.println("Dequeueing");
		try{
			Passenger dequeued = queue.take();
			dequeued.stopWaiting();
		} catch(InterruptedException ie){
			System.out.println(ie.getMessage());
		}
		System.out.println(Thread.currentThread().getName());
	}
}
