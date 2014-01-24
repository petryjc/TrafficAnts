public class StraightCar extends ACar {
    public StraightCar(SwarmCity $parent, ABuilding $home) {
        super($parent,$home);
        //dest = new ApartmentBuilding(parent, new SwarmPoint(5,5), 0, Color.cyan);
    }
    
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
            
            if (($here.getX() > 0) && ($here.getX() < (parent.intersections.length - 1)) && ($here.getY() > 0) && ($here.getY() < (parent.intersections[0].length -1))) {
            	if (($roads[1] != null) && !($roads[1].isRoadClosed($here))){
            		if ($roads[1].otherSide($here).distanceTo(getDest().getLoc()) < $here.distanceTo(getDest().getLoc())){
            			if (!$roads[1].pastDensityThreshold(isPositiveRoad($roads[1], $here))){
//            				System.out.println("Going straight");
            				
            				return 1;
            			}
            		}
            	}                      		
                        
	            for(int i = 0; i < $roads.length; i+=2) {
	                if($roads[i] == null || $roads[i].isRoadClosed($here))
	                    dist[i] = Double.POSITIVE_INFINITY;
	                else if ($roads[i].pastDensityThreshold(isPositiveRoad($roads[i], $here))) 
	                	dist[i] = 2*$roads[i].otherSide($here).distanceTo(getDest().getLoc());
	                else
	                    dist[i] = $roads[i].otherSide($here).distanceTo(getDest().getLoc());
	            }
	         }
	         
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
