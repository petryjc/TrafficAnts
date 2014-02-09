import java.util.Comparator;
import java.util.Deque;


public class CarMixedCurrent extends Car {

	int startRoadTime;
	public CarMixedCurrent(Intersection start, Intersection destionation, int startTime) {
		super(start, destionation, startTime);
	}

	@Override
	public Road nextRoad() {
		if(this.currentRoad != null) {
			this.currentRoad.travelTimes.add(Time.ticks - this.startRoadTime);
		}
		this.startRoadTime = Time.ticks;
		
		Comparator<Utils.Search> comparator = new Comparator<Utils.Search>() {
			public int compare(Utils.Search arg0, Utils.Search arg1) {
				int dist0 = 0, dist1 = 0;
				for(int i: arg0.road.travelTimes) {
					dist0 += i;
				}
				for(int i: arg1.road.travelTimes) {
					dist1 += i;
				}
				return (int) ((arg0.time + arg0.state.location.distance(destination.location) * Road.maxSpeed + dist0/(double)arg0.road.travelTimes.size()) - 
						(arg1.time + arg1.state.location.distance(destination.location) * Road.maxSpeed + dist1/(double)arg1.road.travelTimes.size()));
			}
		};
		Intersection start = null;
		if(this.currentRoad == null) { //we are just starting
			start = this.start;
		} else {
			start = this.currentRoad.end;
		}
		Deque<Road> roadList = Utils.generateRoute(this, comparator, start);
		
		if(roadList.isEmpty()) {
			return null;
		}
		return roadList.pop();
	}

}
