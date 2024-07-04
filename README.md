# Queueing Strategy Simulation
Author: [Jack Robbins](https://www.github.com/jackr276)

## Introduction
This project is intended to show the difference between different queueing strategies and their relative efficiencies. While this simulation is in the context of queues at an airport or grocery store type of location, the concepts learned here can be applied to task/job scheduling queues overall.   

#### This simulation assumes the following:   
  1. There are 5 "service stations" that can serve one queue occupant at a time. These service stations take a while, introducing a bottleneck
  2. All queues are FIFO, there is no priority based queueing mechanism(although that is a future enhancement idea)
  3. Every service station is constantly polling the queue(s) looking for new members to serve. In simulations with multiple queues, service stations can only serve their designated queue and queue occupants may never switch lines(i.e., no hopping over to a shorter line)
  4. Although user-configurable, the idea of this simulation is that the average arrival of new occupants is much less than $1/5$ of the average service time. This ensures we truly crowd the system and force out any differences between strategies.
  5. The average arrival time and average service time are inputted by the user. However, there is some element of randomness thrown into both of these metrics. This means that there is a chance that serving a certain occupant could take a few seconds longer or shorter than expected, simulating the reality of 

#### There are 4 separate simulations in this project:   
  1. A single monolithic queue that holds all occupants. All 5 service stations poll this queue and serve passengers from it
  2. 5 separate queues, one for each service station, where new entrants are assigned to queues in a "round-robin" fashion. This is our orderly and sensible approach scenario
  3. 5 separate queues, one for each service station, where new entrants are always assigned to whatever the currently shortest queue is. This could also be termed as the "greedy" approach -- each occupants attempting to minimize its waiting time
  4. 5 separate queues, one for each service station, where new entrants are randomly assigned to queues. This is our "chaos" scenario

## Simulation Measurements
From the start, the simulation will keep track of certain metrics that can help us determine relative performance. For every queue occupant, the total waiting time is recorded and given back as an average at the end of the simulation. In addition to this, the time spent serving occupants for each service station is also recorded and given back as a percentage of the total simulation time. This can show us if we have any "starvation" of our service stations. The longest length ever achieved by each queue is also tracked. Finally, the average and maximum waiting time for each queue is recorded and shown on the final running statistic printout. For user convenience, there is also a realtime display of what is happening in each of the queues.

## Simulation Results
As expected, these results show that Round Robin was the most effective queueing strategy on average. Since there were no distinctions between the queue occupants, the Round Robin dispatch strategy is usually able to spread out the load evenly across all of the service stations. Notable for Round Robin, each service station is active nearly 100% of the time, meaning that this strategy avoids starvation of threads(or in our case, "service stations"). A close second was the shortest queue first approach, and trailing far back are the other two strategies. The monolithic queue strategy works just fine until there are some occupants that have an unexpectedly long service time. If this happens, every other occupant behind the longer service time one simply has to wait, as there is only one queue. The random assignment sometimes works well, but sometimes results in thread starvation because chance has it that only a few queues really fill up, leaving other queues and service stations empty.

## Running this simulation
You can run this simulation yourself using the given runner script in [run.sh](https://github.com/jackr276/Queueing-Strategy-Simulation/blob/main/run.sh). This runner script will grab all of the user input needed to run the entire simulation. To use the runner script, download this project to your machine and navigate to its directory. Following that, run the following commands(note that, of course, the actual output will be different for each run):
```console
example@bash: ~/Queueing-Strategy-Simulation $ chmod +x run.sh
example@bash: ~/Queueing-Strategy-Simulation $ ./run.sh

Compiling:

./src/simulation/SimulationContext.java
./src/simulation/Simulation.java
./src/Main.java
./src/queueOccupant/Passenger.java

Using Java Version: 
openjdk 21.0.3 2024-04-16
OpenJDK Runtime Environment (build 21.0.3+9-Ubuntu-1ubuntu122.04.1)
OpenJDK 64-Bit Server VM (build 21.0.3+9-Ubuntu-1ubuntu122.04.1, mixed mode, sharing)

Compilation Success!
Attempting to Run

================== Simulation Options ======================

1. Single queue
2. Multiple queues with Round Robin dispatch strategy
3. Multiple queues with shortest queue dispatch strategy
4. Multiple queues with random queue dispatch strategy

============================================================

Which simulation would you like to run: 2
Enter the simulation duration in minutes: 2 #This is in MINUTES, so I wouldn't put a value that large
Enter the average arrival time in seconds: 4
Enter the average service time in seconds: 40 #Ensure that this value is a good bit greater than 5 * average arrival time

=============== Displaying Realtime Evolution ==============

Enqueueing into queue: 1
Station 1 dequeueing from queue: 1
Enqueueing into queue: 2
Station 2 dequeueing from queue: 2
Enqueueing into queue: 3
Station 3 dequeueing from queue: 3
Enqueueing into queue: 4
Station 4 dequeueing from queue: 4
Station 5 dequeueing from queue: 5
Enqueueing into queue: 5
Enqueueing into queue: 1
Enqueueing into queue: 2
Enqueueing into queue: 3
Enqueueing into queue: 4
Enqueueing into queue: 5
Enqueueing into queue: 1
Enqueueing into queue: 2
Enqueueing into queue: 3
Enqueueing into queue: 4
Enqueueing into queue: 5
Enqueueing into queue: 1
Station 2 dequeueing from queue: 2
Station 1 dequeueing from queue: 1
Enqueueing into queue: 2
Enqueueing into queue: 3
Station 3 dequeueing from queue: 3
Enqueueing into queue: 4
Station 4 dequeueing from queue: 4
Enqueueing into queue: 5
Station 5 dequeueing from queue: 5
Enqueueing into queue: 1
Enqueueing into queue: 2
Enqueueing into queue: 3
Enqueueing into queue: 4
Enqueueing into queue: 5
Station 2 dequeueing from queue: 2
Enqueueing into queue: 1
Station 1 dequeueing from queue: 1
Enqueueing into queue: 2
Enqueueing into queue: 3
Station 3 dequeueing from queue: 3
Station 4 dequeueing from queue: 4
Enqueueing into queue: 4
Enqueueing into queue: 5
Station 5 dequeueing from queue: 5
Station 1 dequeueing from queue: 1
Station 2 dequeueing from queue: 2
Station 3 dequeueing from queue: 3
Station 4 dequeueing from queue: 4
Station 5 dequeueing from queue: 5
Station 2 dequeueing from queue: 2
Station 1 dequeueing from queue: 1
Station 3 dequeueing from queue: 3
Station 4 dequeueing from queue: 4
Station 5 dequeueing from queue: 5
Station 2 dequeueing from queue: 2
Station 1 dequeueing from queue: 1
Station 3 dequeueing from queue: 3
Station 4 dequeueing from queue: 4
Station 5 dequeueing from queue: 5


=================== Program Statistics ======================
Program Runtime: 232 seconds

Queue 1 Statistics: 
	Average waiting time: 66.17 seconds
	Maximum waiting time: 119.00 seconds
	Longest length: 3
Queue 2 Statistics: 
	Average waiting time: 62.83 seconds
	Maximum waiting time: 116.00 seconds
	Longest length: 2
Queue 3 Statistics: 
	Average waiting time: 66.33 seconds
	Maximum waiting time: 120.00 seconds
	Longest length: 3
Queue 4 Statistics: 
	Average waiting time: 66.67 seconds
	Maximum waiting time: 120.00 seconds
	Longest length: 2
Queue 5 Statistics: 
	Average waiting time: 65.50 seconds
	Maximum waiting time: 117.00 seconds
	Longest length: 3

Service Time Waiting Percentages
	Station 1: active 98.71% of the time
	Station 2: active 98.71% of the time
	Station 3: active 98.71% of the time
	Station 4: active 98.71% of the time
	Station 5: active 98.71% of the time


=============================================================

Running Sucessful! Exit code: 0 

example@bash: ~/Queueing-Strategy-Simulation $
```
