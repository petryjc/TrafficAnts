import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public class DrawPanel extends JPanel {
	
	public final static int offset = 5;
	public final static int xOffset = 20;
	public final static int yOffset = 20;
	
	public DrawPanel()                       // set up graphics window
    {
        super();
        setBackground(Color.WHITE);
    }

    public void paintComponent(Graphics g)  // draw graphics in the panel
    {

        super.paintComponent(g);            // call superclass to make panel display correctly

        for(Road r : Road.roadList){
        	r.draw(g);
        }
        for(Car c:Car.carList){
        	c.draw(g);
        }
        for(Intersection i : Intersection.intersectionList){
        	i.draw(g);
        }
        
        
        
        
    }
}
