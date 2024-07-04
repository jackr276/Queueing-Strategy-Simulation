# Queueing Simulation
Author: [Jack Robbins](https://www.github.com/jackr276)

## Introduction
This project is intended to show the difference between different queueing strategies and their relative efficiencies. While this simulation is in the context of queues at an airport or grocery store type of location, the concepts learned here can be applied to task/job scheduling queues overall.   

#### This simulation assumes the following:   
  1. There are 5 "service stations" that can serve one queue occupant at a time. These service stations take a while, introducing a bottleneck
  2. All queues are FIFO, there is no priority based queueing mechanism(although that is a future enhancement idea)
  3. Every service station is constantly polling the queue(s) looking for new members to serve. In simulations with multiple queues, service stations can only serve their designated queue and queue occupants may never switch lines(i.e., no hopping over to a shorter line)
  4. Although user-configurable, the idea of this simulation is that the average arrival of new occupants is much less than $1/5$ of the average service time. This ensures we truly crowd the system and force out any differences between strategies.

#### There are 4 separate simulations in this project:   
  1. A single monolithic queue that holds all occupants. All 5 service stations poll this queue and serve passengers from it
  2. 5 separate queues, one for each service station, where new entrants are assigned to queues in a "round-robin" fashion. This is our orderly and sensible approach scenario
  3. 5 separate queues, one for each service station, where new entrants are always assigned to whatever the currently shortest queue is. This could also be termed as the "greedy" approach -- each occupants attempting to minimize its waiting time
  4. 5 separate queues, one for each service station, where new entrants are randomly assigned to queues. This is our "chaos" scenario
