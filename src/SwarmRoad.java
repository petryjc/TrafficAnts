import java.awt.*;
import java.awt.event.*;

public class SwarmRoad implements Clickable {
    //BUFFER_[Direction][Lane]
    //Direction is abstract in this context since the road itself has
    //no knowledge of its own orientation
    public static int ROAD_PIXELS = 100;

    public static int BUFFER_PL = 0;
    public static int BUFFER_PT = 1;
    public static int BUFFER_PR = 2;
    public static int BUFFER_NL = 3;
    public static int BUFFER_NT = 4;
    public static int BUFFER_NR = 5;

    public SwarmRoadBuffer[] buffers;

    private SwarmCity parent;
    public SwarmPoint[] endPoints;

    private boolean vertical;

    private int maxNumCars;

    public boolean[] enabled;

    public SwarmRoad(SwarmCity $parent, SwarmPoint $pointA, SwarmPoint $pointB) {
        parent = $parent;

        endPoints = new SwarmPoint[2];
        endPoints[0] = $pointA;
        endPoints[1] = $pointB;

        if($pointA.y != $pointB.y)
            vertical = true;
        else
            vertical = false;

        enabled = new boolean[2];
        enabled[0] = true;
        enabled[1] = true;

        maxNumCars = 0;

        buffers = new SwarmRoadBuffer[6];
        for(int i = 0; i < 6; i++)
            buffers[i] = new SwarmRoadBuffer();
    }

    public SwarmRoad(SwarmCity $parent, SwarmPoint $pointA, SwarmPoint $pointB, boolean side1, boolean side2) {
    	this($parent, $pointA, $pointB);

        enabled = new boolean[2];
        enabled[0] = side1;
        enabled[1] = side2;
    }

    public void paint(Graphics g) {
        Rectangle r = g.getClipBounds();

        int carsPerLine = buffers[0].buffer.length;

        if(enabled[0])
            g.setColor(Color.LIGHT_GRAY);
        else
            g.setColor(Color.GRAY);

        if(vertical)
        	g.fillRect(0, 0, r.width / 2, ROAD_PIXELS);
        else
            g.fillRect(0, 0, ROAD_PIXELS, r.height / 2);

        if(enabled[1])
            g.setColor(Color.LIGHT_GRAY);
        else
            g.setColor(Color.GRAY);

        if(vertical)
        	g.fillRect(r.width / 2, 0, r.width / 2, ROAD_PIXELS);
        else
            g.fillRect(0, r.height / 2, ROAD_PIXELS, r.height / 2);

        g.setColor(Color.DARK_GRAY);
        Stroke centerLine = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {ROAD_PIXELS/SwarmRoadBuffer.numPossCars}, ROAD_PIXELS/SwarmRoadBuffer.numPossCars);
        ((Graphics2D) g).setStroke(centerLine);
        if(vertical)
            g.drawLine(r.width / 2, 0, r.width / 2, ROAD_PIXELS);
        else
            g.drawLine(0, r.height / 2, ROAD_PIXELS, r.height / 2);

        ((Graphics2D) g).setStroke(new BasicStroke(1));

        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < carsPerLine; j++) {
                if(buffers[i].buffer[j] != null) {
                    Color cColor = Color.WHITE;

                    // Rather than fix this race condition, we'll just trap it
                    try {
                        cColor = buffers[i].buffer[j].carColor;
                    } catch(NullPointerException e) { }

                    int x,y,h,w;

                    if(i < 3) {
                        if(vertical) {
                            int width = r.width / 6;
                            float carHeight = ((float) r.height) / ((float) carsPerLine);

                            x = (r.width / 2) + (width * i);
                            y = r.height - ((int) (carHeight * (j + 1))) - 1;
                            w = width;
                            h = (int) (carHeight < 1 ? 1 : carHeight);
                        } else {
                            int height = r.height / 6;
                            float carWidth = ((float) r.width) / ((float) carsPerLine);

                            x = (int) (carWidth * j);
                            y = (r.height / 2) + (height * i + 1);
                            w = (int) (carWidth < 1 ? 1 : carWidth);
                            h = height;
                        }
                    } else {
                        if(vertical) {
                            int width = r.width / 6;
                            float carHeight = ((float) r.height) / ((float) carsPerLine);

                            x = -1 * (width * (i - 5)) + 1;
                            y = (int) (carHeight * j);
                            w = width;
                            h = (int) (carHeight < 1 ? 1 : carHeight);
                        } else {
                            int height = r.height / 6;
                            float carWidth = ((float) r.width) / ((float) carsPerLine);

                            x = (int) (r.width - (carWidth * (j + 1)));
                            y = -1 * (height * (i - 5)) + 1;
                            w = (int) (carWidth < 1 ? 1 : carWidth);
                            h = height;
                        }
                    }


                    g.setColor(cColor);
                    g.fillRect(x, y, w, h);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, w, h);
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3) {
            enabled[0] = !(enabled[0] | enabled[1]);
            enabled[1] = enabled[0];
            return;
        }

        if(vertical) {
            if(e.getX() < 14)
                enabled[0] = !enabled[0];
            else
                enabled[1] = !enabled[1];
        } else {
            if(e.getY() < 14)
                enabled[0] = !enabled[0];
            else
                enabled[1] = !enabled[1];
        }
    }

    public int statsMaxCars() {
        return maxNumCars;
    }

    public void tick() {
        for(int i = 0; i < buffers.length; i++)
            buffers[i].tick();

        if(maxNumCars < getNumCars())
            maxNumCars = getNumCars();
    }

    public boolean ableToEnter(int idx) {
        return (buffers[idx].buffer[0] == null);
    }

    public SwarmPoint otherSide(SwarmPoint $here) {
        if((endPoints[0].x == $here.x)
                && (endPoints[0].y == $here.y))
            return endPoints[1];

        return endPoints[0];
    }

    public SwarmRoad[] getOtherThree(SwarmPoint $here) {
        SwarmPoint theOtherSide = otherSide($here);

        SwarmRoad[] theOtherThree = new SwarmRoad[3];

        if(theOtherSide.x < 0 || theOtherSide.x >= parent.intersections.length
                || theOtherSide.y < 0 || theOtherSide.y >= parent.intersections[0].length)
            return null;

        if(theOtherSide.y > $here.y) {
            //We are the down road
            theOtherThree[0] = parent.intersections[theOtherSide.x][theOtherSide.y].getRoad(SwarmIntersection.ROAD_RIGHT);
            theOtherThree[1] = parent.intersections[theOtherSide.x][theOtherSide.y].getRoad(SwarmIntersection.ROAD_DOWN);
            theOtherThree[2] = parent.intersections[theOtherSide.x][theOtherSide.y].getRoad(SwarmIntersection.ROAD_LEFT);
        } else if(theOtherSide.y < $here.y) {
            //We are the up road
            theOtherThree[0] = parent.intersections[theOtherSide.x][theOtherSide.y].getRoad(SwarmIntersection.ROAD_LEFT);
            theOtherThree[1] = parent.intersections[theOtherSide.x][theOtherSide.y].getRoad(SwarmIntersection.ROAD_UP);
            theOtherThree[2] = parent.intersections[theOtherSide.x][theOtherSide.y].getRoad(SwarmIntersection.ROAD_RIGHT);
        } else if(theOtherSide.x > $here.x) {
            //We are the right road
            theOtherThree[0] = parent.intersections[theOtherSide.x][theOtherSide.y].getRoad(SwarmIntersection.ROAD_UP);
            theOtherThree[1] = parent.intersections[theOtherSide.x][theOtherSide.y].getRoad(SwarmIntersection.ROAD_RIGHT);
            theOtherThree[2] = parent.intersections[theOtherSide.x][theOtherSide.y].getRoad(SwarmIntersection.ROAD_DOWN);
        } else if(theOtherSide.x < $here.x) {
            //We are the left road
            theOtherThree[0] = parent.intersections[theOtherSide.x][theOtherSide.y].getRoad(SwarmIntersection.ROAD_DOWN);
            theOtherThree[1] = parent.intersections[theOtherSide.x][theOtherSide.y].getRoad(SwarmIntersection.ROAD_LEFT);
            theOtherThree[2] = parent.intersections[theOtherSide.x][theOtherSide.y].getRoad(SwarmIntersection.ROAD_UP);
        } else {
            System.out.println("Can't figure out the other three");
        }

        return theOtherThree;
    }

    public boolean hasWaitingCar(int idx) {
        return buffers[idx].hasWaitingCar();
    }

    public ACar takeWaitingCar(int idx) {
        return buffers[idx].takeWaitingCar();
    }

    public ACar getWaitingCar(int idx) {
        return buffers[idx].getWaitingCar();
    }

    public boolean addCar(int idx, ACar car) {
        return buffers[idx].addCar(car);
    }

    public boolean canCarGo(int $lane) {
        if($lane == -1)
            return false;
        return (buffers[$lane].buffer[0] == null);
    }

    public int getNumCars(int lane) {
    	return buffers[lane].getNumCars();
    }

    public int getNumPosCars() {
        return getNumCars(0) + getNumCars(1) + getNumCars(2);
    }

    public int getNumNegCars() {
        return getNumCars(3) + getNumCars(4) + getNumCars(5);
    }

   	public int getNumCars() {
        int total = 0;
        for(int i = 0; i < buffers.length; i++) {
			if (buffers[i] != null)
				total=total+buffers[i].getNumCars();
        }
        return total;
    }

    //for up and right
    public int getNumCarsPos() {
    	int total=0;
    	for (int i=0; i<3; i++){
    		total=total+buffers[i].getNumCars();
    	}
    	return total;
    }

    //for down and left
    public int getNumCarsNeg() {
    	int total=3;
    	for (int i=0; i<6; i++){
    		total=total+buffers[i].getNumCars();
    	}
    	return total;
    }

	public int getStaggeredTriggerCount(int lane) {
		int total = 0;
		if (buffers[lane].buffer[0] != null)
			total++;
		if (buffers[lane].buffer[(int)(SwarmRoadBuffer.numPossCars*0.5)] != null)
			total++;
		if (buffers[lane].buffer[(int)(SwarmRoadBuffer.numPossCars*0.80)] != null)
			total++;
		return total;
	}

    public double calculateDensity(boolean pos){
    	if (pos)
    		return (getNumCarsPos() / (3 * SwarmRoadBuffer.numPossCars));
    	return (getNumCarsNeg() / (3 * SwarmRoadBuffer.numPossCars));
    }

    public boolean pastDensityThreshold(boolean pos){
    	if (calculateDensity(pos) > (2/3)){
    			return true;
    	}
    	return false;
    }

    public boolean isRoadClosed(SwarmPoint $here) {
        if($here.x > otherSide($here).x)
            return !enabled[0];
        else if($here.x < otherSide($here).x)
            return !enabled[1];
        else if($here.y < otherSide($here).y)
            return !enabled[0];
        else if($here.y > otherSide($here).y)
            return !enabled[1];

        return false;
    }

    public String toString(){
        String state;
        if(enabled[0])
            state = "T ";
        else
            state = "F ";
        if(enabled[1])
            state += "T";
        else
            state += "F";

        return (endPoints[0].toString() + "-" + endPoints[1].toString() + " " + state);
    }
}
