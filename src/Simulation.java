/**
 * Author: Jack Robbins, jmr226
 *
 * This class contains the simulation of queueing strategies(Strategy Pattern)
 * Strategies included are 
 * 	- Single queue with 5 service stations
 * 	- Multiple queues with a round robin dispatch
 * 	- Multiple queues with a shortest queue dispatch
 * 	- Multiple queues with a random queue dispatch
 *
 * 	Using ScheduledExecutorService for simulation of enqueueing and dequeueing
 */

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * This class contains four separate simulations, all of which must
 * be called statically 
 */
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
		for(int i = 0; i < numPassengers; i++){
			//When the pool is empty, each service station is ready to dequeue	
			//Schedule the dequeue for each service station
			switch(i % 5){
				case 0: 
					service1.schedule(() -> dequeue(0, 0, context), 1, TimeUnit.SECONDS);	
					break;
				case 1:
					service2.schedule(() -> dequeue(0, 1, context), 1, TimeUnit.SECONDS);	
					break;
				case 2:	
					service3.schedule(() -> dequeue(0, 2, context), 1, TimeUnit.SECONDS);	
					break;
				case 3:
					service4.schedule(() -> dequeue(0, 3, context), 1, TimeUnit.SECONDS);	
					break;
				case 4:	
					service5.schedule(() -> dequeue(0, 4, context), 1, TimeUnit.SECONDS);		
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
		while(!service1.isTerminated() || !service2.isTerminated() || 
			  !service3.isTerminated() || !service4.isTerminated() ||
			  !service5.isTerminated() || !passengerPool.isTerminated());

		
		//Print runtime statistics to the console
		printRuntimeStatistics(context);
	}


	/**
	 * Queueing simulation with a round robin dispatch strategy
	 */
	public static void multi_RoundRobinSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		//Create the context object that we will use for passing values
		SimulationContext context = new SimulationContext();
		context.setStartTime(System.currentTimeMillis());
		context.setAverageServiceTime(averageServiceTime);

		//Num passengers will be the duration divided by average arrival
		int numPassengers = duration / averageArrivalTime;
		context.setNumPassengers(numPassengers);

		//Initialize the random element for time randomness
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

		//Keep an array of passengers for timing
		Passenger[] passengers = new Passenger[numPassengers];
		context.setPassengers(passengers);

		int[] occupancy = new int[5];

		//Schedule all of the passengers
		for(int i = 0; i < numPassengers; i++){
			//Calculate the arrival time with a random element
			int delaySeconds = i * averageArrivalTime + random.nextInt(-2, 2);
			//Make our new passenger
			passengers[i] = new Passenger();
			Passenger entrant = passengers[i];

			//Round robin dispatch strategy
			final int queueNum = i % 5;
			passengerPool.schedule(() -> enqueue(queueNum, entrant, context), delaySeconds, TimeUnit.SECONDS);
			occupancy[queueNum]++;
		}

		//Schedule the appropriate dequeue based on occupancy
		for(int i = 0; i < occupancy[0]; i++){
			service1.schedule(() -> dequeue(0, 0, context), 1, TimeUnit.SECONDS);	
		}

		for(int i = 0; i < occupancy[1]; i++){
			service2.schedule(() -> dequeue(1, 1, context), 1, TimeUnit.SECONDS);	
		}

		for(int i = 0; i < occupancy[2]; i++){
			service3.schedule(() -> dequeue(2, 2, context), 1, TimeUnit.SECONDS);	
		}

		for(int i = 0; i < occupancy[3]; i++){
			service4.schedule(() -> dequeue(3, 3, context), 1, TimeUnit.SECONDS);	
		}

		for(int i = 0; i < occupancy[4]; i++){
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
		while(!service1.isTerminated() || !service2.isTerminated() || 
			  !service3.isTerminated() || !service4.isTerminated() ||
			  !service5.isTerminated() || !passengerPool.isTerminated());

		//Print runtime statistics to the console
		printRuntimeStatistics(context);	
	}

	
	/**
	 * Simulation in which the shortest queue is always chosen for entrants
	 */
	public static void multi_ShortestQueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		//Initialize the context object that we will use for passing values
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
		ScheduledExecutorService service1 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service2 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service3 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service4 = Executors.newScheduledThreadPool(1);
		ScheduledExecutorService service5 = Executors.newScheduledThreadPool(1);
	
		//1 queue for passengers to enter from
		ScheduledExecutorService passengerPool = Executors.newScheduledThreadPool(1);


		//Keep an array of passengers for timing
		Passenger[] passengers = new Passenger[numPassengers];
		context.setPassengers(passengers);
		
		int[] occupancy = new int[5];
		
		int shortestQueueID = 0;

		//Schedule all of the passengers
		for(int i = 0; i < numPassengers; i++){	
	
			//Recalculate shortest queue
			int shortestQueueLength = 100;
			for(int j = 0; j < 5; j++){
				if(occupancy[j] < shortestQueueLength){
					shortestQueueID = j;
					shortestQueueLength = occupancy[j];
				}
			}

			//Calculate the arrival time with a random element
			int delaySeconds = i * averageArrivalTime + random.nextInt(-2, 2);
			//Make our new passenger
			passengers[i] = new Passenger();
			Passenger entrant = passengers[i];
			
			final int shortestQueue = shortestQueueID;
			//Shortest queue dispatch strategy
			passengerPool.schedule(() -> enqueue(shortestQueue, entrant, context), delaySeconds, TimeUnit.SECONDS);
			occupancy[shortestQueue]++;
		}

		//Schedule the appropriate dequeue based on occupancy
		for(int i = 0; i < occupancy[0]; i++){
			service1.schedule(() -> dequeue(0, 0, context), 1, TimeUnit.SECONDS);	
		}

		for(int i = 0; i < occupancy[1]; i++){
			service2.schedule(() -> dequeue(1, 1, context), 1, TimeUnit.SECONDS);	
		}

		for(int i = 0; i < occupancy[2]; i++){
			service3.schedule(() -> dequeue(2, 2, context), 1, TimeUnit.SECONDS);	
		}

		for(int i = 0; i < occupancy[3]; i++){
			service4.schedule(() -> dequeue(3, 3, context), 1, TimeUnit.SECONDS);	
		}

		for(int i = 0; i < occupancy[4]; i++){
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
		while(!service1.isTerminated() || !service2.isTerminated() || 
			  !service3.isTerminated() || !service4.isTerminated() ||
			  !service5.isTerminated() || !passengerPool.isTerminated());



		//Print runtime statistics to the console
		printRuntimeStatistics(context);			
	}


	/**
	 * A simulation where each entrant is assigned to a queue at random
	 */
	public static void multi_RandomQueueSimulation(int duration, int averageArrivalTime, int averageServiceTime){
		SimulationContext context = new SimulationContext();
		context.setStartTime(System.currentTimeMillis());
		context.setAverageServiceTime(averageServiceTime);

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

		//Keep an array of passengers for timing
		Passenger[] passengers = new Passenger[numPassengers];
		context.setPassengers(passengers);

		int[] occupancy = new int[5];

		//Schedule all of the passengers
		for(int i = 0; i < numPassengers; i++){
			//Calculate the arrival time with a random element
			int	delaySeconds = i * averageArrivalTime + random.nextInt(-2, 2);
			passengers[i] = new Passenger();
			Passenger entrant = passengers[i];

			//Random queue dispatch strategy
			final int queueNum = random.nextInt(0, 5);
			passengerPool.schedule(() -> enqueue(queueNum, entrant, context), delaySeconds, TimeUnit.SECONDS);
			occupancy[queueNum]++;
		}

		//Schedule the appropriate dequeue based on occupancy
		for(int i = 0; i < occupancy[0]; i++){
			service1.schedule(() -> dequeue(0, 0, context), 1, TimeUnit.SECONDS);	
		}

		for(int i = 0; i < occupancy[1]; i++){
			service2.schedule(() -> dequeue(1, 1, context), 1, TimeUnit.SECONDS);	
		}

		for(int i = 0; i < occupancy[2]; i++){
			service3.schedule(() -> dequeue(2, 2, context), 1, TimeUnit.SECONDS);	
		}

		for(int i = 0; i < occupancy[3]; i++){
			service4.schedule(() -> dequeue(3, 3, context), 1, TimeUnit.SECONDS);	
		}

		for(int i = 0; i < occupancy[4]; i++){
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
		while(!service1.isTerminated() || !service2.isTerminated() || 
		      !service3.isTerminated() || !service4.isTerminated() ||
			  !service5.isTerminated() || !passengerPool.isTerminated());
	

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
			p.startWaiting(queueID);

			//Update queue lengths in context
			context.setLongestQueueLength(queueID);	

			System.out.println("Enqueueing into queue: " + (queueID+1));
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
			//Attempt to dequeue
			Passenger dequeued = context.getQueues().get(queueID).take();
			
			//If it worked, perform all of our updates
			if(dequeued != null){
				//There should be no wait if we're the first 5 customers	
				if(context.getPassengersServed() > 4){
					//Occupy the service station for a certain number of seconds
					int randomFactor = random.nextInt(-2, 2);	
					TimeUnit.SECONDS.sleep(context.getAverageServiceTime() + randomFactor);
					context.addToRandomFactor(randomFactor);
				}

				//Set the waiting flag
				dequeued.stopWaiting(stationID);
				//Keep track of the passengers served
				context.passengerServed();
				System.out.println("Station " + (stationID + 1) + " dequeueing from queue: " + (queueID + 1));
			}
		} catch(InterruptedException ie){
			System.out.println(ie.getMessage());
		}
	}


	/**
	 * A private helper method for printing the runtime statistics to the command line
	 */
	private static void printRuntimeStatistics(SimulationContext context){		
		long simulationDuration = ((System.currentTimeMillis() - context.getStartTime()) / 1000);

		//Display program statistics for user
		System.out.println("\n\n=================== Program Statistics ======================");
		System.out.println("Program Runtime: " + simulationDuration + " seconds\n");
		for(int i = 0; i < context.getQueues().size(); i++){
			System.out.println("Queue " + (i + 1) + " Statistics: ");
			System.out.printf("\tAverage waiting time: %.2f seconds\n", getAverageWaitTime(context, i));
			System.out.printf("\tMaximum waiting time: %.2f seconds\n", getMaxWaitTime(context, i));	
			System.out.println("\tLongest length: " + context.getLongestQueueLength(i));
		}

		//Calcualte the number of passengers per queue
		int[] passengersByQueue = new int[5];
		for(Passenger passenger : context.getPassengers()){
			passengersByQueue[passenger.getProcessedBy()]++;
		}

		System.out.println("\nService Time Waiting Percentages");
		//Print out the percentage of active time per station
		for(int i = 0; i < 5; i++){
			double stationActivePercent = ((((double)passengersByQueue[i] * context.getAverageServiceTime())
											+ context.getRandomFactor()) / simulationDuration) * 100;

			System.out.printf("\tStation %d: active %.2f%% of the time\n", i + 1, stationActivePercent);
		}
		
		//For prettiness
		System.out.println("\n\n=============================================================");	
	}


	/**
	 * A private helper function that gets the average waiting time for a queue
	 */
	private static double getAverageWaitTime(SimulationContext context, int queueID){
		double waitingSum = 0;
		int passengersInQueue = 0;
		
		for(Passenger passenger : context.getPassengers()){
			if(passenger.getQueueID() == queueID){
				if(passenger.getWaitingTime() < 0){
					waitingSum += context.getAverageServiceTime();	
				} else {
					waitingSum += passenger.getWaitingTime();
				}
				passengersInQueue++;
			}
		}

		//Take the average
		return waitingSum / passengersInQueue;
	}


	/**
	 * A private helper method that gets the maximum waiting time for a given queue
	 */
	private static double getMaxWaitTime(SimulationContext context, int queueID){
		double maximumWaitTime = 0;

		for(Passenger passenger : context.getPassengers()){
			if(passenger.getQueueID() == queueID && passenger.getWaitingTime() > maximumWaitTime){
				maximumWaitTime = passenger.getWaitingTime();
			}
		}

		return maximumWaitTime;
	}
}
