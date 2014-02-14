import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class Intersection extends Time{
	public static ArrayList<Intersection> intersectionList = new ArrayList<Intersection>();
	
	static int ticksPerLight = 3;
	int currentCount = 0;
	Road currentRoad;
	int id;
	Point location;
	
	private ArrayList<Road> incomingRoads;
	ArrayList<Road> getIncomingRoads() {
		if (incomingRoads == null) {
			incomingRoads = new ArrayList<Road>();
			for (Road r : Road.roadList) {
				if (r.end == this) {
					incomingRoads.add(r);
				}
			}
		}
		return incomingRoads;
	}
	
	private ArrayList<Road> outgoingRoads;
	ArrayList<Road> getOutgoingRoads() {
		if (outgoingRoads == null) {
			outgoingRoads = new ArrayList<Road>();
			for (Road r : Road.roadList) {
				if (r.start == this) {
					outgoingRoads.add(r);
				}
			}
		}
		return outgoingRoads;
	}

	private HashMap<Intersection, HashMap<Road,Double>> pheromone;
	public HashMap<Road,Double> getPheromone(Intersection destination) {
		if(pheromone == null) {
			pheromone = new HashMap<Intersection, HashMap<Road,Double>>();
			for(Intersection i: intersectionList) {
				HashMap<Road,Double> map = new HashMap<Road,Double>();
				for(Road r : getOutgoingRoads()) {
					map.put(r, 0.00001);
				}
				pheromone.put(i, map);
			}
		}
		return pheromone.get(destination);
	}
	
	public Intersection(int id, int x, int y) {
		this.location = new Point(x,y);
		this.id = id;
		intersectionList.add(this);
	}
	
	public void advanceTime() {
		currentCount++;
		if(currentRoad == null && getIncomingRoads().size() > 0) {
			currentRoad = getIncomingRoads().get(0);
		}
		if(currentCount > ticksPerLight) {
			currentCount = 0;
			currentRoad = getIncomingRoads().get((getIncomingRoads().indexOf(currentRoad) + 1) % getIncomingRoads().size());
		}
		for(Intersection destination : intersectionList) {
			for(Road r : getPheromone(destination).keySet()) {
				getPheromone(destination).put(r, getPheromone(destination).get(r) * CarSwarm.decayInverse);
			}
		}
	}
	
	public boolean canGoThrough(Car c) {
		return c.currentRoad == this.currentRoad;
	}

}
