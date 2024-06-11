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

		//Num passengers will be the duration divided by average arrival
		int numPassengers = duration / averageArrivalTime;
		Random random = new Random(System.currentTimeMillis());

		//Single blocking queue for all of our passengers
		BlockingQueue<Passenger> line = new LinkedBlockingQueue<>(numPassengers);

		//5 service stations
		ScheduledExecutorService service1 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service2 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service3 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service4 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service5 = Executors.newScheduledThreadPool(1);

		//1 queue for passengers to enter from
		ScheduledExecutorService passengerPool = Executors.newScheduledThreadPool(1);

		int delaySeconds = 0;
		//Immediately begin dequeueing from the pool
		for(int i = 0; i < numPassengers / 5; i++){
			//When the pool is empty, each service station is ready to dequeue
			if(i > 0){
				//Otherwise, set the appropriate delay. This acts as the "service time"  
				delaySeconds = i * averageServiceTime + random.nextInt(-2, 2);
			}

			//Schedule the dequeue for each service station
			service1.schedule(() -> dequeue(line), delaySeconds, TimeUnit.SECONDS);	
			service2.schedule(() -> dequeue(line), delaySeconds, TimeUnit.SECONDS);	
			service3.schedule(() -> dequeue(line), delaySeconds, TimeUnit.SECONDS);	
			service4.schedule(() -> dequeue(line), delaySeconds, TimeUnit.SECONDS);	
			service5.schedule(() -> dequeue(line), delaySeconds, TimeUnit.SECONDS);	
		}

		//Keep an array of passengers for timing
		Passenger[] passengers = new Passenger[numPassengers];

		for(int i = 0; i < numPassengers; i++){
			//+/- 2 seconds randomly for arrival
			delaySeconds = i * averageArrivalTime + random.nextInt(-2, 2);
			passengers[i] = new Passenger();
			//Local copy to avoid compile error
			Passenger entrant = passengers[i];
			//Schedule passenger pool entrance
			passengerPool.schedule(() -> enqueue(line, entrant), delaySeconds, TimeUnit.SECONDS);
		}

		//Shutdown
		service1.shutdown();
		service2.shutdown();
		service3.shutdown();
		service4.shutdown();
		service5.shutdown();
		passengerPool.shutdown();
	
		//Blocking while loop for service station termination
		while(!service1.isTerminated() ||
				!service2.isTerminated() || 
				!service3.isTerminated() || 
				!service4.isTerminated() ||
				!service5.isTerminated() ||
				!passengerPool.isTerminated());

		printRuntimeStatistics(passengers, numPassengers, startTime);
	}

	
	public static void multi_RoundRobinSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		long startTime = System.currentTimeMillis();

		//Num passengers will be the duration divided by average arrival
		int numPassengers = duration / averageArrivalTime;
		Random random = new Random(System.currentTimeMillis());

		//5 service lines for this simulation -- one per service station
		BlockingQueue<Passenger> service1_line = new LinkedBlockingQueue<>(numPassengers);
		BlockingQueue<Passenger> service2_line = new LinkedBlockingQueue<>(numPassengers);
		BlockingQueue<Passenger> service3_line = new LinkedBlockingQueue<>(numPassengers);
		BlockingQueue<Passenger> service4_line = new LinkedBlockingQueue<>(numPassengers);
		BlockingQueue<Passenger> service5_line = new LinkedBlockingQueue<>(numPassengers);

		//5 service stations
		ScheduledExecutorService service1 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service2 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service3 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service4 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service5 = Executors.newScheduledThreadPool(1);

		//1 queue for passengers to enter from
		ScheduledExecutorService passengerPool = Executors.newScheduledThreadPool(1);

		int delaySeconds = 0;
		//Immediately begin dequeueing from the pool
		for(int i = 0; i < numPassengers / 5; i++){
			//When the pool is empty, each service station is ready to dequeue
			if(i < 1){
				//Set a delay with some randomness here -- this acts as the "service time"
				delaySeconds = i * averageServiceTime + random.nextInt(-2, 2);
			}

			//Schedule the dequeue for each service station
			service1.schedule(() -> dequeue(service1_line), delaySeconds, TimeUnit.SECONDS);	
			service2.schedule(() -> dequeue(service2_line), delaySeconds, TimeUnit.SECONDS);	
			service3.schedule(() -> dequeue(service3_line), delaySeconds, TimeUnit.SECONDS);	
			service4.schedule(() -> dequeue(service4_line), delaySeconds, TimeUnit.SECONDS);	
			service5.schedule(() -> dequeue(service5_line), delaySeconds, TimeUnit.SECONDS);	
		}

		//Keep an array of passengers for timing
		Passenger[] passengers = new Passenger[numPassengers];

		//Schedule all of the passengers
		for(int i = 0; i < numPassengers; i++){
			delaySeconds = i * averageArrivalTime + random.nextInt(-2, 2);
			passengers[i] = new Passenger();
			Passenger entrant = passengers[i];


			switch(i % 5){
				case 1:
					passengerPool.schedule(() -> enqueue(service1_line, entrant), delaySeconds, TimeUnit.SECONDS);
					break;

				case 2:
					passengerPool.schedule(() -> enqueue(service2_line, entrant), delaySeconds, TimeUnit.SECONDS);
					break;
				
				case 3:
					passengerPool.schedule(() -> enqueue(service3_line, entrant), delaySeconds, TimeUnit.SECONDS);
					break;
				
				case 4:
					passengerPool.schedule(() -> enqueue(service4_line, entrant), delaySeconds, TimeUnit.SECONDS);
					break;
			
				case 0:
					passengerPool.schedule(() -> enqueue(service5_line, entrant), delaySeconds, TimeUnit.SECONDS);
					break;
				
			}
		}


	}


	public static void multi_ShortestQueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
	}


	public static void multi_RandomQueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
	}


	/**
	 * Helper method for enqueueing a passenger into a blocking queue
	 */
	private static void enqueue(BlockingQueue<Passenger> queue, Passenger p){
		try{
			//Put in the queue
			queue.put(p);
			//Set the waiting flag for calculation
			p.startWaiting();
			System.out.println("Enqueueing");
		} catch(InterruptedException ie){
			System.out.println(ie.getMessage());
		}
		System.out.println("Line size: " + queue.size() + " passengers");
	}	



	private static void dequeue(BlockingQueue<Passenger> queue){
		try{
			//Dequeue from the queueu
			Passenger dequeued = queue.take();
			//Stop the waiting
			System.out.println("Dequeueing");
			dequeued.stopWaiting();
		} catch(InterruptedException ie){
			System.out.println(ie.getMessage());
		}
		System.out.println(Thread.currentThread().getName());
	}

	private static void printRuntimeStatistics(Passenger[] passengers, int numPassengers, long startTime){	
		//Calculate the average waiting time
		double waitingSum = 0;
		for(int i = 0; i < passengers.length; i++){
			waitingSum += passengers[i].getWaitingTime();	
		}

		//Display program statistics for user
		System.out.println("\n\n=================== Program Statistics ======================");
		System.out.println("Program Runtime: " + (System.currentTimeMillis() - startTime) / 1000 + " seconds.");
		System.out.printf("Average waiting time: %.2f seconds", (waitingSum / passengers.length));
		System.out.println("\n\n=============================================================");	
	}
}
