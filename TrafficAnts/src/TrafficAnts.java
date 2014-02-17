import javax.swing.JFrame;

public class TrafficAnts {

	public static void main(String[] args) {
		
		SetupParser setup = new SetupParser("MapGenTest.txt");

		setup.initialSetup();
		
		DrawPanel d = new DrawPanel();
		
		JFrame frame = new JFrame();
		frame.setSize(1000, 1000);
		frame.add(d);
		frame.setVisible(true);
		
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
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Thread Broke");
				e.printStackTrace();
			}
			d.repaint();
		}
		
		double time = 0;
		for (Car c : Car.carList) {
			time += c.endTime - c.startTime;
		}
		
		
		System.out.println("Average time = " + (time / Car.carList.size()));
	}
}
