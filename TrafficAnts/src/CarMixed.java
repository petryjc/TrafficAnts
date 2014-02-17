import java.util.Deque;


public class CarMixed extends Car {

	int startRoadTime;
	public CarMixed(int id, Intersection start, Intersection destionation, int startTime) {
		super(id, start, destionation, startTime);
	}

	private Deque<Road> roadList;
	@Override
	public Road nextRoad() {
		if(this.currentRoad != null) {
			this.currentRoad.pheromone += 1.0/(Time.ticks - this.startRoadTime);
		}
		this.startRoadTime = Time.ticks;
		
		if(roadList == null) {
			roadList = Utils.generateRoute(this, this.start, new Utils.PheromoneUpdator());
		} 
		
		if(roadList.isEmpty()) {
			return null;
		}
		return roadList.pop();
	}

}
