import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class CarAStar extends Car {

	public CarAStar(Intersection start, Intersection destionation, int startTime) {
		super(start, destionation, startTime);
	}

	private  class Search {
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

	private Deque<Road> roadList;
	@Override
	public Road nextRoad() {
		if(roadList == null) {
			roadList = generateRoute();
		} 
		if(roadList.isEmpty()) {
			return null;
		}
		return roadList.pop();
	}

	private Deque<Road> generateRoute() {
		Comparator<Search> comparator = new Comparator<Search>() {
			public int compare(Search arg0, Search arg1) {
				return (int) ((arg0.time + arg0.state.location.distance(destination.location) * Road.maxSpeed) - 
						(arg1.time + arg1.state.location.distance(destination.location) * Road.maxSpeed));
			}
			
		};
		HashSet<Intersection> beenTo = new HashSet<Intersection>();
		PriorityQueue<Search> storage = 
	            new PriorityQueue<Search>(10, comparator);
		storage.add(new Search(this.start, null, 0, null));
		beenTo.add(this.start);
		
		while(!storage.isEmpty()) {
			Search node = storage.remove();
			if(node.state == this.destination){
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
