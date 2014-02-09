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
	
	public static Deque<Road> generateRoute(final Car c, Comparator<Search> comparator, Intersection start) {
		
		HashSet<Intersection> beenTo = new HashSet<Intersection>();
		PriorityQueue<Search> storage = 
	            new PriorityQueue<Search>(10, comparator);
		storage.add(new Search(start, null, 0, null));
		beenTo.add(c.start);
		
		while(!storage.isEmpty()) {
			Search node = storage.remove();
			if(node.state == c.destination){
				Deque<Road> output = new LinkedList<Road>();
				while (node.parent != null) {
					System.out.println(node.state.id);
					output.push(node.road);
					node = node.parent;
				}
				return output;
			}
			for (Road r : node.state.getOutgoingRoads()) {
				if(!beenTo.contains(r.end)) {
					storage.add(new Search(r.end, r, node.time + r.distance()/r.speedLimit, node));
					beenTo.add(r.end);
				}
			}
		}

		System.out.println("Could not generate route");
		System.exit(1);
		return null;
	}
}
