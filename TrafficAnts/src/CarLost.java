import java.util.Random;


public class CarLost extends Car {

	public CarLost(int id, Intersection start, Intersection destionation,
			int startTime) {
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
		return start.getOutgoingRoads().get(new Random().nextInt(start.getOutgoingRoads().size()));
	}

}
