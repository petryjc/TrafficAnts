public class TrafficAnts {

	public static void main(String[] args) {
		
		SetupParser setup = new SetupParser("MapGenTest.txt");

		setup.initialSetup();

		System.out.println(Car.carList.size());
		for (Time.ticks = 0; Time.ticks < 10000; Time.ticks++) {

			for (Intersection i : Intersection.intersectionList) {
				i.advanceTime();
			}
			for (Road r : Road.roadList) {
				r.advanceTime();
			}
			for (Car c : Car.carList) {
				c.advanceTime();
			}
		}
		double time = 0;
		for (Car c : Car.carList) {
			time += c.endTime - c.startTime;
		}
		System.out.println("Average time = " + (time / Car.carList.size()));
	}
}
