import java.util.ArrayList;

public abstract class Car extends Time{
	static ArrayList<Car> carList = new ArrayList<Car>();
	Road currentRoad;
	double distanceAlongRoad;
	Intersection start;
	final Intersection destination;
	int startTime;
	double endTime;
	boolean finished = false;
	
	public Car(Intersection start, Intersection destionation, int startTime) {
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
		if(!finished) {
			if(startTime == Time.ticks) {
				this.currentRoad = this.nextRoad();
				if(this.currentRoad == null) {
					if(this.start == this.destination) {
						System.out.println("You were already there you twat!!!");
					} else {
						System.out.println("A star could not get from intersection " + this.start.id + " to " + this.destination.id);
					}
					destinationReached();
					return;
				}
				this.currentRoad.currentCars.add(this);
				this.distanceAlongRoad = 0;
			} else if(Time.ticks > startTime){
				Car closestFront = null;
				for(Car c : currentRoad.currentCars) { 
					if(c != this && c.distanceAlongRoad > this.distanceAlongRoad && (closestFront == null || closestFront != null && c.distanceAlongRoad < closestFront.distanceAlongRoad)) { 
						closestFront = c;
					}
				}
				double distancePossible = 0;
				if(closestFront == null) {
					distancePossible = distanceIncrement();
				} else {
					distancePossible = Math.max(Math.min(distanceIncrement(), closestFront.distanceAlongRoad - this.distanceAlongRoad - bufferZone()), 0);
				}
				if((this.currentRoad.distance() - this.distanceAlongRoad) < distancePossible) { //We're at the end of the road
					if(this.currentRoad.end.currentRoad == this.currentRoad) { //And the light is green
						double remainingTick = (this.currentRoad.distance() - this.distanceAlongRoad)/ this.currentRoad.speedLimit;
						Road next = this.nextRoad();
						if(next == null) {
							if(this.currentRoad.end == this.destination) {
								this.currentRoad.currentCars.remove(this);
								//System.out.println("This worked sorta " + (endTime - startTime) + " as a " + this.getClass());
							} else {
								System.out.println("Got to intersection " + this.currentRoad.end.id + " but couldn't get to " + this.destination.id);
							}
							destinationReached();
							return;
						}
						this.currentRoad.currentCars.remove(this);
						this.currentRoad = next;
						this.currentRoad.currentCars.add(this);
						this.distanceAlongRoad = this.distanceIncrement() * remainingTick;
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
	
	public abstract Road nextRoad();
	
}
