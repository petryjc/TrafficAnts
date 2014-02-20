import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class Car extends Time {
	static ArrayList<Car> carList = new ArrayList<Car>();
	Road currentRoad;
	double distanceAlongRoad;
	Intersection start;
	final Intersection destination;
	int startTime;
	double endTime;
	boolean finished = false;
	int id;

	public Car(int id, Intersection start, Intersection destionation,
			int startTime) {
		this.id = id;
		this.start = start;
		this.destination = destionation;
		this.startTime = startTime;
		carList.add(this);
	}

	public double bufferZone() {
		return 3;
	}

	public double distanceIncrement() {
		return this.currentRoad.speedLimit;
	}

	@Override
	public void advanceTime() {
		if (!finished) {
			if (startTime == Time.ticks) {
				this.currentRoad = this.nextRoad();
				if (this.currentRoad == null) {
					if (this.start == this.destination) {
						System.out
								.println("You were already there you twat!!!");
					} else {
						System.out
								.println("A star could not get from intersection "
										+ this.start.id
										+ " to "
										+ this.destination.id);
					}
					destinationReached();
					return;
				}
				this.currentRoad.currentCars.add(this);
				this.distanceAlongRoad = 0;
			} else if (Time.ticks > startTime) {
				Car closestFront = null;
				for (Car c : currentRoad.currentCars) {
					if (c != this
							&& c.distanceAlongRoad > this.distanceAlongRoad
							&& (closestFront == null || closestFront != null
									&& c.distanceAlongRoad < closestFront.distanceAlongRoad)) {
						closestFront = c;
					}
				}
				double distancePossible = 0;
				if (closestFront == null) {
					distancePossible = distanceIncrement();
				} else {
					distancePossible = Math
							.max(Math.min(distanceIncrement(),
									closestFront.distanceAlongRoad
											- this.distanceAlongRoad
											- bufferZone()), 0);
				}
				if ((this.currentRoad.distance() - this.distanceAlongRoad) < distancePossible) { // We're
																									// at
																									// the
																									// end
																									// of
																									// the
																									// road
					if (this.currentRoad.end.currentRoad == this.currentRoad) { // And
																				// the
																				// light
																				// is
																				// green
						double remainingTick = (this.currentRoad.distance() - this.distanceAlongRoad)
								/ this.currentRoad.speedLimit;
						Road next = this.nextRoad();
						if (next == null) {
							if (this.currentRoad.end == this.destination) {
								this.currentRoad.currentCars.remove(this);
								// System.out.println("This worked sorta " +
								// (endTime - startTime) + " as a " +
								// this.getClass());
							} else {
								System.out.println("Got to intersection "
										+ this.currentRoad.end.id
										+ " but couldn't get to "
										+ this.destination.id);
							}
							destinationReached();
							return;
						}
						this.currentRoad.currentCars.remove(this);
						this.currentRoad = next;
						this.currentRoad.currentCars.add(this);
						this.distanceAlongRoad = this.distanceIncrement()
								* remainingTick;
					} else {
						this.distanceAlongRoad = this.currentRoad.distance();
					}
				} else {
					this.distanceAlongRoad += distancePossible;
				}
			}
		}
	}

	public void destinationReached() {
		this.endTime = Time.ticks;
		this.finished = true;
	}

	public void draw(Graphics g) {
		if (this.finished) {
			return;
		}
		if (this.currentRoad == null) {
			return;
		}

		Point2D.Double seperateRdOffset = shiftAlongNormal();

		Double x = this.currentRoad.start.location.getX()
				+ (this.currentRoad.dir.getX() * this.distanceAlongRoad);
		Double y = this.currentRoad.start.location.getY()
				+ (this.currentRoad.dir.getY() * this.distanceAlongRoad);

		Point2D.Double carLoc = new Point2D.Double((x * DrawPanel.offset)
				+ DrawPanel.xOffset + seperateRdOffset.getX(),
				(y * DrawPanel.offset) + DrawPanel.yOffset
						+ seperateRdOffset.getY());

		g.setColor(Color.black);
		g.fillRect((int) carLoc.x - 3, (int) carLoc.y - 3, 6, 6);

		// g.drawString(Integer.toString(this.id), (int)carLoc.getX() - 2,
		// (int)carLoc.getY() - 2);

	}

	public Point2D.Double shiftAlongNormal() {

		Double shiftX = (-this.currentRoad.dir.getY()) * 4;
		Double shiftY = (this.currentRoad.dir.getX()) * 4;

		return new Point2D.Double(shiftX, shiftY);
	}

	public abstract Road nextRoad();

}
