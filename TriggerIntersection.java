//Implements the standard trigger-based light that is used currently
public class TriggerIntersection extends AIntersection {
    private int nextPattern;
    
    public TriggerIntersection(SwarmPoint $here, SwarmCity $parent) {
        super($here, $parent);
        
        nextPattern = -1;
    }
    
    public void tick() {
        super.tick();
        
        boolean[] triggers = new boolean[12];
        
        for(int i = 0; i < 12; i++) {
            if(getRoad(lighttranslator[i][TRANS_SRC]) != null)
                triggers[i] = getRoad(lighttranslator[i][TRANS_SRC]).buffers[lighttranslator[i][TRANS_LANE]].hasWaitingCar();
            else
                triggers[i] = false;
        }

        //This is basically a special FSM
        if(nextPattern == -1) {
            if(triggers[0] && triggers[6]) {
                //Left turn sequence
                if(setPattern(2))
                    nextPattern = 4;
            } else if(triggers[0]) {
                //Left-straight RIGHT
                if(setPattern(6))
                    nextPattern = 4;
            } else if(triggers[6]) {
                //Left-straight LEFT
                if(setPattern(8))
                    nextPattern = 4;
            } else {
                //Straights
                if(setPattern(4))
                    nextPattern = -2;
            }
        } else if(nextPattern == -2) {
            if(triggers[3] && triggers[9]) {
                //Left turn sequence
                if(setPattern(3))
                    nextPattern = 5;
            } else if(triggers[3]) {
                //Left-straight UP
                if(setPattern(7))
                    nextPattern = 5;
            } else if(triggers[9]) {
                //Left-straight DOWN
                if(setPattern(9))
                    nextPattern = 5;
            } else {
                //Straights
                if(setPattern(5))
                    nextPattern = -1;
            }
        } else {
            if(setPattern(nextPattern)) {
                if(nextPattern == 4) {
                    //Only advance on-demand
                    if(triggers[3] || triggers[4] || triggers[9] || triggers[10] || triggers[0] || triggers[6])
                    	nextPattern = -2;
                } else {
                	//Only advance on-demand
                    if(triggers[0] || triggers[1] || triggers[6] || triggers[7] || triggers[3] || triggers[9])        
                    	nextPattern = -1;
                }
            }
        }
    }
}
