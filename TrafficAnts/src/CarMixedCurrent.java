import java.util.Deque;

public class CarMixedCurrent extends Car {

	int startRoadTime;
	public CarMixedCurrent(int id, Intersection start, Intersection destionation, int startTime) {
		super(id, start, destionation, startTime);
	}
	
	private Deque<Road> roadList;	

	@Override
	public Road nextRoad() {
		if(this.currentRoad != null) {
			this.currentRoad.pheromone = 1.0/(Time.ticks - this.startRoadTime);
			if(this.currentRoad.pheromone == 0) {
				System.out.println("FUCK");
			}
		}
		this.startRoadTime = Time.ticks;
		
		if(roadList == null) {
			roadList = Utils.generateRoute(this, this.start, new Utils.TimeUpdator());
		} 
		
		Intersection start = null;
		if(this.currentRoad == null) { //we are just starting
			start = this.start;
		} else {
			start = this.currentRoad.end;
		}
		Deque<Road> roadList = Utils.generateRoute(this, start, new Utils.PheromoneUpdator());
		
		if(this.roadList.poll() != roadList.peek()) {
			//System.out.println(id);
		}
		return roadList.poll();
	}

}
