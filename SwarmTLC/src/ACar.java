import java.awt.Color;
import java.util.ArrayList;

public abstract class ACar {
	protected ABuilding home;
    protected ArrayList destList;

    public Color carColor;

    protected SwarmCity parent;

    private CarStatisticsElement stats;

    public long curWaitTick;
    private long travelStartTick;

    public ACar(SwarmCity $parent, ABuilding $home) {
        parent = $parent;
        home = $home;
		destList = new ArrayList();
        //carColor = getDest().color;//new Color((float)(Math.random()*0.5f+0.5f),(float)(Math.random()*0.5f + 0.5f),(float)(Math.random()*0.5f+0.5f));

        statsClean();
    }
    
    public final void statsClean() {
        travelStartTick = parent.worldTime;
        stats = new CarStatisticsElement();
    }        

    public final void statsMoved() {
        if(curWaitTick > stats.maxWaitTick)
            stats.maxWaitTick = curWaitTick;
        curWaitTick = 0;
        stats.travelDist++;
    }

    public final void statsWaited() {
        if(curWaitTick == 0)
            stats.cumStops++;
        
        curWaitTick++;
        stats.cumWaitTick++;
    }

    public final CarStatisticsElement getStats() {
        stats.travelTime = parent.worldTime - travelStartTick;
        return stats;
    }

    public final ArrayList getDestList() {
		return parent.generateDestList();
	}

    public ABuilding getDest() {
    	if (destList.size() != 0)
    		return (ABuilding)destList.get(0);
    	return home;  // home
    }

    public void advanceDestList() {
    	if (destList.size() != 0) {
    		destList.remove(0);
    		if (destList.size() == 0)
    			carColor = home.color;
    		else
    			carColor = getDest().color;
    	} else {
    		destList = getDestList();
    		if (destList.size() == 0)
    			carColor = Color.white;  // home.color!!
    		else
    			carColor = getDest().color;
    	}
    }

    public abstract SwarmRoad pickRoad(AIntersection $intersection);

    public abstract int pickLane(SwarmPoint $here, SwarmRoad $enteringRoad, SwarmRoad[] $roads);
}
