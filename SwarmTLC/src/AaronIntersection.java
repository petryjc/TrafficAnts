public class AaronIntersection extends AIntersection {
    public AaronIntersection(SwarmPoint $here, SwarmCity $parent) {
        super($here, $parent);
    }

    public void tick() {
        super.tick();

        setPattern(decideNextLightPattern());
    }

    private int decideNextLightPattern() {
        // Stores the number of cars in each lane on adjacent roads
        int[] carsInLanes = new int[12];
        int[] recievingLanes = new int[4];
        
        if(this.roads[ROAD_RIGHT] != null) {
            carsInLanes[0] = this.roads[ROAD_RIGHT].getNumCars(3);
            carsInLanes[1] = this.roads[ROAD_RIGHT].getNumCars(4);
            carsInLanes[2] = this.roads[ROAD_RIGHT].getNumCars(5);
            
            recievingLanes[0] = this.roads[ROAD_RIGHT].getNumCarsPos();
        }

        if(this.roads[ROAD_UP] != null) {
           	carsInLanes[3] = this.roads[ROAD_UP].getNumCars(3);
           	carsInLanes[4] = this.roads[ROAD_UP].getNumCars(4);
           	carsInLanes[5] = this.roads[ROAD_UP].getNumCars(5);
           	
           	recievingLanes[1] = this.roads[ROAD_UP].getNumCarsPos();
        }

        if(this.roads[ROAD_LEFT] != null) {
          	carsInLanes[6] = this.roads[ROAD_LEFT].getNumCars(0);
           	carsInLanes[7] = this.roads[ROAD_LEFT].getNumCars(1);
           	carsInLanes[8] = this.roads[ROAD_LEFT].getNumCars(2);
           	
           	recievingLanes[2] = this.roads[ROAD_LEFT].getNumCarsNeg();
        }
        
        if(this.roads[ROAD_DOWN] != null) {
            carsInLanes[9] = this.roads[ROAD_DOWN].getNumCars(0);
           	carsInLanes[10] = this.roads[ROAD_DOWN].getNumCars(1);
           	carsInLanes[11] = this.roads[ROAD_DOWN].getNumCars(2);
        
        	recievingLanes[3] = this.roads[ROAD_DOWN].getNumCarsNeg();
        }

		// Now we will compare all of the patterns and see which one will allow
		// this intersection to pick the lanes with the most total cars, with some
		// threshold for changing (i.e. you don't want to change back and forth all the time)
		int[] scores = new int[patterns.length];
		int maxScore = -1;
		int maxIndex = -1;
		for (int pattern = 0; pattern < patterns.length; pattern++) {
			scores[pattern] = 0;
			for (int i = 0; i < 12; i++) {
				if (patterns[pattern][i] == 1)
					scores[pattern] += (carsInLanes[i] - (recievingLanes[getCorrespondingLane(i)]/3));
			}
			if (scores[pattern] > maxScore) { //pick the best score
                maxScore = scores[pattern];
                maxIndex = pattern;
			}
		}

		return maxIndex;
    }

	public int getCorrespondingLane(int originLane){
		if ((originLane == 0) || (originLane == 4) ||(originLane == 8)){
			return 1;
		} else if ((originLane == 1) || (originLane == 5) ||(originLane == 9)){
			return 2;
		} else if ((originLane == 2) || (originLane == 6) ||(originLane == 10)){
			return 3;
		} else if ((originLane == 3) || (originLane == 7) ||(originLane == 11)){	
			return 0;
		}	
		return 0;	
	}
}
