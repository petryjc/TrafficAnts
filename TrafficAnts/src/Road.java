import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Road extends Time {
	static ArrayList<Road> roadList = new ArrayList<Road>();
	static double maxSpeed = 0;
	Intersection start, end;
	ArrayList<Car> currentCars;
	double speedLimit;
	Point2D.Double dir;
	double pheromone;
	int id;

	public Road(int id, Intersection start, Intersection end, double speedLimit) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.speedLimit = speedLimit;
		Road.maxSpeed = Math.max(Road.maxSpeed, this.speedLimit);
		pheromone = this.distance() / this.speedLimit;

		this.currentCars = new ArrayList<Car>();

		double xDir = (this.end.location.x - this.start.location.x)
				/ distance();
		double yDir = (this.end.location.y - this.start.location.y)
				/ distance();
		this.dir = new Point2D.Double(xDir, yDir);

		Road.roadList.add(this);
	}

	public double distance() {
		return this.start.location.distance(this.end.location);
	}

	@Override
	public void advanceTime() {
	}

	public void draw(Graphics g) {
		Point2D.Double seperateRdOffset = shiftAlongNormal();

		double startx = (this.start.location.getX() * DrawPanel.offset)
				+ DrawPanel.xOffset + seperateRdOffset.getX();
		double starty = (this.start.location.getY() * DrawPanel.offset)
				+ DrawPanel.yOffset + seperateRdOffset.getY();

		double endx = (this.end.location.getX() * DrawPanel.offset)
				+ DrawPanel.xOffset + seperateRdOffset.getX();
		double endy = (this.end.location.getY() * DrawPanel.offset)
				+ DrawPanel.yOffset + seperateRdOffset.getY();

		g.setColor(Color.RED);
		g.drawLine((int) startx, (int) starty, (int) endx, (int) endy);
		int midx = Math
				.abs((int) ((this.start.location.getX() + this.end.location
						.getX()) / 2))
				* DrawPanel.offset + DrawPanel.xOffset;
		int midy = Math
				.abs((int) ((this.start.location.getY() + this.end.location
						.getY()) / 2))
				* DrawPanel.offset + DrawPanel.yOffset;
		g.drawString(String.format("%.2f", this.speedLimit), midx, midy);
	}

	public Point2D.Double shiftAlongNormal() {

		Double shiftX = (-this.dir.getY()) * 4;
		Double shiftY = (this.dir.getX()) * 4;

		return new Point2D.Double(shiftX, shiftY);
	}

	@Override
	public String toString() {
		return "R:" + this.id;
	}

}
