/**
 * Author: Jack Robbins
 * CS 610 Programming Assignment 1
 *
 * This class contains the simulation of queueing strategies
 * Strategies included are 
 * 	- Single queue with 5 service stations
 * 	- Multiple queues with a round robin dispatch
 * 	- Multiple queues with a shortest queue dispatch
 * 	- Multiple queues with a random queue dispatch
 */

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Simulation{

	/**
	 * A simulation in which all passengers are taken from a single queue when ready
	 */
	public static void single_QueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		//Create a context object that we will use for passing values
		SimulationContext context = new SimulationContext();
		context.setStartTime(System.currentTimeMillis());
		
		//Num passengers will be the duration divided by average arrival
		int numPassengers = duration / averageArrivalTime;
		context.setNumPassengers(numPassengers);	

		//Single blocking queue for all of our passengers
		BlockingQueue<Passenger> line = new LinkedBlockingQueue<>(numPassengers);
		context.addQueue(line);	

		//5 service stations
		ScheduledExecutorService service1 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service2 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service3 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service4 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service5 = Executors.newScheduledThreadPool(1);
		
		ServiceStation station1 = new ServiceStation();	
		ServiceStation station2 = new ServiceStation();
		ServiceStation station3 = new ServiceStation();
		ServiceStation station4 = new ServiceStation();
		ServiceStation station5 = new ServiceStation();

		context.addStation(station1);
		context.addStation(station2);
		context.addStation(station3);
		context.addStation(station4);
		context.addStation(station5);


		//Dispatch thread
		ScheduledExecutorService passengerPool = Executors.newScheduledThreadPool(1);

		//Initialize a random for some randomness in times
		Random random = new Random(System.currentTimeMillis());


		int delaySeconds = 0;
		//Immediately begin dequeueing from the pool
		for(int i = 0; i < numPassengers / 5; i++){
			//When the pool is empty, each service station is ready to dequeue
			if(i > 0){
				//Otherwise, set the appropriate delay. This acts as the "service time"  
				delaySeconds = i * averageServiceTime + random.nextInt(-2, 2);
			}
				
			final int processingTime = (delaySeconds == 0) ? averageServiceTime : delaySeconds;

			//Schedule the dequeue for each service station
			service1.schedule(() -> dequeue(0, context), delaySeconds, TimeUnit.SECONDS);	
			service2.schedule(() -> dequeue(0, context), delaySeconds, TimeUnit.SECONDS);	
			service3.schedule(() -> dequeue(0, context), delaySeconds, TimeUnit.SECONDS);	
			service4.schedule(() -> dequeue(0, context), delaySeconds, TimeUnit.SECONDS);	
			service5.schedule(() -> dequeue(0, context), delaySeconds, TimeUnit.SECONDS);	
		}

		//Keep an array of passengers for timing
		Passenger[] passengers = new Passenger[numPassengers];
		context.setPassengers(passengers);


		for(int i = 0; i < numPassengers; i++){
			//+/- 2 seconds randomly for arrival
			delaySeconds = i * averageArrivalTime + random.nextInt(-2, 2);
			passengers[i] = new Passenger();
			//Local copy to avoid compile error
			Passenger entrant = passengers[i];
			//Schedule passenger pool entrance
			passengerPool.schedule(() -> enqueue(0, entrant, context), delaySeconds, TimeUnit.SECONDS);
		}

		//Shutdown
		service1.shutdown();
		service2.shutdown();
		service3.shutdown();
		service4.shutdown();
		service5.shutdown();
		passengerPool.shutdown();
	
		//Blocking while loop for service station termination
		while(  !service1.isTerminated() ||
				!service2.isTerminated() || 
				!service3.isTerminated() || 
				!service4.isTerminated() ||
				!service5.isTerminated() ||
				!passengerPool.isTerminated());

		//Print runtime statistics to the console
		printRuntimeStatistics(context);
	}

	
	public static void multi_RoundRobinSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		SimulationContext context = new SimulationContext();
		context.setStartTime(System.currentTimeMillis());

		//Num passengers will be the duration divided by average arrival
		int numPassengers = duration / averageArrivalTime;
		context.setNumPassengers(numPassengers);

		Random random = new Random(System.currentTimeMillis());

		//5 service lines for this simulation -- one per service station
		BlockingQueue<Passenger> service1_line = new LinkedBlockingQueue<>(numPassengers);
		BlockingQueue<Passenger> service2_line = new LinkedBlockingQueue<>(numPassengers);
		BlockingQueue<Passenger> service3_line = new LinkedBlockingQueue<>(numPassengers);
		BlockingQueue<Passenger> service4_line = new LinkedBlockingQueue<>(numPassengers);
		BlockingQueue<Passenger> service5_line = new LinkedBlockingQueue<>(numPassengers);

		context.addQueue(service1_line);
		context.addQueue(service2_line);
		context.addQueue(service3_line);
		context.addQueue(service4_line);
		context.addQueue(service5_line);


		//5 service stations
		ScheduledExecutorService service1 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service2 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service3 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service4 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service5 = Executors.newScheduledThreadPool(1);
	
		ServiceStation station1 = new ServiceStation();	
		ServiceStation station2 = new ServiceStation();
		ServiceStation station3 = new ServiceStation();
		ServiceStation station4 = new ServiceStation();
		ServiceStation station5 = new ServiceStation();
	
		context.addStation(station1);
		context.addStation(station2);
		context.addStation(station3);
		context.addStation(station4);
		context.addStation(station5);

		//1 queue for passengers to enter from
		ScheduledExecutorService passengerPool = Executors.newScheduledThreadPool(1);

		int delaySeconds = 0;
		//Immediately begin dequeueing from the pool, as our stations are already ready to serve
		for(int i = 0; i < numPassengers / 5; i++){
			//When the pool is empty, each service station is ready to dequeue
			if(i > 0){
				//Set a delay with some randomness here -- this acts as the "service time"
				delaySeconds = i * averageServiceTime + random.nextInt(-2, 2);
			}

			final int processingTime = (delaySeconds == 0) ? averageServiceTime : delaySeconds;

			//Schedule the dequeue for each service station
			service1.schedule(() -> dequeue(0, context), delaySeconds, TimeUnit.SECONDS);	
			service2.schedule(() -> dequeue(1, context), delaySeconds, TimeUnit.SECONDS);	
			service3.schedule(() -> dequeue(2, context), delaySeconds, TimeUnit.SECONDS);	
			service4.schedule(() -> dequeue(3, context), delaySeconds, TimeUnit.SECONDS);	
			service5.schedule(() -> dequeue(4, context), delaySeconds, TimeUnit.SECONDS);	
		}

		//Keep an array of passengers for timing
		Passenger[] passengers = new Passenger[numPassengers];
		context.setPassengers(passengers);


		//Schedule all of the passengers
		for(int i = 0; i < numPassengers; i++){
			//Calculate the arrival time with a random element
			delaySeconds = i * averageArrivalTime + random.nextInt(-2, 2);
			//Make our new passenger
			passengers[i] = new Passenger();
			Passenger entrant = passengers[i];

			//Round robin dispatch strategy
			switch(i % 5){
				case 1:
					passengerPool.schedule(() -> enqueue(1, entrant, context), delaySeconds, TimeUnit.SECONDS);
					break;

				case 2:
					passengerPool.schedule(() -> enqueue(2, entrant, context), delaySeconds, TimeUnit.SECONDS);
					break;
				
				case 3:
					passengerPool.schedule(() -> enqueue(3, entrant, context), delaySeconds, TimeUnit.SECONDS);
					break;
				
				case 4:
					passengerPool.schedule(() -> enqueue(4, entrant, context), delaySeconds, TimeUnit.SECONDS);
					break;
			
				case 0:
					passengerPool.schedule(() -> enqueue(5, entrant, context), delaySeconds, TimeUnit.SECONDS);
					break;
			}
		}

		//Shutdown
		service1.shutdown();
		service2.shutdown();
		service3.shutdown();
		service4.shutdown();
		service5.shutdown();
		passengerPool.shutdown();
	
		//Blocking while loop for service station termination
		while(  !service1.isTerminated() ||
				!service2.isTerminated() || 
				!service3.isTerminated() || 
				!service4.isTerminated() ||
				!service5.isTerminated() ||
				!passengerPool.isTerminated());

		//Print runtime statistics to the console
		printRuntimeStatistics(context);	
	}


	public static void multi_ShortestQueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
				
	}


	public static void multi_RandomQueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		SimulationContext context = new SimulationContext();
		context.setStartTime(System.currentTimeMillis());

		//Num passengers will be the duration divided by average arrival
		int numPassengers = duration / averageArrivalTime;
		context.setNumPassengers(numPassengers);

		//For our random queue enqueueing
		Random random = new Random(System.currentTimeMillis());

		//5 service lines for this simulation -- one per service station
		BlockingQueue<Passenger> service1_line = new LinkedBlockingQueue<>(numPassengers);
		BlockingQueue<Passenger> service2_line = new LinkedBlockingQueue<>(numPassengers);
		BlockingQueue<Passenger> service3_line = new LinkedBlockingQueue<>(numPassengers);
		BlockingQueue<Passenger> service4_line = new LinkedBlockingQueue<>(numPassengers);
		BlockingQueue<Passenger> service5_line = new LinkedBlockingQueue<>(numPassengers);

		context.addQueue(service1_line);
		context.addQueue(service2_line);
		context.addQueue(service3_line);
		context.addQueue(service4_line);
		context.addQueue(service5_line);


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
				//Set a delay with some randomness here -- this acts as the "service time"
				delaySeconds = i * averageServiceTime + random.nextInt(-2, 2);
			}

		}

		final int processingTime = (delaySeconds == 0) ? averageServiceTime : delaySeconds;

		//Schedule the dequeue for each service station
		service1.schedule(() -> dequeue(0, context), delaySeconds, TimeUnit.SECONDS);	
		service2.schedule(() -> dequeue(1, context), delaySeconds, TimeUnit.SECONDS);	
		service3.schedule(() -> dequeue(2, context), delaySeconds, TimeUnit.SECONDS);	
		service4.schedule(() -> dequeue(3, context), delaySeconds, TimeUnit.SECONDS);	
		service5.schedule(() -> dequeue(4, context), delaySeconds, TimeUnit.SECONDS);	
	
		//Keep an array of passengers for timing
		Passenger[] passengers = new Passenger[numPassengers];
		context.setPassengers(passengers);

		//Schedule all of the passengers
		for(int i = 0; i < numPassengers; i++){
			//Calculate the arrival time with a random element
			delaySeconds = i * averageArrivalTime + random.nextInt(-2, 2);
			passengers[i] = new Passenger();
			Passenger entrant = passengers[i];

			//Round robin dispatch strategy
			switch(random.nextInt() % 5){
				case 1:
					passengerPool.schedule(() -> enqueue(1, entrant, context), delaySeconds, TimeUnit.SECONDS);
					break;

				case 2:
					passengerPool.schedule(() -> enqueue(2, entrant, context), delaySeconds, TimeUnit.SECONDS);
					break;
				
				case 3:
					passengerPool.schedule(() -> enqueue(3, entrant, context), delaySeconds, TimeUnit.SECONDS);
					break;
				
				case 4:
					passengerPool.schedule(() -> enqueue(4, entrant, context), delaySeconds, TimeUnit.SECONDS);
					break;
			
				case 0:
					passengerPool.schedule(() -> enqueue(5, entrant, context), delaySeconds, TimeUnit.SECONDS);
					break;
			}
		}

		//Shutdown
		service1.shutdown();
		service2.shutdown();
		service3.shutdown();
		service4.shutdown();
		service5.shutdown();
		passengerPool.shutdown();
	
		//Blocking while loop for service station termination
		while(  !service1.isTerminated() ||
				!service2.isTerminated() || 
				!service3.isTerminated() || 
				!service4.isTerminated() ||
				!service5.isTerminated() ||
				!passengerPool.isTerminated());
	

		//Print runtime statistics to the console
		printRuntimeStatistics(context);	
	}


	/**
	 * Helper method for enqueueing a passenger into a blocking queue
	 */
	private static void enqueue(int queueID, Passenger p, SimulationContext context){
		try{
			context.getQueues().get(queueID).put(p);
			//Set the waiting flag for calculation
			p.startWaiting();

			//Set the shortest queue length
			context.setShortestQueueLength(queueID);
			context.setLongestQueueLength(queueID);	

		} catch(InterruptedException ie){
			System.out.println(ie.getMessage());
		}
	}	



	/**
	 * Helper method for dequeueing passenger into a blocking queue
	 */
	private static void dequeue(int queueID, SimulationContext context){
		try{
			//Occupy the service station for a certain number of seconds
			Passenger dequeued = context.getQueues().get(queueID).take();
			dequeued.stopWaiting();
			System.out.println("Dequeueing");
			//station.occupy(delaySeconds);
			//Stop the waiting
		} catch(InterruptedException ie){
			System.out.println(ie.getMessage());
		}
	}


	/**
	 * A private helper method for printing the runtime statistics to the command line
	 */
	private static void printRuntimeStatistics(SimulationContext context){	
		//Calculate the average waiting time
		double waitingSum = 0;
		double maximumWaitTime = 0;
		for(int i = 0; i < context.getPassengers().length; i++){
			double waitTime = context.getPassengers()[i].getWaitingTime();
			if(waitTime > maximumWaitTime){
				maximumWaitTime = waitTime;
			}
			waitingSum += waitTime;
		}

		long simulationDuration = (System.currentTimeMillis() - context.getStartTime()) / 1000;

		//Display program statistics for user
		System.out.println("\n\n=================== Program Statistics ======================");
		System.out.println("Program Runtime: " + simulationDuration + " seconds\n");
		for(int i = 0; i < context.getQueues().size(); i++){
			System.out.println("Queue " + (i + 1) + " Statistics: ");
			System.out.printf("\tAverage waiting time: %.2f seconds\n", (waitingSum / context.getPassengers().length));
			System.out.printf("\tMaximum waiting time: %.2f seconds\n", maximumWaitTime);	
			System.out.println("\tLongest length: " + context.getLongestQueueLength(i));
		}

		System.out.println("\nService Time Waiting Percentages");
		//Print out the percentage of active time per station
		for(int i = 0; i < 5; i++){
//			System.out.printf("Station %d: active %d%% of the time\n", i + 1, stations[i].getTimeOccupied());
		}



		System.out.println("\n\n=============================================================");	
	}
}
