import java.util.ArrayList;


public abstract class Car extends Time{
	public static ArrayList<Car> carList = new ArrayList<Car>();
	Road currentRoad;
	double distanceAlongRoad;
	Intersection start;
	final Intersection destination;
	int startTime;
	double endTime;
	
	public Car(Intersection start, Intersection destionation, int startTime) {
		this.start = start;
		this.destination = destionation;
		this.startTime = startTime;
	}
	
	public double bufferZone() {
		return 2;
	}
	
	public double distanceIncrement() {
		return this.currentRoad.speedLimit;
	}
	
	@Override
	public void advanceTime() {
		if(startTime == Time.ticks) {
			this.currentRoad = start.getOutgoingRoads().get(0);
			this.currentRoad.currentCars.add(this);
			this.distanceAlongRoad = 0;
		} else if(startTime > Time.ticks){
			Car closestFront = null;
			for(Car c : currentRoad.currentCars) { 
				if(c.distanceAlongRoad > this.distanceAlongRoad && closestFront == null || c.distanceAlongRoad < closestFront.distanceAlongRoad) { 
					closestFront = c;
				}
			}
			double distancePossible = 0;
			if(closestFront == null) {
				distancePossible = distanceIncrement();
			} else {
				distancePossible = Math.min(distanceIncrement(), closestFront.distanceAlongRoad - this.distanceAlongRoad - bufferZone());
			}
			if((this.currentRoad.distance() - this.distanceAlongRoad) < distancePossible) { //We're at the end of the road
				if(this.currentRoad.end.currentRoad == this.currentRoad) { //And the light is green
					double remainingTick = (this.currentRoad.distance() - this.distanceAlongRoad)/ this.currentRoad.speedLimit;
					Road next = this.nextRoad();
					if(next == null) {
						if(this.currentRoad.end == this.destination) {
							this.endTime = Time.ticks + 1 - remainingTick;
						}
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
	
	public abstract Road nextRoad();
	
}
