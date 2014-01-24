import javax.swing.*;
import java.awt.event.*;

public class ScottIntersectionDialog extends JFrame implements ActionListener, WindowListener {
	private static final long serialVersionUID = 3978984362509612849L;

	private JLabel curLabel;
	private ScottIntersection thisInt;

	public ScottIntersectionDialog(String title, ScottIntersection $int) {
	    super(title);
	    
		curLabel = new JLabel();
		thisInt = $int;
		
		this.getContentPane().add(curLabel);
	
		new Timer(75, this).start();
	}
	
	public void updateLabel() {
	    curLabel.setText("{" + thisInt.flushNeed[0] + ", " + thisInt.flushNeed[1] + ", " + thisInt.flushNeed[2] + ", " + thisInt.flushNeed[3] + "}");
	}

	public void actionPerformed(ActionEvent e) {
		updateLabel();
		this.pack();
	}

	public void windowActivated(WindowEvent e) {
		updateLabel();
		this.pack();
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}
