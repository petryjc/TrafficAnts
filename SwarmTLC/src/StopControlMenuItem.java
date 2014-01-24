import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class StopControlMenuItem extends JPanel implements MenuElement {
	private static final long serialVersionUID = 3617013035739656504L;

	public JTextField value;
    
	public StopControlMenuItem() {
		super();
		
		this.add(new JLabel("Stop at:"));
		value = new JTextField(6);
		this.add(value);
		
		this.validate();
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

	public void processMouseEvent(MouseEvent event, MenuElement[] path,
			MenuSelectionManager manager) {
	}
}
