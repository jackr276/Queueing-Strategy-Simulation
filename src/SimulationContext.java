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
	}	

	public void setStartTime(long startTime){
		this.startTime = startTime;
	}

	public void setPassengers(Passenger[] passengers){
		this.passengers = passengers;
	}

	public void setShortestQueueLength(int length){
		if(length < this.shortestQueueLength){
			this.shortestQueueLength = length;
		}
	}

	public void setNumPassengers(int numPassengers){
		this.numPassengers = numPassengers; 
	}

	public void setLongestQueueLength(int longestQueueLength){
		this.longestQueueLength = longestQueueLength;
	}
	
	public void addQueue(BlockingQueue<Passenger> queue){
		this.queues.add(queue);
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

	public int getLongestQueueLength(){
		return this.longestQueueLength;
	}

}
