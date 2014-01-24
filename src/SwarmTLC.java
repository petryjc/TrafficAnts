import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.io.*;

public class SwarmTLC extends JFrame implements WindowListener {
    private static final long serialVersionUID = 3546920286160107317L;

    private SwarmCity theCity;

    public SwarmCityComponent theDrawing;

    public JLabel status;
    public JTextField switch_delay;

    private JSlider slider;
    private PauseMenuItem pausebox;
    private StopControlMenuItem stopbox;

    private String lightType;
    private String buildingFile;
    private String intersectionFile;
    private String roadFile;

    // Used to allow programmatic selection of the timedMenuItem
    // (whenever a timed intersection file is loaded, the intersection
    // type is automatically set to timed)
    JRadioButtonMenuItem timedMenuItem;
    ButtonGroup intersectionButtonGroup;

    public SwarmTLC() {
        theCity = null;
        theDrawing = new SwarmCityComponent();
        switch_delay = new JTextField(5);

        this.setTitle("Swarm Traffic Light Control");

        JMenuBar menuBar = new JMenuBar();
        JMenu menu;
        JMenu sbMenu;
        JMenuItem menuItem;
        ButtonGroup bg;

        menu = new JMenu("Simulation");

        menuItem = new JMenuItem("Load Buildings..");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
			    final JFileChooser fc = new JFileChooser();
				String curDir = System.getProperty("user.dir");
				File f = new File(curDir+"/Building Files/");
				fc.setCurrentDirectory(f);
		        int returnVal = fc.showOpenDialog(null);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
					buildingFile = file.getAbsolutePath();
		        }
			}
		});
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Clear Timed Intersections");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
	            intersectionFile = null;
			}
		});
        menu.add(menuItem);

        menuItem = new JMenuItem("Load Timed Intersections..");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				JFileChooser fc = new JFileChooser();
				String curDir = System.getProperty("user.dir");
				File f = new File(curDir+"/Timed Intersection Files/");
				fc.setCurrentDirectory(f);
		        int returnVal = fc.showOpenDialog(null);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            intersectionFile = file.getAbsolutePath();
		            setTimedIntersection();
		        }
			}
		});
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Save Timed Intersections..");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				JFileChooser fc = new JFileChooser();
				String curDir = System.getProperty("user.dir");
				File f = new File(curDir+"/Timed Intersection Files/");
				fc.setCurrentDirectory(f);
		        int returnVal = fc.showSaveDialog(null);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            saveTimedIntersectionFile(file.getAbsolutePath());
		        }
			}
		});
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Clear Road Pattern");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
	            roadFile = null;
			}
		});
        menu.add(menuItem);

        menuItem = new JMenuItem("Load Road Pattern..");
		menuItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e){
				JFileChooser fc = new JFileChooser();
				String curDir = System.getProperty("user.dir");
				File f = new File(curDir+"/Road Files/");
				fc.setCurrentDirectory(f);
		        int returnVal = fc.showOpenDialog(null);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            roadFile = file.getAbsolutePath();
		        }
			}
		});
        menu.add(menuItem);

        menuItem = new JMenuItem("Save Road Pattern..");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
			    if(theCity != null) {
					JFileChooser fc = new JFileChooser();
					String curDir = System.getProperty("user.dir");
					File f = new File(curDir+"/Road Files/");
					fc.setCurrentDirectory(f);
			        int returnVal = fc.showSaveDialog(null);

			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            roadFile = file.getAbsolutePath();

			            try {
			            	theCity.saveRoadFile(roadFile);
			            	System.out.println("Done writing road file!");
			            } catch(Exception ex) {
			                System.out.println("Error writing road file:");
			                ex.printStackTrace();
			            }
			        }
			    }
			}
		});
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Initialize New");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initializeCity();
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
			    if(theCity != null)
			    	theCity.stop();
				System.exit(0);
			}
		});
        menu.add(menuItem);

        menuBar.add(menu);

        menu = new JMenu("Options");

        sbMenu = new JMenu("Light type");

        bg = new ButtonGroup();

        lightType = "Trigger";
        menuItem = new JRadioButtonMenuItem("Trigger", true);
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				lightType = "Trigger";
			}
		});
        sbMenu.add(menuItem);
        bg.add(menuItem);

        menuItem = new JRadioButtonMenuItem("Most Cars First");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				lightType = "Most Cars First";
			}
		});
        sbMenu.add(menuItem);
        bg.add(menuItem);

        menuItem = new JRadioButtonMenuItem("Local Demand");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				lightType = "Local Demand";
			}
		});
        sbMenu.add(menuItem);
        bg.add(menuItem);

        menuItem = new JRadioButtonMenuItem("Recieving Capacity");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				lightType = "Recieving Capacity";
			}
		});
        sbMenu.add(menuItem);
        bg.add(menuItem);

        menuItem = new JRadioButtonMenuItem("Trigger Ext", true);
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				lightType = "Trigger Ext";
			}
		});
        sbMenu.add(menuItem);
        bg.add(menuItem);

        menuItem = new JRadioButtonMenuItem("Most Cars First");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				lightType = "Most Cars First";
			}
		});
        sbMenu.add(menuItem);
        bg.add(menuItem);

        menuItem = new JRadioButtonMenuItem("Limited Vision MCF");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				lightType = "Limited Vision MCF";
			}
		});
        sbMenu.add(menuItem);
        bg.add(menuItem);

        menuItem = new JRadioButtonMenuItem("Green-length Adjusted MCF");
        menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				lightType = "Green-length Adjusted MCF";
			}
		});
        sbMenu.add(menuItem);
        bg.add(menuItem);

        timedMenuItem = new JRadioButtonMenuItem("Timed");
        timedMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				lightType = "Timed";
			}
		});
        sbMenu.add(timedMenuItem);
        bg.add(timedMenuItem);
        menu.add(sbMenu);

        sbMenu = new JMenu("Indicate Road Usage");

        bg = new ButtonGroup();
        intersectionButtonGroup = bg;

        menuItem = new JRadioButtonMenuItem("None", true);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                theDrawing.showRoadUsage = 0;
            }
        });
        sbMenu.add(menuItem);
        bg.add(menuItem);

        menuItem = new JRadioButtonMenuItem("Current");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                theDrawing.showRoadUsage = 1;
            }
        });
        sbMenu.add(menuItem);
        bg.add(menuItem);

        menuItem = new JRadioButtonMenuItem("Global Max");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                theDrawing.showRoadUsage = 2;
            }
        });
        sbMenu.add(menuItem);
        bg.add(menuItem);

        menu.add(sbMenu);

        menuBar.add(menu);

        menuItem = new JMenuItem("Statistics");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(theCity != null)
                	(new StatisticsDialog(theCity.carStats)).show();
            }
        });

        menuBar.add(menuItem);

        pausebox =  new PauseMenuItem();
        pausebox.box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
			    if(theCity != null)
			    	theCity.setPaused(pausebox.box.isSelected());
			}
		});

        menuBar.add(pausebox);

        stopbox = new StopControlMenuItem();

        menuBar.add(stopbox);

        slider = new JSlider(0, 300, 75);
		slider.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
		    	if(theCity != null) {
		    		JSlider source = (JSlider)e.getSource();
		    		int v = source.getValue();
		    		theCity.setDelay(v);
		    	}
		    }
		});
		slider.setInverted(true);	// makes --> mean go faster
        menuBar.add(slider);

        this.setJMenuBar(menuBar);

        this.getContentPane().add(new JScrollPane(theDrawing));

        JPanel status_bar = new JPanel();

        status = new JLabel("Started..");
        status_bar.setLayout(new BorderLayout());
        status_bar.add(status, BorderLayout.WEST);
        status_bar.setBorder(new BevelBorder(BevelBorder.LOWERED));

        this.getContentPane().add(status_bar, BorderLayout.SOUTH);

        this.pack();
        this.setResizable(false);
        this.addWindowListener(this);
    }
    
    public void saveTimedIntersectionFile(String file) {
    	try {
    		theCity.saveTimedIntersectionFile(file);	
    	} catch (Exception e) {
    		
    	}
    }

    public void setTimedIntersection() {
    	// Select the timed intersection menu item
    	intersectionButtonGroup.setSelected(timedMenuItem.getModel(),true);
    	lightType = "Timed";
    }

    public void initializeCity() {
        if(theCity != null)
            theCity.stop();

        pausebox.box.setSelected(true);

        System.out.println("Starting new city..");

        int stopAfter;
        try {
        	stopAfter = Integer.parseInt(stopbox.value.getText());
        } catch(NumberFormatException e) {
            stopAfter = -1;
        }

        theCity = new SwarmCity(this, lightType, buildingFile, intersectionFile, roadFile, stopAfter);
        theDrawing.setCity(theCity);
        this.pack();
        this.repaint();

        theCity.setDelay(slider.getValue());
        theCity.setPaused(pausebox.box.isSelected());

        if(!theCity.isStarted())
            theCity.start();
    }

    public static void main(String[] args) {
    	SwarmTLC theMap = new SwarmTLC();

        theMap.show();
    }

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	    if(theCity != null)
	    	theCity.stop();
	    System.exit(0);
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
