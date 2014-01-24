public class MovieTheaterBuilding extends ABuilding {
	// The amount of time between releases
	int timeBetweenMovies;

	// The amount of time since a car last left the building.
	int timeSinceLastRelease;

	// Remembers the number of cars that were in the building when
	// the building started releasing cars and decrements until 0.
	// At this point the building stops releasing cars until the next
	// timeBetweenMovies ticks have passed.
	int carsToRemove;

	// After every unit of system time equal to timeBetweenMovies
	// this building releases all of its cars. Then resets the timer.
	// This variable indicates what state it is in (releasing cars or not).
	boolean releasingCars = false;

    public MovieTheaterBuilding(SwarmCity $parent, SwarmPoint $loc,
    						 int initCars, int[] $attractiveness, int[] $releaseInfo) {
        super($parent, $loc, initCars, $attractiveness, $releaseInfo);

        timeSinceLastRelease = 0;
    }

    // Does this function need to reset statistics of the car
    // or report them to any other functions?
    public void addCar(ACar car) {
        this.carsAtHome.add(car);
        parent.carStats.add(car.getStats());
        car.statsClean();
    }

	// Does this function need to reset statistics of the car
    // or report them to any other functions?
    public void takeWaitingCar(ACar car) {
        // Since we are removing the cars in "get" we don't need to do anything here.
//    	this.carsAtHome.remove(car);
        car.advanceDestList();
    }

    public boolean wantsToReleaseCars() {
    	return releasingCars;
    }

    public ACar getWaitingCar() {
    	if (!wantsToReleaseCars()) {
    		return null;
    	}
    	else
    	{
    		if (this.carsAtHome.size() != 0) {
	    		//this.carsToRemove--;

	    		return (ACar) this.carsAtHome.removeFirst();
	    	}
	    	else
	    	{
	    		// We are done releasing cars
	    		this.releasingCars = false;
	    		return null;
	    	}
    	}
    }

    public void keepWaitingCar(ACar car) {
    	// This car could not enter the intersection, so
    	// place it back at the head of the queue (i.e. it
    	// does not lose its turn
    	this.carsAtHome.addFirst(car);
    }

    public void tick() {
    	if (!this.releasingCars) {
	    	this.timeSinceLastRelease++;
	    	if (this.timeSinceLastRelease >= getReleaseTime()) {
	    		this.releasingCars = true;
	    		this.timeSinceLastRelease = 0;
	    	}
	    }
    }
    
    public int getReleaseTime() {
   		for (int x = 1; x < releaseInfo.length; x+=3) {
    		if (releaseInfo[x] <= parent.worldTime % parent.DAY_LENGTH &&
    			releaseInfo[x+1] >= parent.worldTime % parent.DAY_LENGTH)
    			return releaseInfo[x+2];
    	}

    	return releaseInfo[0];
    }


    public int getBuildingAttractiveness() {
    	for (int x = 1; x < attractiveness.length; x+=3) {
    		if (attractiveness[x] <= parent.worldTime % parent.DAY_LENGTH &&
    			attractiveness[x+1] >= parent.worldTime % parent.DAY_LENGTH)
    			return attractiveness[x+2];
    	}

    	return attractiveness[0];
    }
}

