import java.awt.event.*;
import java.util.*;

//Implements the standard trigger-based light that is used currently
public class TimedIntersection extends AIntersection {
    private ArrayList switchDelay;
    
    // lightPattern should be an int which indexes the allowed_redless
    // array in AIntersection. currentPositionInLightPattern is the
    // index in the specific pattern of lights.
    private int currentPositionInLightPattern;
    
    private int timeSinceSwitch;
    
    private ArrayList pattern;
    
    boolean tryingToChange;
    
    public TimedIntersection(SwarmPoint $here, SwarmCity $parent) {
        super($here, $parent);
        
        timeSinceSwitch = 0;
        switchDelay = null;
        pattern = null;
        currentPositionInLightPattern = 0;
    }
    
    public void setExtInfo(ArrayList $switchDelay, ArrayList $lightPattern) {
        switchDelay = $switchDelay;
        pattern = $lightPattern;
    }
    
    public void tick() {
        super.tick();
        
        if (tryingToChange) {
        	if (setPattern(((Integer)pattern.get(currentPositionInLightPattern)).intValue())) {
        		tryingToChange = false;
        	}
        } else {
        	timeSinceSwitch++;
        	if (timeSinceSwitch > ((Integer)switchDelay.get(currentPositionInLightPattern)).intValue()) {
        		tryingToChange = true;
        		timeSinceSwitch = 0;
        		currentPositionInLightPattern = 
        		           (currentPositionInLightPattern + 1) % pattern.size();
        	}
        }
     }
    
    public int setDelayTime(String delay,int index) {
    	try {
    		setDelayTime(Integer.parseInt(delay),index);
    		return 1;
    	} catch (NumberFormatException e) {
    		// reset the delay time indicator
    		return 0;
    	}
    }
    
    public int setDelayTime(int delay,int index) {
    	if (delay > 0 && index < switchDelay.size()) {
//    		System.out.println ("Setting " + index + " to " + delay);
    		switchDelay.set(index,new Integer(delay));
    		return 1;
    	} else {
    		return 0;
    	}
    }
    
    public int getDelayTime(int index) {
    	if (index < switchDelay.size()) {
    		return ((Integer)switchDelay.get(index)).intValue();
    	}
    	else
    	{
    		throw new ArrayIndexOutOfBoundsException();
    	}
    }
    
    public ArrayList getDelayTimes() {
    	return switchDelay;
    }
    
    public ArrayList getPattern() {
    	return pattern;
    }
    
    public void mouseClicked(MouseEvent e) {
    	(new TimedIntersectionDialog(here.toString(), this)).show();
    }
    
    public ArrayList getSwitchDelays() {
    	return this.switchDelay;	
    }
    
    public ArrayList getPatterns() {
    	return 	pattern;
    }
    
        
    public void resetIndex() {
    	currentPositionInLightPattern = 0;
    }
}
