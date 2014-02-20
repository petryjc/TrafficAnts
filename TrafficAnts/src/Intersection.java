import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.io.File;

public class Intersection extends Time{
	public static ArrayList<Intersection> intersectionList = new ArrayList<Intersection>();
	
	static int ticksPerLight = 3;
	static String persistenceFile = "Intersections.xml";
	int currentCount = 0;
	Road currentRoad;
	int id;
	final Point location;
	
	private ArrayList<Road> incomingRoads;
	ArrayList<Road> getIncomingRoads() {
		if (incomingRoads == null) {
			incomingRoads = new ArrayList<Road>();
			for (Road r : Road.roadList) {
				if (r.end == this) {
					incomingRoads.add(r);
				}
			}
		}
		return incomingRoads;
	}
	
	private ArrayList<Road> outgoingRoads;
	ArrayList<Road> getOutgoingRoads() {
		if (outgoingRoads == null) {
			outgoingRoads = new ArrayList<Road>();
			for (Road r : Road.roadList) {
				if (r.start == this) {
					outgoingRoads.add(r);
				}
			}
		}
		return outgoingRoads;
	}

	private HashMap<Intersection, HashMap<Road,Double>> pheromone;
	public HashMap<Road,Double> getPheromone(Intersection destination) {
		return getPheromoneAll().get(destination);
	}
	public HashMap<Intersection, HashMap<Road,Double>> getPheromoneAll() {
		if(pheromone == null) {
			pheromone = new HashMap<Intersection, HashMap<Road,Double>>();
			for(Intersection i: intersectionList) {
				HashMap<Road,Double> map = new HashMap<Road,Double>();
				for(Road r : getOutgoingRoads()) {
					map.put(r, 1.0/(Utils.routeTime(r.end, i, new Utils.TimeUpdator()) + r.distance()/r.speedLimit));
				}
				pheromone.put(i, map);
			}
		}
		return pheromone;
	}
	
	
	public Intersection(int id, int x, int y) {
		this.location = new Point(x,y);
		this.id = id;
		intersectionList.add(this);
	}
	
	public void advanceTime() {
		currentCount++;
		if(currentRoad == null && getIncomingRoads().size() > 0) {
			currentRoad = getIncomingRoads().get(0);
		}
		if(currentCount > ticksPerLight) {
			currentCount = 0;
			currentRoad = getIncomingRoads().get((getIncomingRoads().indexOf(currentRoad) + 1) % getIncomingRoads().size());
		}
		for(Intersection destination : intersectionList) {
			if(getPheromone(destination) == null) {
				System.out.println("Here");
			}
			for(Road r : getPheromone(destination).keySet()) {
				getPheromone(destination).put(r, getPheromone(destination).get(r) * CarSwarm.decayInverse);
			}
		}
	}
	
	public boolean canGoThrough(Car c) {
		return c.currentRoad == this.currentRoad;
	}
	
	public void draw(Graphics g2){
		
		g2.setColor(Color.black);
		double x = (this.location.getX()-3) * DrawPanel.offset + DrawPanel.xOffset;
		double y = (this.location.getY()-3) * DrawPanel.offset + DrawPanel.yOffset;
		int dim = 30;
		
		g2.fillRect((int) x, (int) y , dim, dim);		
	}

	@Override
	public String toString() {
		return "I:" + this.id;
	}
	
	public static void persist(double averageTime) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(persistenceFile));
			writer.write(averageTime + "\n");
			writer.write("<Intersections>");
			for(Intersection i: Intersection.intersectionList) {
				writer.write(convertToXML(i) + "\n");
			}
			writer.write("</Intersections>");
			writer.close();
		} catch (IOException e) {
			System.out.println("Could not save intersection data");
		}
	}
	
	private static String convertToXML(Intersection intersection) {
		StringBuilder sb = new StringBuilder("<");
        sb.append(intersection);
        sb.append(">");

        for (Entry<Intersection, HashMap<Road, Double>> e : intersection.getPheromoneAll().entrySet()) {
            sb.append("<");
            sb.append(e.getKey());
            sb.append(">");

            for (Map.Entry<Road,Double> e2 : e.getValue().entrySet()) {
                sb.append("<");
                sb.append(e2.getKey());
                sb.append(">");

                sb.append(e2.getValue());
                sb.append("</");
                sb.append(e2.getKey());
                sb.append(">");
            }
            
            sb.append("</");
            sb.append(e.getKey());
            sb.append(">");
        }

        sb.append("</");
        sb.append(intersection);
        sb.append(">");

        return sb.toString();
	}

	public static double parse() {
		double pastTime = Double.MAX_VALUE;
		try {
			InputStream reader = new FileInputStream(new File(persistenceFile));
			int cur = '"';String build = "";
			while((cur = reader.read()) != '\n') {
				build += (char)cur;
			}
			System.out.println("Previous best " + build);
//			BufferedReader r = new BufferedReader(new InputStreamReader(reader));
			//File fXmlFile = new File(persistenceFile);
			//fXmlFile.
			pastTime = Double.parseDouble(build);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(reader);
		 
			doc.getDocumentElement().normalize();
		 
			NodeList nList = doc.getChildNodes().item(0).getChildNodes();
			
		 
			System.out.println("----------------------------");
		 
			for (int temp = 0; temp < nList.getLength(); temp++) {
		 
				Node intersectionNode = nList.item(temp);
				if (intersectionNode.getNodeType() == Node.ELEMENT_NODE) {
					Intersection current = Intersection.intersectionList.get(Integer.parseInt(intersectionNode.getNodeName().substring(2,intersectionNode.getNodeName().length())));
					current.pheromone = new HashMap<Intersection, HashMap<Road,Double>>();
					NodeList destinations = intersectionNode.getChildNodes();
					
//					for(Intersection i: intersectionList) {
//						HashMap<Road,Double> map = new HashMap<Road,Double>();
//						for(Road r : getOutgoingRoads()) {
//							map.put(r, 0.00001);
//						}
//						pheromone.put(i, map);
//					}
					
					for (int i = 0; i < destinations.getLength(); i++) {
						Node destinationNode = destinations.item(i);
						if(destinationNode.getNodeType() == Node.ELEMENT_NODE) {
							Intersection destination = Intersection.intersectionList.get(Integer.parseInt(destinationNode.getNodeName().substring(2, destinationNode.getNodeName().length())));
							HashMap<Road,Double> map = new HashMap<Road,Double>();
							NodeList roads = destinationNode.getChildNodes();
							for(int j = 0; j < roads.getLength(); j++) {
								Node roadNode = roads.item(j);
								if(roadNode.getNodeType() == Node.ELEMENT_NODE) {
									map.put(Road.roadList.get(Integer.parseInt(roadNode.getNodeName().substring(2,roadNode.getNodeName().length()))), Double.parseDouble(roadNode.getTextContent()));
								}
							}
							current.pheromone.put(destination, map);
						}
					}
			 
				}
				
				
			}			
		} catch (Exception e) {
			System.out.println("Could not parse the intersection pharamone");
			e.printStackTrace();
		}
		return pastTime;		
	}
}
