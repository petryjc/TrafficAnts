public class SwarmIntersection2 extends AIntersection {
    int greentime;

    public SwarmIntersection2(SwarmPoint $here, SwarmCity $parent) {
        super($here, $parent);
    	greentime = 0;
    }

    public void tick() {
        super.tick();

       	setPattern(decideNextLightPattern());
    }

    private int decideNextLightPattern() {
        // Stores the number of cars in each lane on adjacent roads
        int[] carsInLanes = new int[12];

        if(this.roads[ROAD_RIGHT] != null) {
            carsInLanes[0] = this.roads[ROAD_RIGHT].getNumCars(3);
            carsInLanes[1] = this.roads[ROAD_RIGHT].getNumCars(4);
            carsInLanes[2] = this.roads[ROAD_RIGHT].getNumCars(5);
        }

        if(this.roads[ROAD_UP] != null) {
           	carsInLanes[3] = this.roads[ROAD_UP].getNumCars(3);
           	carsInLanes[4] = this.roads[ROAD_UP].getNumCars(4);
           	carsInLanes[5] = this.roads[ROAD_UP].getNumCars(5);
        }

        if(this.roads[ROAD_LEFT] != null) {
          	carsInLanes[6] = this.roads[ROAD_LEFT].getNumCars(0);
           	carsInLanes[7] = this.roads[ROAD_LEFT].getNumCars(1);
           	carsInLanes[8] = this.roads[ROAD_LEFT].getNumCars(2);
        }

        if(this.roads[ROAD_DOWN] != null) {
            carsInLanes[9] = this.roads[ROAD_DOWN].getNumCars(0);
           	carsInLanes[10] = this.roads[ROAD_DOWN].getNumCars(1);
           	carsInLanes[11] = this.roads[ROAD_DOWN].getNumCars(2);
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
					scores[pattern] += carsInLanes[i];
			}
			if (scores[pattern] > maxScore) { //pick the best score
                maxScore = scores[pattern];
                maxIndex = pattern;
			}
		}

        greentime--;
        if (greentime <= 0) {
			greentime = maxScore;
			return maxIndex;
		}
		return this.currentPattern;
    }
}
