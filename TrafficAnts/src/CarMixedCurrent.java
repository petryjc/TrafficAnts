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
				int time0 = 0, time1 = 0;
				double timea = 0, timeb;
				for(int i: arg0.road.travelTimes) {
					time0 += i;
				}
				if(arg0.road.travelTimes.size() == 0) {
					timea = arg0.state.location.distance(destination.location) * Road.maxSpeed;
				} else {
					timea = time0/arg0.road.travelTimes.size();
				}
				for(int i: arg1.road.travelTimes) {
					time1 += i;
				}
				if(arg1.road.travelTimes.size() == 0) {
					timeb = arg1.state.location.distance(destination.location) * Road.maxSpeed;
				} else {
					timeb = time1/arg1.road.travelTimes.size();
				}
				
				return (int) ((arg0.time + arg0.state.location.distance(destination.location) * Road.maxSpeed + timea) - 
						(arg1.time + arg1.state.location.distance(destination.location) * Road.maxSpeed + timeb));
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
