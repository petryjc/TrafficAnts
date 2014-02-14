import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class CarSwarm extends Car {

	public CarSwarm(Intersection start, Intersection destionation, int startTime) {
		super(start, destionation, startTime);
	}
	
	public static double alpha = 1;
	public static double decayInverse = 0.9;
	
	private ArrayList<Path> path = new ArrayList<Path>();

	@Override
	public Road nextRoad() {
		//figure out what the current intersection is
		Intersection currentIntersection;
		if(this.currentRoad == null) {
			currentIntersection = this.start;
		} else {
			currentIntersection = this.currentRoad.end;
		}
		//update the pheromone for all of the intersections that are involved with this idea.
		for(Path p: this.path) {
			HashMap<Road, Double> a = p.road.start.getPheromone(currentIntersection);
			a.put(p.road, a.get(p.road) + 1/(Time.ticks - p.startTime));
		}
		
		//if we reach the destination, we have to return that we made it
		if(currentIntersection == this.destination) { 
			return null;
		}
		
		//calculate the total value of the scoring function over all of the roads
		double total = 0;
		HashMap<Road,Double> pheromone = currentIntersection.getPheromone(this.destination);
		for(Road r : currentIntersection.getOutgoingRoads()){
			double T = Math.pow(pheromone.get(r), alpha);
			double n = 100.0/(this.destination.location.distance(r.end.location) + 0.00000001);
			total += T*n;
		}
		
		//Use the total and a random float to decide which road to go on next
		float decider = new Random().nextFloat();
		for(Road r : currentIntersection.getOutgoingRoads()){
			double T = Math.pow(pheromone.get(r), alpha);
			double n = 100.0/(this.destination.location.distance(r.end.location) + 0.00000001);
			
			decider -= (T*n)/total;
			if(decider <= 0) {
				this.path.add(new Path(r,Time.ticks));
				return r;
			}
		}		
		System.out.println("Damn");
		return null;
	}
	
	@Override
	public void destinationReached(){
		super.destinationReached();
	}

	private class Path{
		Road road;
		int startTime;
		public Path(Road r, int start) {
			this.road = r;
			this.startTime = start;
		}
	}
}
