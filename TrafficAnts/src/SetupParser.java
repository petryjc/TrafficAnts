import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SetupParser {

	String filename;
	int duration;

	public SetupParser(String filename) {
		this.filename = filename;
		this.duration = 0;
	}
	
	public void initialSetup(Class<? extends Car> carType){
		populateIntersections();
		populateRoads();
		populateCars(carType);
	}
	//id, x, y, 
	public void populateIntersections() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					this.filename));
			String line = null;
			int counter = 0;
			
			while ((line = reader.readLine()) != null) {
				String[] brokenString = line.split("\\,");
				
				if(counter == 0){
					this.duration = Integer.parseInt(brokenString[0]);
					counter++;
					continue;
				}
				
				if(!lineCheck(brokenString, "i", counter)){
					continue;
				}
				
				int arg1 = Integer.parseInt(brokenString[1]);
				int arg2 = Integer.parseInt(brokenString[2]);
				double arg3 = Double.parseDouble(brokenString[3]);
				
				new Intersection(arg1, arg2, (int) arg3);
				counter+=1;
			}
			
			reader.close();
			
		} catch (IOException e) {
			System.out.println("The file was unreadable or did not exist");
			e.printStackTrace();
		}
	}
	//start index, end index, speed limit
	public void populateRoads(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					this.filename));
			String line = null;
			int counter = 0;
			while ((line = reader.readLine()) != null) {
				String[] brokenString = line.split("\\,");
				
				
				
				if(counter == 0 || !lineCheck(brokenString, "r", counter)){
					counter++;
					continue;
				}
				
				int arg1 = Integer.parseInt(brokenString[1]);
				int arg2 = Integer.parseInt(brokenString[2]);
				int arg3 = Integer.parseInt(brokenString[3]);
				double arg4 = Double.parseDouble(brokenString[4]);
				
		
				Intersection start = Intersection.intersectionList.get(arg2);
				Intersection end = Intersection.intersectionList.get(arg3);
				
				Double speedLimit = arg4;
				new Road(arg1, start, end, speedLimit);
				
				counter++;
			}
			reader.close();
			
		} catch (IOException e) {
			System.out.println("The file was unreadable or did not exist");
			e.printStackTrace();
		}
	}
	//start, end, startTime, type
	public void populateCars(Class<? extends Car> carType){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					this.filename));
			String line = null;
			int counter = 0;
			while ((line = reader.readLine()) != null) {
				String[] brokenString = line.split("\\,");
				
				if(counter == 0 || !lineCheck(brokenString, "c", counter)){
					counter++;
					continue;
				}
				
				int arg1 = Integer.parseInt(brokenString[1]);
				int arg2 = Integer.parseInt(brokenString[2]);
				int arg3 = Integer.parseInt(brokenString[3]);
				int arg4 = Integer.parseInt(brokenString[4]);
				String arg5 = brokenString[5];
				
				Intersection start = Intersection.intersectionList.get(arg2);
				Intersection end = Intersection.intersectionList.get(arg3);
				
				if(carType == null) {
					if(arg5.compareTo("*") == 0){
						new CarAStar(arg1, start, end, arg4);
					}else{
						new CarSwarm(arg1, start, end, arg4);
					}
				} else {
					try {
						carType.getConstructor(int.class, Intersection.class,Intersection.class,int.class).newInstance(arg1, start, end, arg4);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				
				counter++;
			}
			reader.close();
			
		} catch (IOException e) {
			System.out.println("The file was unreadable or did not exist");
			e.printStackTrace();
		}
	}
	
	public boolean lineCheck(String[] line, String expected, int counter){
		if(line.length < 4){
			if(line.length >= 1){ 
				if(line[0].compareTo(expected) == 0){
					System.out.println("the mapfile has an input with to few args at line: " + counter);
				}
			}else{
				System.out.println("The text file is broken at line: "  + counter);
			}
			return false;
		}else{
			if(line[0].compareTo(expected) == 0){
				return true;
			}else{
				return false;
			}
		}
	}
}
