import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Utils {
	public static class Search {
		Intersection state;
		Road road;
		double time;
		Search parent;
		public Search(Intersection state, Road road, double time, Search parent) {
			this.state = state;
			this.road = road;
			this.time = time;
			this.parent = parent;
		}
	}
	
	public static abstract class MeasurementUpdator {
		abstract double newMeasurement(double old, Road r);
	}
	
	public static class TimeUpdator extends MeasurementUpdator {

		@Override
		double newMeasurement(double old, Road r) {
			return old + r.distance()/r.speedLimit;
		}
		
	}
	
	public static class PheromoneUpdator extends MeasurementUpdator {
		@Override
		double newMeasurement(double old, Road r) {
			return old + (r.distance()/r.speedLimit * Math.pow(r.pheromone,0.2));
		}
	}
	
	public static Deque<Road> generateRoute(final Car c, Intersection start, MeasurementUpdator updator) {
		Comparator<Utils.Search> comparator = new Comparator<Utils.Search>() {
			public int compare(Utils.Search arg0, Utils.Search arg1) {
				if(arg0.time < arg1.time)
					return -1;
				if(arg0.time > arg1.time)
					return 1;
				return 0;
			}
		};
		HashSet<Intersection> beenTo = new HashSet<Intersection>();
		PriorityQueue<Search> storage = 
	            new PriorityQueue<Search>(10, comparator);
		storage.add(new Search(start, null, 0, null));
		
		while(!storage.isEmpty()) {
			Search node = storage.remove();
			if(!beenTo.contains(node)) {
				
				beenTo.add(node.state);
				if(node.state == c.destination){
					Deque<Road> output = new LinkedList<Road>();
					while (node.parent != null) {
						output.push(node.road);
						node = node.parent;
					}
					return output;
				}
				for (Road r : node.state.getOutgoingRoads()) {
					if(!beenTo.contains(r.end)) {
						storage.add(new Search(r.end, r, updator.newMeasurement(node.time, r), node));
					}
				}
			}
			
		}

		System.out.println("Could not generate route");
		return null;
	}
}
