import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Road extends Time {
	static ArrayList<Road> roadList = new ArrayList<Road>();
	static double maxSpeed = 0;
	Intersection start, end;
	ArrayList<Car> currentCars;
	double speedLimit;
	Point2D dir;
	ArrayList<Integer> travelTimes = new ArrayList<Integer>();
	
	public Road(Intersection start, Intersection end, double speedLimit) {
		this.start = start;
		this.end = end;
		this.speedLimit = speedLimit;
		Road.maxSpeed = Math.max(Road.maxSpeed,this.speedLimit);
		
		this.currentCars = new ArrayList<Car>();
		
		double xDir = (this.end.location.x - this.start.location.x)/distance();
		double yDir = (this.end.location.y - this.start.location.y)/distance();
		this.dir = new Point2D.Double(xDir, yDir);
		
		Road.roadList.add(this);
	}
	
	public double distance() {
		return this.start.location.distance(this.end.location);
	}

	@Override
	public void advanceTime() {
		
	}	
	
}
