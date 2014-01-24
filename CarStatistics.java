import java.util.*;

public class CarStatistics extends Vector {
    private static final long serialVersionUID = 4051047484086170673L;

    public final static int WAIT_PER_DIST = 0;
    public final static int TRAVEL_TIME = 1;
    public final static int WORST_WAIT = 2;
    public final static int JITTER = 3;
    public SwarmCity city;

	public CarStatistics(SwarmCity $city) {
		city = $city;
	}

    public double getMeanStat(int which) {
    	if(size() == 0)
    	    return Double.NaN;

    	double total = 0;

		for(int i = 0; i < this.size(); i++) {
			CarStatisticsElement aElem = (CarStatisticsElement) this.get(i);

			if(which == WAIT_PER_DIST)
				total += ((double) aElem.cumWaitTick) / ((double) aElem.travelTime);
			else if(which == TRAVEL_TIME)
				total += aElem.travelTime;
			else if(which == WORST_WAIT)
				total += aElem.maxWaitTick;
			else if(which == JITTER)
				total += ((double) aElem.cumStops) / ((double) aElem.travelTime);
		}

		return total / this.size();
    }

    public double getMedianStat(int which) {
    	if(size() == 0)
    	    return Double.NaN;

        ArrayList stat = new ArrayList();

        for(int i = 0; i < this.size(); i++) {
            CarStatisticsElement aElem = (CarStatisticsElement) this.get(i);

            if(which == WAIT_PER_DIST)
                stat.add(new Double(((double) aElem.cumWaitTick) / ((double) aElem.travelTime)));
            else if(which == TRAVEL_TIME)
                stat.add(new Double(aElem.travelTime));
            else if(which == WORST_WAIT)
                stat.add(new Double(aElem.maxWaitTick));
            else if(which == JITTER)
                stat.add(new Double(((double) aElem.cumStops) / ((double) aElem.travelTime)));
        }

        Collections.sort(stat);

        return ((Double) stat.get(stat.size() / 2)).doubleValue();
    }

    public double getMaxStat(int which) {
    	if(size() == 0)
    	    return Double.NaN;

        double max = 0;

        for(int i = 0; i < this.size(); i++) {
            CarStatisticsElement aElem = (CarStatisticsElement) this.get(i);

            double thisOne = 0;
            if(which == WAIT_PER_DIST)
                thisOne = ((double) aElem.cumWaitTick) / ((double) aElem.travelTime);
            else if(which == TRAVEL_TIME)
                thisOne = aElem.travelTime;
            else if(which == WORST_WAIT)
                thisOne = aElem.maxWaitTick;
            else if(which == JITTER)
                thisOne = ((double) aElem.cumStops) / ((double) aElem.travelTime);

            if(thisOne > max)
                max = thisOne;

        }

        return max;
    }

    public double getMinStat(int which) {
    	if(size() == 0)
    	    return Double.NaN;

        double min = Double.MAX_VALUE;

        for(int i = 0; i < this.size(); i++) {
            CarStatisticsElement aElem = (CarStatisticsElement) this.get(i);

            double thisOne = 0;
            if(which == WAIT_PER_DIST)
                thisOne = ((double) aElem.cumWaitTick) / ((double) aElem.travelTime);
            else if(which == TRAVEL_TIME)
                thisOne = aElem.travelTime;
            else if(which == WORST_WAIT)
                thisOne = aElem.maxWaitTick;
            else if(which == JITTER)
                thisOne = ((double) aElem.cumStops) / ((double) aElem.travelTime);

            if(thisOne < min)
                min = thisOne;
        }

        return min;
    }
}
