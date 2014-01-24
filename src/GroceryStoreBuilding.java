public class GroceryStoreBuilding extends ABuilding {

	int carReleaseCooldown;

	// The amount of time since a car last left the building.
	int timeSinceLastRelease;

    public GroceryStoreBuilding(SwarmCity $parent, SwarmPoint $loc,
    						 int initCars, int[] $attractiveness, int[] $releaseInfo) {
        super($parent, $loc, initCars, $attractiveness, $releaseInfo);
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
//       	System.out.println ("release time " + getReleaseTime());

    	// If there are cars to release and it has been sufficiently
    	// long since the last release, then we want to release
    	if (!this.carsAtHome.isEmpty() &&
    	    timeSinceLastRelease > getReleaseTime()) {
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }

    public ACar getWaitingCar() {
    	if (!wantsToReleaseCars()) {
    		return null;
    	}
    	else
    	{
    		// reset time since last release
    		this.timeSinceLastRelease = 0;

    		return (ACar) this.carsAtHome.removeFirst();
    	}
    }

    public void keepWaitingCar(ACar car) {
    	// This car could not enter the intersection, so
    	// place it back at the head of the queue (i.e. it
    	// does not lose its turn
    	this.carsAtHome.addFirst(car);
    }

    public void tick() {
    	this.timeSinceLastRelease++;
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

