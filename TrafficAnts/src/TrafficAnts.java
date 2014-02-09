public class TrafficAnts {
	
	public static void main(String[] args) {
		
		
//		MapGenerator m = new MapGenerator("MapGenTest.txt", 50, 200, 100, .7f, 5);
//		m.generateMapFile();
//		m.printArray();
		
		SetupParser setup = new SetupParser("MapGenTest.txt");
		
		setup.initialSetup();

		System.out.println(Car.carList.size());
		for(Time.ticks = 0; Time.ticks < setup.duration; Time.ticks++ ) {
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
	}
}
