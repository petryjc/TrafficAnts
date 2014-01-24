public class SwarmCar extends ACar {
    public SwarmCar(SwarmCity $parent, ABuilding $home) {
        super($parent,$home);
        //dest = new ApartmentBuilding(parent, new SwarmPoint(5,5), 0, Color.cyan);
    }
    
    /*public SwarmRoad pickRoad(AIntersection $intersection) {
  		double r = Math.random();
  		if (r < 0.25)
        	return $intersection.getRoad(SwarmIntersection.ROAD_LEFT);
        if (r < 0.50)
        	return $intersection.getRoad(SwarmIntersection.ROAD_UP);
        if (r < 0.75)
        	return $intersection.getRoad(SwarmIntersection.ROAD_DOWN);
       	return $intersection.getRoad(SwarmIntersection.ROAD_RIGHT);
    }*/

/*
    public SwarmRoad pickRoad(AIntersection $intersection) {
    	SwarmRoad leastBusy = null; $intersection.getRoad(0);  
    	int leastBusyVal = Integer.MAX_VALUE;
    	
    	for (int i=0; i<4; i++){
            if($intersection.getRoad(i) == null || !$intersection.getRoad(i).enabled)
                continue;
            
    		if ((i==0)||(i==3)){
    			if ($intersection.getRoad(i).getNumCarsNeg() < leastBusyVal ||
                        ($intersection.getRoad(i).getNumCarsNeg() == leastBusyVal && parent.random.nextDouble() > .5)) {
    				leastBusy = $intersection.getRoad(i);
                }
    		} else {
    			if ($intersection.getRoad(i).getNumCarsPos() < leastBusyVal ||
                    ($intersection.getRoad(i).getNumCarsPos() == leastBusyVal && parent.random.nextDouble() > .5)) {
    				leastBusy = $intersection.getRoad(i);
    			}    
    		}
    	}
    	return leastBusy;	
    }
 */
    public SwarmRoad pickRoad(AIntersection $intersection) {
        SwarmRoad bestRoad = null;
        double bestRoadDist = Double.MAX_VALUE;
        
        for(int i = 0; i < 4; i++) {
            SwarmRoad thisRoad = $intersection.roads[i];
            if(thisRoad == null || thisRoad.isRoadClosed($intersection.here))
                continue;
            
            double thisDist = thisRoad.otherSide($intersection.here).distanceTo(getDest().getLoc());
            if(thisDist < bestRoadDist || parent.random.nextDouble() > 0.95) {
                bestRoadDist = thisDist;
                bestRoad = thisRoad;
            }
        }
        
        return bestRoad;
    }
    
    public int roadChoice(SwarmRoad[] $roads, int first, int second, SwarmPoint $here){
		if ($roads[first].pastDensityThreshold(positiveRoad($roads, $here, first))){
		 	if ($roads[second].pastDensityThreshold(positiveRoad($roads, $here, second))){
		 		return first;
		 	} else {
		 		return second;
		 	}
		 } else {
			return first;
		 }
	}
    
    public boolean positiveRoad(SwarmRoad[] $roads, SwarmPoint $here, int index){
		return (($roads[index].otherSide($here).getX() > $here.getX()) || ($roads[index].otherSide($here).getY() < $here.getY()));
	}

    public int pickLane(SwarmPoint $here, SwarmRoad $enteringRoad, SwarmRoad[] $roads) {
        //If we are driving into a building, what lane should we use?
        if($here.equals(getDest().getLoc()))
            return 2; //Cars get in the right-hand lane to get off onto a building
                
		if (($here.getX() > -1) && ($here.getX() < parent.intersections.length) && ($here.getY() > -1) && ($here.getY() < parent.intersections[0].length)) {
            
            //This will do simple shortest-path driving
            
            double[] dist = new double[$roads.length];
            
            for(int i = 0; i < $roads.length; i++) {
                if($roads[i] == null || $roads[i].isRoadClosed($here))
                    dist[i] = Double.POSITIVE_INFINITY;
                else if ($roads[i].pastDensityThreshold(isPositiveRoad($roads[i], $here))) 
                	dist[i] = 2*$roads[i].otherSide($here).distanceTo(getDest().getLoc());
                else
                    dist[i] = $roads[i].otherSide($here).distanceTo(getDest().getLoc());
            }
            
            int clIdx = -1;
            double clDist = Double.POSITIVE_INFINITY;
            
            for(int i = 0; i < $roads.length; i++) {
                if($roads[i] == null || $roads[i].isRoadClosed($here))
                    continue;
                
                if((dist[i] == clDist && parent.random.nextDouble() > .5)
                        || dist[i] < clDist || parent.random.nextDouble() > 0.95) {
                    clIdx = i;
                    clDist = dist[i];
                }
            }
            
            return clIdx;

            
/*  This block does some wierd stuff..            
            
			if ($roads[1].otherSide($here).distanceTo(getDest().getLoc()) < $here.distanceTo(getDest().getLoc())) {
    			if ($roads[1].pastDensityThreshold(positiveRoad($roads, $here, 1))){ 
    				if ($roads[0].otherSide($here).distanceTo(getDest().getLoc()) < $roads[2].otherSide($here).distanceTo(getDest().getLoc())) {
    					return roadChoice($roads, 0, 2, $here);
       				} else {
						return roadChoice($roads, 2, 0, $here);
    				}
    			} else {
    				return 1;
    			}
    		} else {
		    	if ($roads[0].otherSide($here).distanceTo(getDest().getLoc()) < $roads[2].otherSide($here).distanceTo(getDest().getLoc())) {
				 	return roadChoice($roads, 0, 2, $here);
				} else {
					return roadChoice($roads, 2, 0, $here);
				}
			} */				
    	} else {
    		return (int) Math.round(parent.random.nextDouble() * 2);
    	}
    }
    
    boolean isPositiveRoad (SwarmRoad $road, SwarmPoint $here){
    	if ($road.otherSide($here).morePositiver($here))
    		return true;
    	return false;
    }
}
