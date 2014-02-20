import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class CarSwarm extends Car {

	public CarSwarm(int id, Intersection start, Intersection destionation, int startTime) {
		super(id, start, destionation, startTime);
	}
	
	public static double alpha = 5;
	public static double decayInverse = 0.99;
	
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
		
		//check to see if we've been to this intersection before.  If we have, remove the looped section and update the times to act like we never did.
		for(int i = 0; i < path.size(); ) {
			if(path.get(i).road.end == currentIntersection) {
				for(int j = 0; j < path.size(); ) {
					if(j < i) {
						path.get(j).startTime = path.get(j).startTime + Time.ticks - path.get(i).startTime;
						j++;
					} else {
						path.remove(j);
					}
				}
				break;
			} else {
				i++;
			}
		}
		
		//update the pheromone for all of the intersections that are involved with this car.
		for(Path p: this.path) {
			HashMap<Road, Double> roadsOutOfIntersection = p.road.start.getPheromone(currentIntersection);
			double deltap = 0.1/(Time.ticks - p.startTime);
			for(Road r : roadsOutOfIntersection.keySet()) {
				if(r != p.road) {
					roadsOutOfIntersection.put(r, roadsOutOfIntersection.get(r)/(1 + deltap));
				} else {
					roadsOutOfIntersection.put(r, (roadsOutOfIntersection.get(r) + deltap)/(1 + deltap));
				}
			}
		}
		
		//if we reach the destination, we have to return that we made it
		if(currentIntersection == this.destination) { 
			return null;
		}
		
		//calculate the total value of the scoring function over all of the roads
		double total = 0, max = 0;
		Road maxRoad = null;
		HashMap<Road,Double> pheromone = currentIntersection.getPheromone(this.destination);
		for(Road r : currentIntersection.getOutgoingRoads()){
			double T = Math.pow(1.0/(1.0/(pheromone.get(r)) + r.currentCars.size()), alpha);
			double n = 100.0/(this.destination.location.distance(r.end.location) + 0.00000001);
			total += T*n;
			if(T*n > max) {
				max = T*n;
				maxRoad = r;
			}
		}
		
		int bestIncentive = 5;
		
		total += max*bestIncentive;
		
		//Use the total and a random float to decide which road to go on next
		float decider = new Random().nextFloat();
		for(Road r : currentIntersection.getOutgoingRoads()){
			double T = Math.pow(1.0/(1.0/(pheromone.get(r)) + r.currentCars.size()), alpha);
			if(r == maxRoad) {
				T *= bestIncentive + 1;
			}
			double n = 100.0/(this.destination.location.distance(r.end.location) + 0.00000001);
			
			decider -= (T*n)/total;
			if(decider <= 0) {
				this.path.add(new Path(r,Time.ticks));
				return r;
			}
		}		
		System.out.println("Broken");
		//Use the total and a random float to decide which road to go on next	
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
