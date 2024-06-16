import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class SimulationContext{
	private int longestQueueLength;
	private int shortestQueueLength;
	private ArrayList<BlockingQueue<Passenger>> queues;
	private ArrayList<ServiceStation> stations;
	private HashMap<BlockingQueue<Passenger>, Integer> lengths;
	private long startTime;
	private int numPassengers;
	private Passenger[] passengers;


	public SimulationContext(){
		this.longestQueueLength = 0;
		this.shortestQueueLength = 100;
		this.queues = new ArrayList<>();
		this.stations = new ArrayList<>();
		this.lengths = new HashMap<>();
	}	

	public void setStartTime(long startTime){
		this.startTime = startTime;
	}

	public void setPassengers(Passenger[] passengers){
		this.passengers = passengers;
	}

	public void setShortestQueueLength(int queueID){
		if(this.queues.get(queueID).size() < this.shortestQueueLength){
			this.shortestQueueLength = this.queues.get(queueID).size();
		}
	}

	public void setNumPassengers(int numPassengers){
		this.numPassengers = numPassengers; 
	}

	public void setLongestQueueLength(int queueID){
		BlockingQueue<Passenger> queue = this.queues.get(queueID);
		if(lengths.get(queue) < queue.size()){
			lengths.put(queue, queue.size());
		}

	}
	
	public void addQueue(BlockingQueue<Passenger> queue){
		this.queues.add(queue);
		this.lengths.put(queue, 0); 
	}

	public void addStation(ServiceStation station){
		this.stations.add(station);
	}

	public Passenger[] getPassengers(){
		return this.passengers;
	}

	public long getStartTime(){
		return this.startTime;
	}

	public int getNumPassengers(){
		return this.numPassengers;
	}

	public int getShortestQueueLength(){
		return this.shortestQueueLength;
	}

	public ArrayList<BlockingQueue<Passenger>> getQueues(){
		return this.queues;
	}

	public ArrayList<ServiceStation> getStations(){
		return this.stations;
	}

	public int getLongestQueueLength(int queueID){
		return this.lengths.get(this.queues.get(queueID));
	}

}
