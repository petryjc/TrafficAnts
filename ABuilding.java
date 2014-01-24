import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.*;

public abstract class ABuilding implements Clickable {
	
    public final static int PIXELS = 24;

    protected SwarmCity parent;
    protected SwarmPoint loc;
    protected Color color;
	// A queue of cars currently waiting to leave the building.
	// Cars are let out in first in first out order.
	protected LinkedList carsAtHome;
	protected int[] attractiveness;
	protected int[] releaseInfo;

	public ABuilding(SwarmCity $parent, SwarmPoint $loc, int initCars, int[] $attractiveness, int[] $releaseInfo) {
		parent = $parent;
		loc = $loc;
		attractiveness = $attractiveness;
		releaseInfo = $releaseInfo;
		color = new Color((float)(parent.random.nextDouble()*0.5f+0.5f),(float)(parent.random.nextDouble()*0.5f + 0.5f),(float)(parent.random.nextDouble()*0.5f+0.5f));
		
    	carsAtHome = new LinkedList();
    	for (int x = 0; x < initCars; x++) {
			ACar c;
			if(parent.random.nextBoolean())
			    c = new SwarmCar($parent, this);
			else
			    c = new StraightCar($parent, this);
			carsAtHome.add(c);
			parent.cars.add(c);
		}		
	}

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g.setColor(color);
        g.fillRect(0, 0, PIXELS - 1, PIXELS - 1);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, PIXELS - 1, PIXELS - 1);

        if(carsAtHome != null) {
            String label = String.valueOf(carsAtHome.size());

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 10));

            Rectangle2D bounds = g.getFont().getStringBounds(label, g2d.getFontRenderContext());
            g.drawString(label, (int) Math.round((PIXELS / 2) - (bounds.getWidth() / 2)), (int) Math.round((PIXELS / 2) + (bounds.getHeight() / 2)) - 2);
        }

        g.dispose();
    }
    
    public void mouseClicked(MouseEvent e) {
    }

    public SwarmPoint getLoc() {
        return loc;
    }

    public abstract void addCar(ACar car);

    public abstract void takeWaitingCar(ACar car);

    public abstract boolean wantsToReleaseCars();

    public abstract ACar getWaitingCar();

    public abstract void keepWaitingCar(ACar car);

    public abstract int getBuildingAttractiveness();

    public abstract void tick();
}
