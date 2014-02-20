import javax.swing.JFrame;
import java.util.ArrayList;

public class TrafficAnts {

	public static void main(String[] args) {
		String subfilename = "Test1";
		run(CarAStar.class, subfilename);
		run(CarSwarm.class, subfilename);
		run(CarMixedCurrent.class, subfilename);
		
		Intersection.intersectionList = new ArrayList<Intersection>();
		Road.roadList = new ArrayList<Road>();
		Car.carList = new ArrayList<Car>();

		SetupParser setup = new SetupParser(subfilename + "Map.txt");

		setup.initialSetup(CarAStar.class);
		double time = 0;
		for(Car c : Car.carList) {
			time += Utils.routeTime(c.start, c.destination, new Utils.TimeUpdator());
		}
		System.out.println("Optimal Time: " + time/Car.carList.size());
		
	}

	public static void run(Class<? extends Car> carType, String subfilename) {
		Intersection.intersectionList = new ArrayList<Intersection>();
		Road.roadList = new ArrayList<Road>();
		Car.carList = new ArrayList<Car>();

		SetupParser setup = new SetupParser(subfilename + "Map.txt");
		Intersection.persistenceFile = subfilename + "Intersection.xml";

		DrawPanel d = new DrawPanel();
		

		JFrame frame = new JFrame();
		frame.setSize(1000, 1000);
		frame.add(d);
		frame.setVisible(true);
		frame.setTitle(carType + "");

		setup.initialSetup(carType);
		
		//new BlockingCar(Car.carList.size(), Intersection.intersectionList.get(14),Intersection.intersectionList.get(0), 50);
		
		double lastTime = 10000;
		if(carType == CarSwarm.class) {
			lastTime = Intersection.parse();
		}
		ArrayList<Car> carsLeft = new ArrayList<Car>(Car.carList);

		int duration = 5000;
		for (Time.ticks = 0; Time.ticks < duration && carsLeft.size() > 0; Time.ticks++) {

			for (Intersection i : Intersection.intersectionList) {
				i.advanceTime();

			}
			for (Road r : Road.roadList) {
				r.advanceTime();
			}
			for (int i = 0; i < carsLeft.size();) {
				carsLeft.get(i).advanceTime();
				if (carsLeft.get(i).finished) {
					carsLeft.remove(i);
				} else {
					i++;
				}
			}
			
			try {
				if(frame.isVisible()) {
					Thread.sleep(30);	
					d.repaint();
				}
			} catch (InterruptedException e) {
				System.out.println("Thread Broke");
				e.printStackTrace();
			}
		}
		
		frame.setEnabled(false);
		frame.dispose();
		
		double time = 0;
		int unfinished = 0;
		for (Car c : Car.carList) {
			if (c.finished) {
				time += c.endTime - c.startTime;
			} else {
				unfinished++;
				// System.out.println("Car " + c.id + " failed to finish");
			}
		}
		System.out.println("Running " + carType);
		double averageTime = (time + unfinished * duration)/ (Car.carList.size()) ;
		System.out.println("Average time = "
				+ averageTime);
		if (unfinished > 0) {
			System.out.println(unfinished + " didn't finish");
		}

		System.out.println("Simulation finished in " + Time.ticks + " ticks");
		System.out.println();
		if(carType == CarSwarm.class && averageTime < lastTime){
			Intersection.persist(averageTime);
		}
		
	}
}
