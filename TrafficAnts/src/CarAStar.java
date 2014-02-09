import java.util.Comparator;
import java.util.Deque;

public class CarAStar extends Car {

	public CarAStar(Intersection start, Intersection destionation, int startTime) {
		super(start, destionation, startTime);
	}

	private Deque<Road> roadList;
	@Override
	public Road nextRoad() {
		Comparator<Utils.Search> comparator = new Comparator<Utils.Search>() {
			public int compare(Utils.Search arg0, Utils.Search arg1) {
				return (int) ((arg0.time + arg0.state.location.distance(destination.location) * Road.maxSpeed) - 
						(arg1.time + arg1.state.location.distance(destination.location) * Road.maxSpeed));
			}
			
		};
		if(roadList == null) {
			roadList = Utils.generateRoute(this, comparator, this.start);
		} 
		if(roadList.isEmpty()) {
			return null;
		}
		return roadList.pop();
	}

}
