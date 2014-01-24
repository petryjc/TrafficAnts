import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

public class SwarmCityComponent extends Component implements MouseListener {
    private static final long serialVersionUID = 3978144326935261748L;

    private SwarmCity theCity;
    private Vector clickBounds;
    private Dimension size;

    public int showRoadUsage;

    public SwarmCityComponent() {
        theCity = null;
        size = null;
        showRoadUsage = 0;

        this.addMouseListener(this);
    }
    
    public void setCity(SwarmCity $city) {
        theCity = $city;
        clickBounds = null;
        size = null;
    }

    public Dimension getPreferredSize() {
        if(size != null)
            return size;

		if(theCity == null)
			return new Dimension(800,800);

    	size = new Dimension((theCity.intersections.length - 1) * (SwarmRoad.ROAD_PIXELS + 14) + 2 * (ABuilding.PIXELS + 13),
				(theCity.intersections[0].length - 1) * (SwarmRoad.ROAD_PIXELS + 14) + 2 * (ABuilding.PIXELS + 13));
    	return size;
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public void paint(Graphics g) {
    	if(theCity != null && theCity.isStarted()) {
	        int roadPixels = SwarmRoad.ROAD_PIXELS;
	        int buildPixels = ABuilding.PIXELS;

	        boolean recordBounds = false;
	        if(clickBounds == null) {
	            recordBounds = true;
	            clickBounds = new Vector();
	        }

	        for(int i = 0 ; i < theCity.intersections.length; i++) {
	            for(int j = 0; j < theCity.intersections[0].length; j++) {
	                int x = i * (roadPixels + 13) + buildPixels + 13;
	                int y = j * (roadPixels + 13) + buildPixels + 13;

	                AIntersection thisInt = theCity.intersections[i][j];

	                if(i < theCity.intersections.length - 1) {
	                    if(recordBounds) clickBounds.add(new SwarmPair(thisInt.getRoad(AIntersection.ROAD_RIGHT),
	                            new Rectangle(x + 13, y - 13, roadPixels - 13, 28)));

	                    Graphics lg = g.create(x + 13, y - 13, roadPixels - 13, 28);
	                    thisInt.getRoad(AIntersection.ROAD_RIGHT).paint(lg);

	                    if(showRoadUsage > 0) {
	                        float usage = 0.0f;

	                        if(showRoadUsage == 1)
	                            usage = (float) ((double) thisInt.getRoad(AIntersection.ROAD_RIGHT).getNumCars() / (SwarmRoadBuffer.numPossCars * 6));
	                        else if(showRoadUsage == 2)
	                            usage = (float) ((double) thisInt.getRoad(AIntersection.ROAD_RIGHT).statsMaxCars() / (SwarmRoadBuffer.numPossCars * 6));

	                        lg.setColor(new Color(1.0f, 0.0f, 0.0f, usage));
	                        lg.fillRect(0, 0, roadPixels, 28);
	                    }
	                }

	                if(j < theCity.intersections[0].length - 1) {
	                    if(recordBounds) clickBounds.add(new SwarmPair(thisInt.getRoad(AIntersection.ROAD_DOWN),
	                            new Rectangle(x - 13, y + 13, 28, roadPixels - 13)));

	                    Graphics lg = g.create(x - 13, y + 13, 28, roadPixels - 13);
	                    thisInt.getRoad(SwarmIntersection.ROAD_DOWN).paint( lg );

	                    if(showRoadUsage > 0) {
	                        float usage = 0.0f;

	                        if(showRoadUsage == 1)
	                        	usage = (float) ((double) thisInt.getRoad(AIntersection.ROAD_DOWN).getNumCars() / (SwarmRoadBuffer.numPossCars * 6));
	                        else if(showRoadUsage == 2)
	                        	usage = (float) ((double) thisInt.getRoad(AIntersection.ROAD_DOWN).statsMaxCars() / (SwarmRoadBuffer.numPossCars * 6));

	                        lg.setColor(new Color(1.0f, 0.0f, 0.0f, usage));
	                        lg.fillRect(0, 0, 28, roadPixels - 13);
	                    }

	                }

	                // Draw the intersection itself
	                if(recordBounds)
	                    clickBounds.add(new SwarmPair(thisInt,
	                            new Rectangle(x - 13, y - 13, 27, 27)));
	                thisInt.paint( g.create(x - 13, y - 13, 27, 27) );

	                // Draw the buildings
	                for(int k = 0; k < thisInt.buildings.size(); k++) {
	                    ABuilding thisBldg = (ABuilding) thisInt.buildings.get(k);

	                    if(k == 0) {
	                        if(recordBounds)
	                            clickBounds.add(new SwarmPair(thisBldg,
	                                    new Rectangle(x - 13 - buildPixels, y - 13 - buildPixels, buildPixels, buildPixels)));
	                        thisBldg.paint( g.create(x - 13 - buildPixels, y - 13 - buildPixels, buildPixels, buildPixels) );
	                    } else if(k == 1) {
	                        if(recordBounds)
	                            clickBounds.add(new SwarmPair(thisBldg,
	                                    new Rectangle(x + 13, y - 13 - buildPixels, buildPixels, buildPixels)));
	                        thisBldg.paint( g.create(x + 13, y - 13 - buildPixels, buildPixels, buildPixels) );
	                    } else if(k == 2) {
	                        if(recordBounds)
	                            clickBounds.add(new SwarmPair(thisBldg,
	                                    new Rectangle(x - 13 - buildPixels, y + 13, buildPixels, buildPixels)));
	                        thisBldg.paint( g.create(x - 13 - buildPixels, y + 13, buildPixels, buildPixels) );
	                    } else if(k == 3) {
	                        if(recordBounds)
	                            clickBounds.add(new SwarmPair(thisBldg,
	                                    new Rectangle(x - 13 - buildPixels, y - 13 - buildPixels, buildPixels, buildPixels)));
	                        thisBldg.paint( g.create(x + 13, y + 13, buildPixels, buildPixels) );
	                    }
	                }
	            }
	        }
    	}
    }

    public void mouseClicked(MouseEvent e) {
		if (clickBounds == null)
			return;
        for(int i = 0; i < clickBounds.size(); i++) {
            SwarmPair thisBound = ((SwarmPair) clickBounds.get(i));
            if(((Rectangle) thisBound.second).contains(e.getX(), e.getY())) {
                e.translatePoint(-1*((Rectangle) thisBound.second).x, -1*((Rectangle) thisBound.second).y);
                ((Clickable) thisBound.first).mouseClicked(e);
                repaint();
                break;
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
}
