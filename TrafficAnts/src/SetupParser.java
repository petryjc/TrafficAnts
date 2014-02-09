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
	
	public void initialSetup(){
		populateIntersections();
		populateRoads();
		populateCars();
		System.out.println("Finished loading the Mapfile");
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
				double arg3 = Double.parseDouble(brokenString[3]);
				
		
				Intersection start = Intersection.intersectionList.get(arg1);
				Intersection end = Intersection.intersectionList.get(arg2);
				
				Double speedLimit = arg3;
				new Road(start, end, speedLimit);
				
				counter++;
			}
			reader.close();
			
		} catch (IOException e) {
			System.out.println("The file was unreadable or did not exist");
			e.printStackTrace();
		}
	}
	//start, end, startTime, type
	public void populateCars(){
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
				String arg4 = brokenString[4];
				
				Intersection start = Intersection.intersectionList.get(arg1);
				Intersection end = Intersection.intersectionList.get(arg2);
				
				if(arg4.compareTo("*") == 0){
					new CarAStar(start, end, arg3);
				}else{
					System.out.println("Car that isn't a star");
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
