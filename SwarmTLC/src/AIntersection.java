import java.awt.*;
import java.awt.event.*;
import java.util.*;

public abstract class AIntersection implements Clickable {
    public final static int MIN_LIGHT_TIME = 16; //Light must stay on a pattern for this long
    public final static int MIN_STOP_TIME = 6; //Light must stay stopped for this long between switches if a green goes to red

    public final static int ROAD_RIGHT = 0;
    public final static int ROAD_UP = 1;
    public final static int ROAD_LEFT = 2;
    public final static int ROAD_DOWN = 3;
    public final static int ALL_STOP = 0;
    public final static int TICKS_TO_STOP = 2;

    protected SwarmCity parent;
    protected SwarmRoad[] roads;
    protected boolean[] lights;
    protected int currentPattern;
    private int beenThisFor;

    protected SwarmPoint here;
    protected ArrayList buildings;

    //Pixel locations for drawing them
    protected final int[][] lightloc = {{21, 9}, {21, 5}, {21, 1},
                                {9, 1}, {5, 1}, {1, 1},
                                {1, 13}, {1, 17}, {1, 21},
                                {13, 21}, {17, 21}, {21, 21}};

    //All of the valid patterns for the lights
    protected final static int[][] patterns = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //All stop
            {0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1}, //All right

            {1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1},
            {0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0}, //Left/Right

            {0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1}, //Straight and rights

            {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1}}; //Single road/rights

    protected final static int[] patterns_lightson = {0, 4, 2, 2, 2, 2, 4, 4, 3, 3, 3, 3};

    //Current standard light switches that require no all-stops
    protected final static int[][] allowed_redless = {
        {},
        {2, 3, 4, 5, 6, 7, 8, 9},
        {4, 6, 8},
        {5, 7, 9},
        {2, 6, 8},
        {3, 7, 9},
        {2, 4, 8},
        {3, 5, 9},
        {2, 4, 6},
        {3, 5, 7}};

    //Translate a given light into:
    // {Source Road, Destination Road, Source Lane}
    protected final static int TRANS_SRC = 0;
    protected final static int TRANS_DEST = 1;
    protected final static int TRANS_LANE = 2;
    protected final static int[][] lighttranslator = {
        {ROAD_RIGHT,    ROAD_DOWN,  SwarmRoad.BUFFER_NL},
        {ROAD_RIGHT,    ROAD_LEFT,  SwarmRoad.BUFFER_NT},
        {ROAD_RIGHT,    ROAD_UP,    SwarmRoad.BUFFER_NR},

        {ROAD_UP,       ROAD_RIGHT, SwarmRoad.BUFFER_NL},
        {ROAD_UP,       ROAD_DOWN,  SwarmRoad.BUFFER_NT},
        {ROAD_UP,       ROAD_LEFT,  SwarmRoad.BUFFER_NR},

        {ROAD_LEFT,     ROAD_UP,    SwarmRoad.BUFFER_PL},
        {ROAD_LEFT,     ROAD_RIGHT, SwarmRoad.BUFFER_PT},
        {ROAD_LEFT,     ROAD_DOWN,  SwarmRoad.BUFFER_PR},

        {ROAD_DOWN,     ROAD_LEFT,  SwarmRoad.BUFFER_PL},
        {ROAD_DOWN,     ROAD_UP,    SwarmRoad.BUFFER_PT},
        {ROAD_DOWN,     ROAD_RIGHT, SwarmRoad.BUFFER_PR}};
    
    protected final static int[] rightturns = {2, 5, 8, 11};

    public AIntersection(SwarmPoint $here, SwarmCity $parent) {
        here = $here;
        parent = $parent;
        roads = new SwarmRoad[4];
        lights = new boolean[4*3]; // each lane on each road gets a light
        buildings = new ArrayList();
        currentPattern = 0;
        beenThisFor = 0;
    }

    public final void setRoad(int which, SwarmRoad road) {
        roads[which] = road;
    }

    public final SwarmRoad getRoad(int which) {
        return roads[which];
    }

    protected final boolean setPattern(int patternIndex) {
        int askFor = patternIndex;

        //Enforce universal switching rules
        if(currentPattern == 0) {
            beenThisFor++;
            if(beenThisFor < MIN_STOP_TIME)
                return false;
        } else {
            beenThisFor++;
            if(beenThisFor < MIN_LIGHT_TIME)
                return false;

            if(patternIndex != currentPattern) {
                beenThisFor = 0;

                int[] allowed = allowed_redless[currentPattern];

                boolean isAllowed = false;
                for(int i = 0; i < allowed.length; i++) {
                    if(allowed[i] == patternIndex)
                        isAllowed = true;
                }

                if(!isAllowed)
                    patternIndex = 0; //Force red
            }
        }
        
        currentPattern = patternIndex;
        int[] pattern = patterns[patternIndex];
        for(int i = 0; i < pattern.length; i++) {
            if(pattern[i] == 1)
                lights[i] = true;
            else
                lights[i] = false;
        }

        if(currentPattern == askFor)
            return true;
        else
            return false;
    }

    public final void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 26, 26);

        for(int i = 0; i < lights.length; i++) {
            g.setColor(Color.BLACK);
            g.drawRect(lightloc[i][0], lightloc[i][1], 4, 4);

            if(lights[i])
                g.setColor(Color.GREEN);
            else
                g.setColor(Color.RED);

            g.fillRect(lightloc[i][0] + 1, lightloc[i][1] + 1, 3, 3);
        }
        
        g.dispose();
    }
    
    public void mouseClicked(MouseEvent e) {
    }
    
    public final void addBuilding(ABuilding building) {
        buildings.add(building);
    }
    
    public ArrayList getBuildings() {
        return buildings;
    }

    public void setBuildings(ArrayList $buildings) {
        this.buildings = $buildings;
    }

    private final void tickCars() {
        //Move any cars that can cross
        //Check each active light for cars that are waiting to leave
        for(int i = 0; i < lights.length; i++) {
            if(lights[i]) {
                int curRoad, nextRoad, curLane;

                //Fetch the RoadBuffer we are removing a car from
                curRoad = lighttranslator[i][TRANS_SRC];
                nextRoad = lighttranslator[i][TRANS_DEST];
                curLane = lighttranslator[i][TRANS_LANE];

                // If this car wants to stop here, the state of the other roads is ignored
                if(getRoad(curRoad) != null && getRoad(curRoad).hasWaitingCar(curLane)) {
                    ACar car = getRoad(curRoad).getWaitingCar(curLane);

                    if(buildings.contains(car.getDest())) {
                        //Take the car from the waiting car from the road
                        car = getRoad(curRoad).takeWaitingCar(curLane);

                        //Give it to the building
                        car.getDest().addCar(car);
                    } else {
                        //We need to feed the car the 3 other roads at the intersection it
                        //is heading towards now

                        SwarmRoad[] relevantRoads = getRoad(nextRoad).getOtherThree(here);
                        int newLane = car.pickLane(getRoad(nextRoad).otherSide(here), getRoad(nextRoad), relevantRoads);

                        if(newLane == -1) {
                            car.statsWaited();
                            continue;
                        }

                        if(nextRoad == ROAD_DOWN || nextRoad == ROAD_LEFT) {
                            //shift to the negative lanes
                            newLane += 3;
                        } // else the newLane is accurate

                        if(!getRoad(nextRoad).canCarGo(newLane))
                            continue;

                        // We are moving, take the car
                        car = getRoad(curRoad).takeWaitingCar(curLane);
                        //car.statsMoved();

                        getRoad(nextRoad).addCar(newLane, car);
                    }
                }
            }
        }
        
        //right-turn on reds
        for(int i = 0; i < rightturns.length; i++) {
            int curRoad, nextRoad, curLane;

            //Fetch the RoadBuffer we are removing a car from
            curRoad = lighttranslator[rightturns[i]][TRANS_SRC];
            nextRoad = lighttranslator[rightturns[i]][TRANS_DEST];
            curLane = lighttranslator[rightturns[i]][TRANS_LANE];

            // If this car wants to stop here, the state of the other roads is ignored
            if(getRoad(curRoad) != null && getRoad(curRoad).hasWaitingCar(curLane)) {
                ACar car = getRoad(curRoad).getWaitingCar(curLane);

                if(buildings.contains(car.getDest())) {
                    //Take the car from the waiting car from the road
                    car = getRoad(curRoad).takeWaitingCar(curLane);

                    //Give it to the building
                    car.getDest().addCar(car);
                } else {
                    //We need to feed the car the 3 other roads at the intersection it
                    //is heading towards now

                    SwarmRoad[] relevantRoads = getRoad(nextRoad).getOtherThree(here);
                    int newLane = car.pickLane(getRoad(nextRoad).otherSide(here), getRoad(nextRoad), relevantRoads);

                    if(newLane == -1) {
                        car.statsWaited();
                        continue;
                    }

                    if(nextRoad == ROAD_DOWN || nextRoad == ROAD_LEFT) {
                        //shift to the negative lanes
                        newLane += 3;
                    } // else the newLane is accurate

                    if(!getRoad(nextRoad).canCarGo(newLane))
                        continue;

                    // We are moving, take the car
                    car = getRoad(curRoad).takeWaitingCar(curLane);
                    //car.statsMoved();

                    getRoad(nextRoad).addCar(newLane, car);
                }
            }
        }
            
    }

    private final void tickBuildings() {
        //This block of code will randomize which building will get to dump cars first
        //Perhaps we should be doing this in a more systematic way?
        int[] bldgOrder = new int[buildings.size()];
        for(int i = 0; i < bldgOrder.length; i++) {
            bldgOrder[i] = -1;
        }
        for(int i = 0; i < bldgOrder.length; i++) {
            int idx = (int) Math.round(parent.random.nextDouble() * bldgOrder.length);

            boolean done = false;
            for(int j = idx; j < bldgOrder.length; j++) {
                if(bldgOrder[j] == -1) {
                    bldgOrder[j] = i;
                    done = true;
                    break;
                }
            }
            if(!done) {
                for(int j = 0; j < idx; j++) {
                    if(bldgOrder[j] == -1)
                        bldgOrder[j] = i;
                }
            }
        }

        for(int i = 0; i < bldgOrder.length; i++) {
            ABuilding bldg = (ABuilding) buildings.get(bldgOrder[i]);

            //Give it a tick call
            bldg.tick();

            ACar car;
            while ((car = bldg.getWaitingCar()) != null) {
                    SwarmRoad nextRoad = car.pickRoad(this);
                    
                    if(nextRoad == null) {
                        bldg.keepWaitingCar(car);
                        break;
                    }                        

                    SwarmRoad[] relevantRoads = nextRoad.getOtherThree(here);
                    int lane = car.pickLane(nextRoad.otherSide(here), null, relevantRoads);

                    if(lane == -1) {
                        bldg.keepWaitingCar(car);
                        break;
                    }

                    if(nextRoad == getRoad(ROAD_DOWN) || nextRoad == getRoad(ROAD_LEFT)) {
                        //shift to the negative lanes
                        lane += 3;
                    }

                    if(!nextRoad.canCarGo(lane)) {
                        bldg.keepWaitingCar(car);
                        break;
                    }

                    bldg.takeWaitingCar(car);
                    car.statsClean();

                    nextRoad.addCar(lane, car);
                }
        }
    }

    public void tick() {
        tickCars();
        tickBuildings();
    }
}
