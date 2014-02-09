import java.awt.Point;
import java.util.ArrayList;

public class Intersection extends Time{
	public static ArrayList<Intersection> intersectionList = new ArrayList<Intersection>();
	
	static int ticksPerLight = 20;
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
	}
	
	public boolean canGoThrough(Car c) {
		return c.currentRoad == this.currentRoad;
	}

}
