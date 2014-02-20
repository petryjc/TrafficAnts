import java.util.Deque;

public class CarMixedCurrent extends Car {

	public CarMixedCurrent(int id, Intersection start, Intersection destionation, int startTime) {
		super(id, start, destionation, startTime);
	}
	
	@Override
	public Road nextRoad() {
		Intersection start = null;
		if(this.currentRoad == null) { //we are just starting
			start = this.start;
		} else {
			start = this.currentRoad.end;
		}
		
		Deque<Road> roadList = Utils.generateRoute(start, this.destination, new Utils.LonerUpdator());
		
		return roadList.poll();
	}

}
