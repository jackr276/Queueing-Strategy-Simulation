import java.util.Random;
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

	/**
	 * A simulation in which all passengers are taken from a single queue when ready
	 */
	public static void single_QueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		long startTime = System.currentTimeMillis();
		int numPassengers = duration / averageArrivalTime;
		Random random = new Random(System.currentTimeMillis());


		BlockingQueue<Passenger> line = new LinkedBlockingQueue<>(numPassengers);
		//5 service stations
		ScheduledExecutorService service1 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service2 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service3 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service4 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service5 = Executors.newScheduledThreadPool(1);

		//1 queue for passengers to enter from
		ScheduledExecutorService passengerPool = Executors.newScheduledThreadPool(1);

		int delaySeconds;
		//Immediately begin dequeueing from the pool
		for(int i = 0; i < numPassengers / 5; i++){
			//When the pool is empty, each service station is ready to dequeue
			if(i < 1){
				delaySeconds = 0;
			} else {
				//Otherwise, 
				delaySeconds = i * averageServiceTime + random.nextInt(-2, 2);
			}
			service1.schedule(() -> dequeue(line), delaySeconds, TimeUnit.SECONDS);	
			service2.schedule(() -> dequeue(line), delaySeconds, TimeUnit.SECONDS);	
			service3.schedule(() -> dequeue(line), delaySeconds, TimeUnit.SECONDS);	
			service4.schedule(() -> dequeue(line), delaySeconds, TimeUnit.SECONDS);	
			service5.schedule(() -> dequeue(line), delaySeconds, TimeUnit.SECONDS);	
		}

		//Keep an array of passengers for timing
		Passenger[] passengers = new Passenger[numPassengers];

		for(int i = 0; i < numPassengers; i++){
			//+/- withing 5 seconds randomly for arrival
			delaySeconds = i * averageArrivalTime + random.nextInt(-2, 2);
			passengers[i] = new Passenger();
			Passenger entrant = passengers[i];
			passengerPool.schedule(() -> enqueue(line, entrant), delaySeconds, TimeUnit.SECONDS);
		}

		//Shutdown once done
		service1.shutdown();
		service2.shutdown();
		service3.shutdown();
		service4.shutdown();
		service5.shutdown();
		passengerPool.shutdown();
		
		while(!service1.isTerminated() ||
				!service2.isTerminated() || 
				!service3.isTerminated() || 
				!service4.isTerminated() ||
				!service5.isTerminated());

		double waitingSum = 0;
		for(int i = 0; i < passengers.length; i++){
			waitingSum += passengers[i].getWaitingTime();	
		}


		System.out.println("\n\n=================== Program Statistics ======================");
		System.out.println("Program Runtime: " + (System.currentTimeMillis() - startTime) / 1000 + " seconds.");
		System.out.printf("Average waiting time: %.2f seconds", (waitingSum / passengers.length));
		System.out.println("\n\n=============================================================");
		
	}

	
	public static void multi_RoundRobinSimulation(int duration, int averageArrivalTime, int averageServiceTime){
	}


	public static void multi_ShortestQueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
	}


	public static void multi_RandomQueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
	}


	private static void enqueue(BlockingQueue<Passenger> queue, Passenger p){
		try{
			queue.put(p);
			System.out.println("Enqueueing");
			p.startWaiting();
		} catch(InterruptedException ie){
			System.out.println(ie.getMessage());
		}
		System.out.println("Line size: " + queue.size() + " passengers");
	}	

	private static void dequeue(BlockingQueue<Passenger> queue){
		try{
			Passenger dequeued = queue.take();
			System.out.println("Dequeueing");
			dequeued.stopWaiting();
		} catch(InterruptedException ie){
			System.out.println(ie.getMessage());
		}
		System.out.println(Thread.currentThread().getName());
	}
}
