import java.util.Deque;

public class CarAStar extends Car {

	public CarAStar(int id, Intersection start, Intersection destionation, int startTime) {
		super(id, start, destionation, startTime);
	}

	private Deque<Road> roadList;
	@Override
	public Road nextRoad() {
		if(roadList == null) {
			roadList = Utils.generateRoute(this, this.start, new Utils.TimeUpdator());
		} 
		return roadList.poll();
	}

}
