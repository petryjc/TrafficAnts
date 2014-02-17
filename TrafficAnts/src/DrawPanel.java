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

        for(int i = 0; i < Road.roadList.size(); i++){
        	Road.roadList.get(i).draw(g);
        }
        
        for(int i = 0; i < Intersection.intersectionList.size(); i++){
        	Intersection.intersectionList.get(i).draw(g);
        }
        
        for(int i = 0; i < Car.carList.size(); i++){
        	Car.carList.get(i).draw(g);
        }
        
    }
}
