public class CarStatisticsElement {
    public long cumWaitTick;
    public long maxWaitTick; 
    public long travelDist;
    public long travelTime;
    public long cumStops;
    
    public CarStatisticsElement() {
        cumWaitTick = 0;
        maxWaitTick = 0;
        travelDist = 0;
        travelTime = 0;
        cumStops = 0;
    }
    
    public CarStatisticsElement(ACar car) {
        CarStatisticsElement stats = car.getStats();
        
        cumWaitTick = stats.cumWaitTick;
        maxWaitTick = stats.maxWaitTick;
        travelDist = stats.travelDist;
        travelTime = stats.travelTime;
        cumStops = stats.cumStops;
    }
    
    public String toString() {
        return cumWaitTick + "\t" + maxWaitTick + "\t" + travelDist + "\t" + travelTime + "\t" + cumStops;
    }
}
