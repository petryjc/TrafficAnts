import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PauseMenuItem extends JPanel implements MenuElement {
	private static final long serialVersionUID = 3544671771962979636L;

	public JCheckBox box;
    
    public PauseMenuItem() {
		super();

		box = new JCheckBox("Paused", true);
		box.setBorderPaintedFlat(false);
		this.add(box);
	}

	public void menuSelectionChanged(boolean isIncluded) {
	}

	public Component getComponent() {
		return this;
	}

	public MenuElement[] getSubElements() {
		return null;
	}

	public void processKeyEvent(KeyEvent event, MenuElement[] path, MenuSelectionManager manager) {
	}

	public void processMouseEvent(MouseEvent event, MenuElement[] path, MenuSelectionManager manager) {
	}
}
