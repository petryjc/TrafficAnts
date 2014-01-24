import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class TimedIntersectionDialog extends JFrame implements ActionListener, WindowListener {
	private static final long serialVersionUID = 3978984362509612849L;

	private JTextField curLabel;
	private JButton setButton;
	private JButton addButton;
	private JButton removeButton;
	private TimedIntersection thisInt;
	
	private Object[] possibleLightPatternsData  =
	                             { "All Stop" , "All Right" , "E/W Left Right" ,
	                               "N/S Left Right" , "E/W Straight Right" ,
	                               "N/S Straight Right" , "East Full" ,
	                               "North Full" , "West Full" , "South Full" };
	
	private JList currentLightPattern;
	private JList possibleLightPatterns;

	public TimedIntersectionDialog(String title, TimedIntersection $int) {
	    super(title);
	    
	    thisInt = $int;
	    
	    this.setSize(new Dimension(290,500));
	    this.getContentPane().setLayout(new FlowLayout());
	    
		curLabel     = new JTextField(10);
		setButton    = new JButton("Set");
		addButton    = new JButton("Add");
		addButton.setPreferredSize(new Dimension(120,20));
		removeButton = new JButton("Remove");
		removeButton.setPreferredSize(new Dimension(120,20));
		
		JLabel patternDelayTime = new JLabel("Pattern Delay Time");
		JLabel currentPatternLabel = new JLabel("Current Pattern",2);
		JLabel possiblePatternLabel = new JLabel("Possible Patterns",2);
		
		currentLightPattern = new JList();
		possibleLightPatterns = new JList();
		
		currentLightPattern = new JList();
		currentLightPattern.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		currentLightPattern.setLayoutOrientation(JList.VERTICAL);
		currentLightPattern.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(currentLightPattern);
		listScroller.setPreferredSize(new Dimension(120, 190));
		
		ArrayList currentPattern = thisInt.getPatterns();
		DefaultListModel listModel = new DefaultListModel();
		for (int i = 0 ; i < currentPattern.size() ; i++) {
			listModel.addElement(new TimedIntersectionListElement((String)
				                 possibleLightPatternsData[((Integer)currentPattern.get(i)).intValue()],
				                 ((Integer)currentPattern.get(i)).intValue()));
		}
		currentLightPattern.setModel(listModel);
		currentLightPattern.setSelectedIndex(0);
		
		possibleLightPatterns = new JList(possibleLightPatternsData);
		possibleLightPatterns.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		possibleLightPatterns.setLayoutOrientation(JList.VERTICAL);
		possibleLightPatterns.setVisibleRowCount(-1);
		JScrollPane listScroller2 = new JScrollPane(possibleLightPatterns);
		listScroller2.setPreferredSize(new Dimension(120, 190));
		possibleLightPatterns.setSelectedIndex(0);
		
		currentLightPattern.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (currentLightPattern.getSelectedIndex() == -1) {
		        	// Do nothing -- or set the text box to say nothing
		        	// or possibly disallow selecting nothing
		        } else {
		        	curLabel.setText(String.valueOf(thisInt.getDelayTime(
		        		              currentLightPattern.getSelectedIndex())));
		        }
			}
		});
		
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = currentLightPattern.getSelectedIndex();
				((DefaultListModel)currentLightPattern.getModel()).remove(currentLightPattern.getSelectedIndex());
				int newSelectedIndex = (selectedIndex == 0 ? 0 : selectedIndex-1);
				currentLightPattern.setSelectedIndex(newSelectedIndex);
				
				thisInt.getPattern().remove(selectedIndex);
				thisInt.getDelayTimes().remove(selectedIndex);
				
				thisInt.resetIndex();
			}
		});
		
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TimedIntersectionListElement newElement = new TimedIntersectionListElement(
					(String)possibleLightPatternsData[possibleLightPatterns.getSelectedIndex()],
					possibleLightPatterns.getSelectedIndex());
				((DefaultListModel)currentLightPattern.getModel()).addElement(newElement);
				
				thisInt.getPattern().add(new Integer(possibleLightPatterns.getSelectedIndex()));
				int delay = getTextBoxInput();
				if (delay == -1) delay = 20;
				thisInt.getDelayTimes().add(new Integer(delay));
			}
		});
		
		setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int newDelay = getTextBoxInput();
//				System.out.println ("input: " + newDelay);
				if (newDelay != -1) {
					thisInt.setDelayTime(newDelay,
						                currentLightPattern.getSelectedIndex());
				} else {
					curLabel.setText(String.valueOf(thisInt.getDelayTime(
							          currentLightPattern.getSelectedIndex())));
				}
				
				TimedIntersectionDialog.this.repaint();
			}
		});
		
		curLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int newDelay = getTextBoxInput();
				if (newDelay != -1) {
					thisInt.setDelayTime(newDelay,
						                currentLightPattern.getSelectedIndex());
				} else {
					curLabel.setText(String.valueOf(thisInt.getDelayTime(
							          currentLightPattern.getSelectedIndex())));
				}
				
				TimedIntersectionDialog.this.repaint();
			}
		});
				
		curLabel.setText(String.valueOf(thisInt.getDelayTime(
			                          currentLightPattern.getSelectedIndex())));
		
		this.getContentPane().add(patternDelayTime);
		this.getContentPane().add(curLabel);
		this.getContentPane().add(currentPatternLabel);
		this.getContentPane().add(possiblePatternLabel);
		this.getContentPane().add(listScroller);
		this.getContentPane().add(listScroller2);
		this.getContentPane().add(removeButton);
		this.getContentPane().add(addButton);
		this.getContentPane().add(setButton);
	}
	
	public int getTextBoxInput() {
		int newDelay = -1;
		try {
			newDelay = Integer.parseInt(curLabel.getText());
			if (newDelay > AIntersection.MIN_LIGHT_TIME) {
				return newDelay;
			} else {
				return -1;
			}
		} catch (NumberFormatException ex) {
			return -1;
		}	
	}
	
	public void updateLabel() {
	}

	public void actionPerformed(ActionEvent e) {
		this.pack();
	}

	public void windowActivated(WindowEvent e) {
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

	private class TimedIntersectionListElement {
		private String text;
		private int index;
		public TimedIntersectionListElement(String $text,int $index) {
			text = $text;
			index = $index;
		}
		
		public int getIndex() {
			return index;
		}
		
		public String toString() {
			return text;	
		}
	}

}