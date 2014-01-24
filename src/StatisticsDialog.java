import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

public class StatisticsDialog extends JFrame implements ActionListener, WindowListener {
	private static final long serialVersionUID = 3834588811803113012L;

	CarStatistics stats;

	JLabel sizeLabel;
	JLabel[][] statLabels;
	Timer updateTimer;

	private int[] whichStat = {CarStatistics.WAIT_PER_DIST,
	        CarStatistics.TRAVEL_TIME,
	        CarStatistics.WORST_WAIT,
	        CarStatistics.JITTER};

	private void placeComponent(GridBagLayout gbl, int x, int y, Component c) {
	    GridBagConstraints gbc = new GridBagConstraints();

	    gbc.gridx = x;
	    gbc.gridy = y;
	    gbc.ipadx = 50;
	    gbc.ipady = 50;

	    gbc.anchor = GridBagConstraints.EAST;

	    gbl.setConstraints(c, gbc);
	    getContentPane().add(c);
	}

	public StatisticsDialog(CarStatistics $stats) {
	    super();

	    stats = $stats;

	    setTitle("Car Statistics - " + stats.city.lightType);

	    GridBagLayout gbl = new GridBagLayout();
	    getContentPane().setLayout(gbl);


	    sizeLabel = new JLabel("N=0");
	    placeComponent(gbl, 0, 0, sizeLabel);

	    placeComponent(gbl, 1, 0, new JLabel("Mean"));
	    placeComponent(gbl, 2, 0, new JLabel("Median"));
	    placeComponent(gbl, 3, 0, new JLabel("Max"));
	    placeComponent(gbl, 4, 0, new JLabel("Max"));

	    placeComponent(gbl, 0, 1, new JLabel("Wait/Dist:"));
	    placeComponent(gbl, 0, 2, new JLabel("Route Time:"));
	    placeComponent(gbl, 0, 3, new JLabel("Max Wait:"));
	    placeComponent(gbl, 0, 4, new JLabel("Jitter:"));

	    statLabels = new JLabel[4][4];

	    for(int i = 0; i < 4; i++) {
	        for(int j = 0; j < 4; j++) {
	        	statLabels[i][j] = new JLabel("");
	        	placeComponent(gbl, 1+i, 1+j, statLabels[i][j]);
	        }
	    }

	    pack();

	    this.addWindowListener(this);

	    updateTimer = new Timer(75, this);
	    updateTimer.start();
	}

	public void actionPerformed(ActionEvent e) {
	    DecimalFormat nf = new DecimalFormat("0.###");

	    sizeLabel.setText("N=" + stats.size());

	    for(int i = 0; i < 4; i++)
	        statLabels[0][i].setText(nf.format(stats.getMeanStat(whichStat[i])));
	    for(int i = 0; i < 4; i++)
	        statLabels[1][i].setText(nf.format(stats.getMedianStat(whichStat[i])));
	    for(int i = 0; i < 4; i++)
	        statLabels[2][i].setText(nf.format(stats.getMaxStat(whichStat[i])));
	    for(int i = 0; i < 4; i++)
	        statLabels[3][i].setText(nf.format(stats.getMinStat(whichStat[i])));

	    repaint();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	    updateTimer.stop();
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
