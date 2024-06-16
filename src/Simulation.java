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
		context.setAverageServiceTime(averageServiceTime);

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
	
		ServiceStation station1 = new ServiceStation(averageServiceTime);	
		ServiceStation station2 = new ServiceStation(averageServiceTime);
		ServiceStation station3 = new ServiceStation(averageServiceTime);
		ServiceStation station4 = new ServiceStation(averageServiceTime);
		ServiceStation station5 = new ServiceStation(averageServiceTime);
	
		context.addStation(station1);
		context.addStation(station2);
		context.addStation(station3);
		context.addStation(station4);
		context.addStation(station5);


		//Dispatch thread
		ScheduledExecutorService passengerPool = Executors.newScheduledThreadPool(1);

		//Initialize a random for some randomness in times
		Random random = new Random(System.currentTimeMillis());

		//Keep an array of passengers for timing
		Passenger[] passengers = new Passenger[numPassengers];
		context.setPassengers(passengers);

		//Randomly schedule the arrival for all passengers
		for(int i = 0; i < numPassengers; i++){
			//+/- 2 seconds randomly for arrival
			int	delaySeconds = i * averageArrivalTime + random.nextInt(-2, 2);
			passengers[i] = new Passenger();
			//Local copy to avoid compile error
			Passenger entrant = passengers[i];
			//Schedule passenger pool entrance
			passengerPool.schedule(() -> enqueue(0, entrant, context), delaySeconds, TimeUnit.SECONDS);
			//Schedule the dequeue for each service station
		}

		//Immediately begin dequeueing from the pool
		for(int i = 0; i < numPassengers / 5; i++){
			//When the pool is empty, each service station is ready to dequeue	
			//Schedule the dequeue for each service station
			service1.schedule(() -> dequeue(0, 0, context), 1, TimeUnit.SECONDS);	
			service2.schedule(() -> dequeue(0, 1, context), 1, TimeUnit.SECONDS);	
			service3.schedule(() -> dequeue(0, 2, context), 1, TimeUnit.SECONDS);	
			service4.schedule(() -> dequeue(0, 3, context), 1, TimeUnit.SECONDS);	
			service5.schedule(() -> dequeue(0, 4, context), 1, TimeUnit.SECONDS);		
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
		context.setAverageServiceTime(averageServiceTime);

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
		ScheduledExecutorService service1 = Executors.newScheduledThreadPool(5);
		ScheduledExecutorService service2 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service3 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service4 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service5 = Executors.newScheduledThreadPool(1);
	
		//1 queue for passengers to enter from
		ScheduledExecutorService passengerPool = Executors.newScheduledThreadPool(1);


		//Keep an array of passengers for timing
		Passenger[] passengers = new Passenger[numPassengers];
		context.setPassengers(passengers);

		//Schedule all of the passengers
		for(int i = 0; i < numPassengers; i++){
			//Calculate the arrival time with a random element
			int delaySeconds = i * averageArrivalTime + random.nextInt(-2, 2);
			//Make our new passenger
			passengers[i] = new Passenger();
			Passenger entrant = passengers[i];

			//Round robin dispatch strategy
			final int queueNum = i;
			passengerPool.schedule(() -> enqueue(queueNum, entrant, context), delaySeconds, TimeUnit.SECONDS);
			service1.schedule(() -> dequeue(queueNum, queueNum, context), 1, TimeUnit.SECONDS);	
		}

		//Immediately begin dequeueing from the pool, as our stations are already ready to serve
		for(int i = 0; i < numPassengers / 5; i++){
			//Schedule the dequeue for each service station
			service2.schedule(() -> dequeue(1, 1, context), 1, TimeUnit.SECONDS);	
			service3.schedule(() -> dequeue(2, 2, context), 1, TimeUnit.SECONDS);	
			service4.schedule(() -> dequeue(3, 3, context), 1, TimeUnit.SECONDS);	
			service5.schedule(() -> dequeue(4, 4, context), 1, TimeUnit.SECONDS);	
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
			//Schedule the dequeue for each service station
			service1.schedule(() -> dequeue(0, 0, context), 1, TimeUnit.SECONDS);	
			service2.schedule(() -> dequeue(1, 1, context), 1, TimeUnit.SECONDS);	
			service3.schedule(() -> dequeue(2, 2, context), 1, TimeUnit.SECONDS);	
			service4.schedule(() -> dequeue(3, 3, context), 1, TimeUnit.SECONDS);	
			service5.schedule(() -> dequeue(4, 4, context), 1, TimeUnit.SECONDS);	
		}


		//Keep an array of passengers for timing
		Passenger[] passengers = new Passenger[numPassengers];
		context.setPassengers(passengers);

		//Schedule all of the passengers
		for(int i = 0; i < numPassengers; i++){
			//Calculate the arrival time with a random element
			delaySeconds = i * averageArrivalTime + random.nextInt(-2, 2);
			passengers[i] = new Passenger();
			Passenger entrant = passengers[i];

			//Random queue dispatch strategy
			final int queueNum = random.nextInt() % 5;
			passengerPool.schedule(() -> enqueue(queueNum, entrant, context), delaySeconds, TimeUnit.SECONDS);
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
			//Put the passenger in the queue
			context.getQueues().get(queueID).put(p);
			//Set the waiting flag for calculation
			p.startWaiting();

			//Set the shortest queue length
			context.setShortestQueueLength(queueID);
			context.setLongestQueueLength(queueID);	

			System.out.println("Enqueueing");
		} catch(InterruptedException ie){
			System.out.println(ie.getMessage());
		}
	}	



	/**
	 * Helper method for dequeueing passenger from a blocking queue
	 */
	private static void dequeue(int queueID, int stationID, SimulationContext context){
		try{
			//For time randomness
			Random random = new Random(System.currentTimeMillis());

			Passenger dequeued = context.getQueues().get(queueID).take();
			//There should be no wait if we're the first 5 customers	
			if(context.getPassengersServed() > 4){
			//Occupy the service station for a certain number of seconds
				TimeUnit.SECONDS.sleep(context.getAverageServiceTime() + random.nextInt(-2, 2));
			}

			//Set the waiting flag
			dequeued.stopWaiting(stationID);
			//Keep track of the passengers served
			context.passengerServed();
			System.out.println("Dequeueing");

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

		long simulationDuration = ((System.currentTimeMillis() - context.getStartTime()) / 1000);

		//Display program statistics for user
		System.out.println("\n\n=================== Program Statistics ======================");
		System.out.println("Program Runtime: " + simulationDuration + " seconds\n");
		for(int i = 0; i < context.getQueues().size(); i++){
			System.out.println("Queue " + (i + 1) + " Statistics: ");
			System.out.printf("\tAverage waiting time: %.2f seconds\n", (waitingSum / context.getPassengers().length));
			System.out.printf("\tMaximum waiting time: %.2f seconds\n", maximumWaitTime);	
			System.out.println("\tLongest length: " + context.getLongestQueueLength(i));
		}

		int[] passengersByQueue = new int[5];
		for(Passenger passenger : context.getPassengers()){
			passengersByQueue[passenger.getProcessedBy()]++;
		}

		System.out.println("\nService Time Waiting Percentages");
		//Print out the percentage of active time per station
		for(int i = 0; i < 5; i++){
			double stationActivePercent = ((double)passengersByQueue[i] * context.getAverageServiceTime() / simulationDuration) * 100;
			System.out.printf("\tStation %d: active %.2f%% of the time\n", i + 1, stationActivePercent);
		}

		System.out.println("\n\n=============================================================");	
	}
}
