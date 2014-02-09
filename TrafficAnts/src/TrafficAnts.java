public class TrafficAnts {
	
	public static void main(String[] args) {
		
		SetupParser setup = new SetupParser("test.txt");
		
		setup.initialSetup();
		
		new CarAStar(Intersection.intersectionList.get(3), Intersection.intersectionList.get(1),0);
		new CarMixed(Intersection.intersectionList.get(3), Intersection.intersectionList.get(1),0);
		new CarMixedCurrent(Intersection.intersectionList.get(3), Intersection.intersectionList.get(1),0);
		Time.ticks = 0;
		
		for(Time.ticks = 0; Time.ticks < 10000; Time.ticks++ ) {
			//System.out.println("Time = " + Time.ticks);
			for(Intersection i : Intersection.intersectionList){
				i.advanceTime();
			}
			for(Road r : Road.roadList){
				r.advanceTime();
			}
			for(Car c : Car.carList){
				c.advanceTime();
			}
		}
		double time = 0;
		for(Car c : Car.carList) {
			time += c.endTime - c.startTime;
		}
		System.out.println("Average time = " + (time / Car.carList.size()));
	}
}
