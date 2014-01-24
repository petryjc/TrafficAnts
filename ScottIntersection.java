import java.awt.event.*;

public class ScottIntersection extends AIntersection {
    public ScottIntersection(SwarmPoint $here, SwarmCity $parent) {
        super($here, $parent);
        
        currentPattern = 0;
        setPattern(0);
        
        flushNeed = new int[4];
        for(int i = 0; i < 4; i++)
            flushNeed[i] = 0;
    }
    
    private final static double LISTEN = 1.0;
    
    private final static int[] reverseroad = {ROAD_LEFT, ROAD_DOWN, ROAD_RIGHT, ROAD_UP};
    
    public int[] flushNeed;
    
    public void tick() {
        super.tick();

        int[] carsInLanes = new int[12];
        
        for(int i = 0; i < 4; i++) {
            SwarmRoad dest = getRoad(i);
            
            if(dest != null) {
                SwarmPoint other = dest.otherSide(here);
                
                ((ScottIntersection) parent.intersections[other.x][other.y]).flushNeed[reverseroad[i]] = 0;
            }
        }

        for(int i = 0; i < lighttranslator.length; i++) {
            SwarmRoad src = getRoad(lighttranslator[i][TRANS_SRC]);
            SwarmRoad dest = getRoad(lighttranslator[i][TRANS_DEST]);
            int srcLane = lighttranslator[i][TRANS_LANE]; 
            
            if(src != null) {
                carsInLanes[i] = src.getNumCars(srcLane);

                // if a car has waited at least a light length start giving them more attention
                if(src.buffers[srcLane].buffer[0] != null
                        && (src.buffers[srcLane].buffer[0].curWaitTick > (AIntersection.MIN_LIGHT_TIME/2)))
                    carsInLanes[i] += (src.buffers[srcLane].buffer[0].curWaitTick - (AIntersection.MIN_LIGHT_TIME/2));                 
                
                if(dest != null) {
                    SwarmPoint other = dest.otherSide(here);
                    ((ScottIntersection) parent.intersections[other.x][other.y]).flushNeed[reverseroad[lighttranslator[i][TRANS_DEST]]] += carsInLanes[i] + (flushNeed[lighttranslator[i][TRANS_SRC]] / 3.0);
                }
            }
        }
            
        int[] scores = new int[patterns.length];
        int maxScore = -1;
        int maxIndex = -1;
        for (int pattern = 0; pattern < patterns.length; pattern++) {
            scores[pattern] = 0;
            for (int i = 0; i < 12; i++) {
                if (patterns[pattern][i] == 1) {
                    if(LISTEN == 1) {
                        scores[pattern] += carsInLanes[i] + (flushNeed[lighttranslator[i][TRANS_DEST]] / 3);
                    } else {
                    	scores[pattern] += carsInLanes[i] + Math.pow((flushNeed[lighttranslator[i][TRANS_DEST]] / 3), LISTEN);
                    }
                }
            }
            if (scores[pattern] > maxScore) { //pick the best score
                maxScore = scores[pattern];
                maxIndex = pattern;
            }
        }
        
        if(maxIndex != currentPattern)
            setPattern(maxIndex);
    }
    
    public void mouseClicked(MouseEvent e) {
        (new ScottIntersectionDialog(here.toString(), this)).show();
    }
}
