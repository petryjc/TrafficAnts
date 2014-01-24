import java.util.*;
import java.io.*;
import java.awt.Graphics;
import java.text.ParseException;
import java.lang.reflect.Constructor;

public class SwarmCity extends Thread {
    private static final long serialVersionUID = 4049078237370135604L;
	public int DAY_LENGTH = 2000; // Made nonstatic so that it may be loadable

    public SwarmTLC parent;
	public boolean paused;

    public AIntersection[][] intersections;
    public ArrayList roads;
    public ArrayList cars;
    public ArrayList buildings;

	private int width, height;		// Height and width of city

    public long worldTime;
    public Random random; //Global Random instance to be used

    public CarStatistics carStats;
    private boolean started;

    public String lightType;
    private String buildingFile;		//The building file to try to load
	private String intersectionFile;	//The intersection file to try to load
	private String roadFile;    		//The road file to try to load

	private int sleepytime;

	private int stopAfter;

    public SwarmCity(SwarmTLC $parent,
            String $lightType,
            String $buildingsFile,
            String $intersectionFile,
            String $roadFile,
            int $stopAfter) {

        stopAfter = $stopAfter;
        worldTime = 0;
        parent = $parent;
        paused = false;
        sleepytime = 0;

        lightType = $lightType;
        buildingFile = $buildingsFile;
		intersectionFile = $intersectionFile;
		roadFile = $roadFile;

		random = new Random(0);
		carStats = new CarStatistics(this);

		try {
		    loadSize();
			createIntersections();
			createRoads();
			if(roadFile != null)
			    loadRoads();
			loadBuildings();
		} catch(Exception e) {
		    if(e.getMessage() != null)
		    	System.out.println(e.getMessage());
		    e.printStackTrace();
		}
		System.out.println("Loading done");
    }

    private void createIntersections() throws Exception {
    	// Create the interconnected graph
	    intersections = new AIntersection[width][height];

	    // Take advantage of Java reflection
	    Class iClass;
	    if(lightType.equalsIgnoreCase("Trigger"))
	        iClass = TriggerIntersection.class;
	    else if(lightType.equalsIgnoreCase("Trigger Ext"))
	        iClass = SwarmIntersection.class;
	    else if(lightType.equalsIgnoreCase("Most Cars First"))
	        iClass = SwarmIntersection.class;
	    else if(lightType.equalsIgnoreCase("Local Demand"))
	        iClass = ScottIntersection.class;
	    else if(lightType.equalsIgnoreCase("Timed"))
	        iClass = TimedIntersection.class;
	    else if(lightType.equalsIgnoreCase("Recieving Capacity"))
	        iClass = AaronIntersection.class;
	    else if(lightType.equalsIgnoreCase("Limited Vision MCF"))
	    	iClass = AnthonyIntersection.class;
	    else if(lightType.equalsIgnoreCase("Green-length Adjusted MCF"))
	    	iClass = SwarmIntersection2.class;
	    else
	        throw new Exception("Unknown light type!");

	    Constructor iConstructor = iClass.getConstructor(new Class[] {SwarmPoint.class, SwarmCity.class});

    	// Initialize all the intersections
	    for(int i = 0; i < width; i++) {
	        for(int j = 0; j < height; j++) {
                intersections[i][j] = (AIntersection) iConstructor.newInstance(new Object[] {new SwarmPoint(i,j), this});
	        }
	    }

	    if(iClass == TimedIntersection.class)
			loadTimedIntersectionFile();
    }

    private void createRoads() throws Exception {
    	roads = new ArrayList();

    	for(int i = 0; i < width; i++) {
	        for(int j = 0; j < height; j++) {
	            AIntersection thisInt = intersections[i][j];

	            if(i > 0)
	                thisInt.setRoad(AIntersection.ROAD_LEFT, intersections[i-1][j].getRoad(AIntersection.ROAD_RIGHT));

	            if(j > 0) {
	                thisInt.setRoad(AIntersection.ROAD_UP, intersections[i][j-1].getRoad(AIntersection.ROAD_DOWN));
	            }

	            if(i < width - 1) {
	                SwarmRoad r = new SwarmRoad(this, new SwarmPoint(i,j), new SwarmPoint(i+1,j));

	                roads.add(r);
	                thisInt.setRoad(AIntersection.ROAD_RIGHT, r);
	            }
	            if(j < height - 1) {
	                SwarmRoad r = new SwarmRoad(this, new SwarmPoint(i,j), new SwarmPoint(i,j+1));

	                roads.add(r);
	                thisInt.setRoad(AIntersection.ROAD_DOWN, r);
	            }
	        }
	    }
	 }

	private void loadSize() throws Exception {
		System.out.println ("Loading size from building file");

		BufferedReader in;

		in = new BufferedReader(new FileReader(this.buildingFile));

		String line;
		// Continue reading lines of the file until the end of file is reached
		while ((line = in.readLine()) != null) {
			// Allow for comments
			if ((line.trim()).startsWith("#"))
				continue;

			if ((line.trim()).length() == 0)
				continue;

			// Data in each line is delimited by spaces
			String[] tokens = line.split("[\\s]+");

			if (tokens[0] == null)
				return;
			if (tokens[0].equals("S")) {
				width = Integer.parseInt(tokens[1]);
				height = Integer.parseInt(tokens[2]);
				break;
			}
		}
//		System.out.println("Got size: " + width + "x" + height);
	}

	private void loadBuildings() throws Exception {
		System.out.println ("Loading building file");

		buildings = new ArrayList();
		cars = new ArrayList();

		BufferedReader in;

		in = new BufferedReader(new FileReader(buildingFile));

		String line;
		// Continue reading lines of the file until the end of file is reached
		while ((line = in.readLine()) != null) {

//			System.out.println(line);

			// Allow for comments
			if ((line.trim()).startsWith("#"))
				continue;

			if ((line.trim()).length() == 0)
				continue;

			// Data in each line is delimited by spaces
			String[] tokens = line.split("[\\s]+");
			int[] attractiveness = new int[100];
			int[] releaseInfo = new int[100];

			if (tokens[0] == null)
				return;
			if (tokens[0].equals("S")) {
				continue;
			}

			int x;
			for (x = 5; !tokens[x].equals("|"); x++) {
				attractiveness[x-5] = Integer.parseInt(tokens[x]);
			}
			x++;
			for (int y = x; y < tokens.length; y++) {
				releaseInfo[y-x] = Integer.parseInt(tokens[y]);
			}

			if (tokens[0].equals("A")) {
				ABuilding ab = new ApartmentBuilding(this,
						new SwarmPoint(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])),
						Integer.parseInt(tokens[3]), attractiveness, releaseInfo);
				intersections[Integer.parseInt(tokens[1])][Integer.parseInt(tokens[2])].addBuilding(ab);
				buildings.add(ab);
			}
			if (tokens[0].equals("G")) {
				ABuilding ab = new GroceryStoreBuilding(this,
						new SwarmPoint(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])),
						Integer.parseInt(tokens[3]), attractiveness, releaseInfo);
				intersections[Integer.parseInt(tokens[1])][Integer.parseInt(tokens[2])].addBuilding(ab);
				buildings.add(ab);
			}
			if (tokens[0].equals("M")) {
				ABuilding ab = new MovieTheaterBuilding(this,
						new SwarmPoint(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])),
						Integer.parseInt(tokens[3]), attractiveness, releaseInfo);
				intersections[Integer.parseInt(tokens[1])][Integer.parseInt(tokens[2])].addBuilding(ab);
				buildings.add(ab);
			}
		}
	}
	
	public void saveTimedIntersectionFile(String file) throws Exception {
		// The file input stream
		BufferedWriter in;

		in = new BufferedWriter(new FileWriter(new File(file)));

		for(int i = 0; i < width; i++) {
	        for(int j = 0; j < height; j++) {
	        	in.write(j + " " + i + " | ");
	        	ArrayList patterns = ((TimedIntersection) intersections[j][i]).getPatterns();
	        	for (int k = 0 ; k < patterns.size() ; k++) {
	        		in.write(((Integer)patterns.get(k)).intValue() + " ");
	        	}
	        	in.write(" | ");
	        	ArrayList delayTimes = ((TimedIntersection) intersections[j][i]).getDelayTimes();
	        	for (int k = 0 ; k < delayTimes.size() ; k++) {
	        		in.write(((Integer)delayTimes.get(k)).intValue() + " ");
	        	}
	        	in.newLine();
	        }
	     }

		in.flush();
		in.close();
	}

	private void loadTimedIntersectionFile() throws Exception {
		System.out.println ("Loading timed intersection file");

		boolean[][] timingSet = new boolean[height][width];

		boolean defaultSet     = false;
		boolean settingDefault = false;
		ArrayList defaultPattern = new ArrayList();
		ArrayList defaultDelay   = new ArrayList();
		int row = 0;
		int column = 0;


		// The file input stream
		BufferedReader in;

		in = new BufferedReader(new FileReader(intersectionFile));

		String line;
		// Continue reading lines of the file until the end of file is reached
		while ((line = in.readLine()) != null) {

//			System.out.println(line);

			// Allow for comments
			if ((line.trim()).startsWith("#"))
				continue;

			if ((line.trim()).length() == 0)
				continue;

			// Sections of data are delimited by | with any
			// number of spaces on either side
			String[] sections = line.split("[\\s]*[|][\\s]*");

/*
			for (int i = 0 ; i < sections.length ; i++) {
				System.out.println ("."+sections[i]+".");
			}
 */

			if (sections.length != 3) {
				throw new ParseException("Error in line: " + line,0);
			} else {
				// Data in each section is delimited by spaces

				// First section
				String[] tokens = sections[0].split("[\\s]+");
//				System.out.println ("reading: ."+tokens[0]+".");
				if (tokens.length == 1 && tokens[0].compareTo("default") == 0) {
					if (defaultSet) {
						throw new ParseException("Default value already set",0);
					}
					defaultSet = true;
					settingDefault = true;
				} else if (tokens.length == 2) {
					settingDefault = false;
					row = Integer.parseInt(tokens[0]);
					column = Integer.parseInt(tokens[1]);
				} else {
					throw new ParseException("Error in line: " + line,0);
				}

				// Second section
				tokens = sections[1].split("[\\s]+");
//				System.out.println ("reading: ."+tokens[0]+".");
				ArrayList pattern = new ArrayList();
				for (int i = 0 ; i < tokens.length ; i++) {
					if (settingDefault) {
						defaultPattern.add(new Integer( Integer.parseInt(tokens[i]) ));
					} else {
						pattern.add(new Integer( Integer.parseInt(tokens[i]) ));
					}
				}

				// Third section
				tokens = sections[2].split("[\\s]+");
//				System.out.println ("reading: ."+tokens[0]+".");
				ArrayList delay = new ArrayList();
				for (int i = 0 ; i < tokens.length ; i++) {
					if (settingDefault) {
						defaultDelay.add(new Integer( Integer.parseInt(tokens[i]) ));
					} else {
						delay.add(new Integer( Integer.parseInt(tokens[i]) ));
					}
				}

				if (!settingDefault) {
					// Set the intersection
//					System.out.println ("Setting intersection at " + row + " " + column);
					timingSet[row][column] = true;
					((TimedIntersection) intersections[row][column]).setExtInfo(delay, pattern);
				}
				settingDefault = false;

			}
		}

		if (defaultSet) {
			for (int rowi = 0 ; rowi < height ; rowi ++) {
				for (int columni = 0 ; columni < width ; columni ++) {
					if (!timingSet[rowi][columni]) {
//						System.out.println ("Setting intersection at " + rowi + " " + columni);
						ArrayList defaultDelayCopy = new ArrayList(defaultDelay.size());
						ArrayList defaultPatternCopy = new ArrayList(defaultPattern.size());
						for (int i = 0 ; i < defaultPattern.size() ; i++)
							defaultPatternCopy.add(defaultPattern.get(i));
						for (int i = 0 ; i < defaultDelay.size() ; i++)
							defaultDelayCopy.add(defaultDelay.get(i));						
						((TimedIntersection) intersections[rowi][columni]).setExtInfo(defaultDelayCopy, defaultPatternCopy);
					}
				}
			}
		}
	}

	public void saveRoadFile(String file) throws Exception {
	    System.out.println("Saving road file");

	    BufferedWriter out = new BufferedWriter(new FileWriter(file));

	    for(int i = 0; i < roads.size(); i++) {
	        SwarmRoad r = (SwarmRoad) roads.get(i);

	        if(!r.enabled[0] || !r.enabled[1]) {
	            out.write(r.toString());
	            out.newLine();
	        }
	    }

	    out.close();
	}

	private void loadRoads() throws Exception {
	    System.out.println("Loading road file");

	    BufferedReader in = new BufferedReader(new FileReader(roadFile));

	    String line;
	    int lc = 0;
		while ((line = in.readLine()) != null) {
		    lc++;

			if ((line.trim()).startsWith("#"))
				continue;

			if ((line.trim()).length() == 0)
				continue;

			String[] tokens = line.split("[\\W\\s]+");

			int moveTo = 0;
			int goodTokens = 0;
			for(int i = 0; i < tokens.length; i++) {
			    tokens[moveTo] = tokens[i];

			    if(!tokens[i].equals("")) {
			        moveTo++;
			        goodTokens++;
			    }
			}

			if(goodTokens != 6)
			    System.out.println("Bad line: " + lc);

			try {
				int x1,y1,x2,y2;
				boolean s1,s2;

			    try {
				    x1 = Integer.parseInt(tokens[0]);
				    y1 = Integer.parseInt(tokens[1]);
				    x2 = Integer.parseInt(tokens[2]);
				    y2 = Integer.parseInt(tokens[3]);
			    } catch(NumberFormatException e) {
			        throw new ParseException("Bad Coordinates", 0);
			    }

			    if(tokens[4].startsWith("t") || tokens[4].startsWith("T"))
			        s1 = true;
			    else
			        s1 = false;
			    if(tokens[5].startsWith("t") || tokens[5].startsWith("T"))
			        s2 = true;
			    else
			        s2 = false;

			    AIntersection tInt = intersections[x1][y1];
			    if(x1 < x2) {
			        tInt.getRoad(AIntersection.ROAD_RIGHT).enabled[0] = s1;
			        tInt.getRoad(AIntersection.ROAD_RIGHT).enabled[1] = s2;
			    } else if(x1 > x2) {
			        tInt.getRoad(AIntersection.ROAD_LEFT).enabled[0] = s1;
			        tInt.getRoad(AIntersection.ROAD_LEFT).enabled[1] = s2;
			    } else if(y1 < y2) {
			        tInt.getRoad(AIntersection.ROAD_DOWN).enabled[0] = s1;
			        tInt.getRoad(AIntersection.ROAD_DOWN).enabled[1] = s2;
			    } else if(y1 > y2) {
			        tInt.getRoad(AIntersection.ROAD_UP).enabled[0] = s1;
			        tInt.getRoad(AIntersection.ROAD_UP).enabled[1] = s2;
			    } else {
			        throw new ParseException("Bad Locations", 0);
			    }
			} catch(ParseException e) {
			    System.out.println("Bad line: " + lc);
			}
		}
	}


    public void paint(Graphics g) {
        int roadPixels = SwarmRoad.ROAD_PIXELS;
        int buildPixels = ABuilding.PIXELS;

        for(int i = 0 ; i < intersections.length; i++) {
            for(int j = 0; j < intersections[0].length; j++) {
                int x = (i + 1) * (roadPixels + 13);
                int y = (j + 1) * (roadPixels + 13);

                AIntersection thisInt = intersections[i][j];

                // Draw the roads
                if(i == 0)
                    thisInt.getRoad(SwarmIntersection.ROAD_LEFT).paint( g.create(x - roadPixels, y - 13, roadPixels - 13, 28) );
                if(j == 0)
                    thisInt.getRoad(SwarmIntersection.ROAD_UP).paint( g.create(x - 13, y - roadPixels, 28, roadPixels - 13) );

                thisInt.getRoad(SwarmIntersection.ROAD_RIGHT).paint( g.create(x + 13, y - 13, roadPixels - 13, 28) );
                thisInt.getRoad(SwarmIntersection.ROAD_DOWN).paint( g.create(x - 13, y + 13, 28, roadPixels - 13) );

                // Draw the intersection itself
//                thisInt.patterns[thisInt.currentPattern][
                thisInt.paint( g.create(x - 13, y - 13, 27, 27) );

                // Draw the buildings
                for(int k = 0; k < thisInt.buildings.size(); k++) {
                    ABuilding thisBldg = (ABuilding) thisInt.buildings.get(k);

                    if(k == 0)
                        thisBldg.paint( g.create(x - 13 - buildPixels, y - 13 - buildPixels, buildPixels, buildPixels) );
                    else if(k == 1)
                        thisBldg.paint( g.create(x + 13, y - 13 - buildPixels, buildPixels, buildPixels) );
                    else if(k == 2)
                        thisBldg.paint( g.create(x - 13 - buildPixels, y + 13, buildPixels, buildPixels) );
                    else if(k == 3)
                        thisBldg.paint( g.create(x + 13, y + 13, buildPixels, buildPixels) );
                }
            }
        }
    }

    private void tick() {
        // Update all of the intersections
        for(int i = 0; i < intersections.length; i++) {
            for(int j = 0; j < intersections[0].length; j++) {
                intersections[i][j].tick();
            }
        }
        // Update all of the roads
        for(int i = 0; i < roads.size(); i++) {
            ((SwarmRoad) roads.get(i)).tick();
        }
    }

    public ArrayList generateDestList() {
        ArrayList pool = new ArrayList(buildings.size());

        int total = 0;
        for(int i = 0; i < buildings.size(); i++) {
            ABuilding b = (ABuilding) buildings.get(i);
            int v = b.getBuildingAttractiveness();
            if(v > 0) {
                total += v;
                pool.add(new SwarmPair(b, new Integer(v)));
            }
        }

        if(pool.size() == 0) {
            System.out.println("ERROR: There are no buildings available");
            stop();
        }

        int getNum = (int) Math.round(random.nextDouble() * (pool.size() / 2));
        if(getNum < 1)
            getNum = 1;

        ArrayList ret = new ArrayList(getNum);

        for(int j = 0; j < getNum; j++) {
        	double roll = random.nextDouble();

        	int i = 0;
        	int fi = -1;
        	double ptot = 0;
        	SwarmPair pe = null;
        	ABuilding b = null;
        	int v = 0;
        	for(; i < pool.size() && ptot < roll; i++) {
        	    pe = (SwarmPair) pool.get(i);
        	    if(pe == null)
        	        continue;
        	    fi = i;
        	    b = (ABuilding) pe.first;
        	    v = ((Integer) pe.second).intValue();

        	    double pv = ((double) v) / ((double) total);
        	    ptot += pv;
        	}
        	pool.set(fi, null);
        	total -= v;

        	ret.add(b);
        }

        return ret;
    }

/*
    public ArrayList generateDestList() {
    	if (this.buildings.size() < 2) {
    		System.out.println("ERROR:  Must specify > 1 building for SwarmCity.generateDestList() to work!");
    		System.exit(0);
    	}

    	ArrayList toReturn = new ArrayList();
		float[] probability = new float[this.buildings.size()];
		int[] attractiveness = new int[this.buildings.size()];
		int total = 0;
		for (int x = 0; x < this.buildings.size(); x++) {
			attractiveness[x] = ((ABuilding)(this.buildings.get(x))).getBuildingAttractiveness();
			total += attractiveness[x];
		}

		for (int x = 0; x < this.buildings.size(); x++) {
			if (x == 0)
				probability[x] = (float)(attractiveness[x])/total;
			else
				probability[x] = (float)(attractiveness[x])/total + probability[x-1];
		}

		// Pick a random series of destinations based on probabilities

        // This selection method can't gurantee results

    	int thisone = 0, lastone = -1;
    	for (int x = 0; x < 2; x++) {
            boolean done = false;
    	    do {
				double random = this.random.nextDouble();
				for (thisone = 0; thisone < this.buildings.size(); thisone++) {
                    if(thisone == lastone)
                        continue;
					if (probability[thisone] >= random) {
                        done = true;
						break;
                    }
				}
			} while (!done);
	    	toReturn.add(this.buildings.get(thisone));
	    	lastone = thisone;
	    }

//	    System.out.println(toReturn);
	    return toReturn;
    }
*/

    public boolean isStarted() {
        return started;
    }

    public void run() {
        started = true;
        for(;;) {
            while(paused) {
                try { sleep(500); } catch(InterruptedException e) { }
            }

            tick();
            worldTime++;

            if(parent != null) {
                parent.status.setText("<" + worldTime + "> " + cars.size() + " cars");
            	parent.repaint();

            	if(sleepytime > 0)
            		try { sleep(sleepytime); } catch(InterruptedException e) { }
            } else {
                if(worldTime % 1000 == 0)
                    System.out.println(worldTime);
            }

            if(stopAfter >= 0 && worldTime == stopAfter)
                break;
        }

        if(parent == null) {
            String statsFile;

            System.out.println(lightType);
            statsFile = lightType + ".";
        	System.out.println(buildingFile);
        	statsFile += buildingFile + ".";
        	if(intersectionFile != null) {
        		System.out.println(intersectionFile);
        		statsFile += intersectionFile + ".";
        	}
        	if(roadFile != null) {
        		System.out.println(roadFile);
        		statsFile += roadFile + ".";
        	}
        	statsFile += "stats";

        	try {
		    	BufferedWriter out = new BufferedWriter(new FileWriter(statsFile));

		    	for(int i = 0; i < carStats.size(); i++) {
		    	    CarStatisticsElement e = (CarStatisticsElement) carStats.get(i);
		    	    out.write(i + "\t" + e.toString());
		    	    out.newLine();
		    	}
		    	out.close();
        	} catch(IOException e) {
        	    System.out.println("Failed to write stats file!");
        	}

        	System.out.println("Ticks\t" + stopAfter);
	        System.out.println("Completed Routes\t" + carStats.size());

	        System.out.println("Stats\tMean\tMedian\tMax\tMin");
	        System.out.println("Wait/Dist\t"
					+ carStats.getMeanStat(CarStatistics.WAIT_PER_DIST) + "\t"
					+ carStats.getMedianStat(CarStatistics.WAIT_PER_DIST) + "\t"
					+ carStats.getMaxStat(CarStatistics.WAIT_PER_DIST) + "\t"
					+ carStats.getMinStat(CarStatistics.WAIT_PER_DIST));
			System.out.println("Route Time\t"
					+ carStats.getMeanStat(CarStatistics.TRAVEL_TIME) + "\t"
					+ carStats.getMedianStat(CarStatistics.TRAVEL_TIME) + "\t"
					+ carStats.getMaxStat(CarStatistics.TRAVEL_TIME) + "\t"
					+ carStats.getMinStat(CarStatistics.TRAVEL_TIME));
			System.out.println("Max Wait\t"
					+ carStats.getMeanStat(CarStatistics.WORST_WAIT) + "\t"
					+ carStats.getMedianStat(CarStatistics.WORST_WAIT) + "\t"
					+ carStats.getMaxStat(CarStatistics.WORST_WAIT) + "\t"
					+ carStats.getMinStat(CarStatistics.WORST_WAIT));
			System.out.println("Jitter\t"
					+ carStats.getMeanStat(CarStatistics.JITTER) + "\t"
					+ carStats.getMedianStat(CarStatistics.JITTER) + "\t"
					+ carStats.getMaxStat(CarStatistics.JITTER) + "\t"
					+ carStats.getMinStat(CarStatistics.JITTER));
        }
    }

    public static void main(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("") || args[i].equals("null"))
                args[i] = null;
        }

        SwarmCity theCity = new SwarmCity(null, args[0], args[1], args[2], args[3], Integer.parseInt(args[4]));

        theCity.run();
    }

	public void setPaused(boolean p) {
		paused = p;
    }

    public void setDelay(int value) {
    	sleepytime = value;
    }
}
