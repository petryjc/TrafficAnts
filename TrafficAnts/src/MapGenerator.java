import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class MapGenerator {
	String filename;
	private float percentAStar;
	private int totalNumOfCars;
	private int duration;
	private int numOfCities;
	private int arrayDim;
	private int intersectionLocs[][];
	private ArrayList<Car> carsToWrite;
	private ArrayList<Road> roadsToWrite;
	private ArrayList<Intersection> intersectionsToWrite;
	private ArrayList<Character> carTypes;  
	
	final int MIN_DIST_BET_CITIES = 25;
	
	public static void main(String[] args) {
		String subfilename = "Blocking2";
		Intersection.persistenceFile = subfilename + "Intersection.xml";
		
		 MapGenerator m = new MapGenerator(subfilename + "Map.txt", 100, 200, 10,
		 1f, 1000);
		 m.generateMapFile();
		 m.printArray();
		 Intersection.persist(Integer.MAX_VALUE);
	}
	
	public MapGenerator(String filename, int numberOfCities, int width, int duration, float percentAStar, int totalNumOfCars){
		this.filename = filename;
		this.percentAStar = percentAStar;
		this.totalNumOfCars = totalNumOfCars;
		this.duration = duration;
		this.arrayDim = width/MIN_DIST_BET_CITIES;
		this.numOfCities = numberOfCities;
		this.intersectionLocs = new int[arrayDim][arrayDim];
		System.out.println(this.arrayDim);
		for(int i = 0; i < arrayDim; i++){
			for(int j = 0; j < arrayDim; j++ ){
				this.intersectionLocs[i][j] = -1;
			}
		}
		
		this.carsToWrite = new ArrayList<Car>();
		this.roadsToWrite = new ArrayList<Road>();
		this.intersectionsToWrite = new ArrayList<Intersection>();
		this.carTypes = new ArrayList<Character>();
	}
	public void generateMapFile(){
		designateIntersections();
		designateRoads();
		designateCars();
		writeDataToFile();
	}
	
	public void printArray(){
		for(int i = 0; i < this.arrayDim; i++){
			for(int j = 0; j < this.arrayDim; j++){
				System.out.print(this.intersectionLocs[i][j] + ",");
			}
			
			System.out.print("\n");
		}
	}
	
	private void designateIntersections(){
		int counter = 0;
		Random r = new Random();
		for(int i = 0; i < this.arrayDim; i++){
			if(counter >= this.numOfCities){
				break;
			}
			for(int j = 0; j < this.arrayDim; j++ ){
				if(r.nextBoolean()){
					this.intersectionLocs[i][j] = counter;
					this.intersectionsToWrite.add(new Intersection(counter, j * MIN_DIST_BET_CITIES, i * MIN_DIST_BET_CITIES));
					counter+=1;
				}
				if(counter >= this.numOfCities){
					break;
				}
			}
		}
		if(this.intersectionsToWrite.size() == 0){
			this.intersectionLocs[0][0] = 0;
			this.intersectionsToWrite.add(new Intersection(0, 0 * MIN_DIST_BET_CITIES, 0 * MIN_DIST_BET_CITIES));
			this.intersectionLocs[this.arrayDim - 1][this.arrayDim - 1] = 1;
			this.intersectionsToWrite.add(new Intersection(1, (this.arrayDim - 1) * MIN_DIST_BET_CITIES, (this.arrayDim - 1) * MIN_DIST_BET_CITIES));
		}else if(this.intersectionsToWrite.size() == 1){
			if(this.intersectionLocs[this.arrayDim - 1][this.arrayDim - 1] == -1){
				this.intersectionLocs[this.arrayDim - 1][this.arrayDim - 1] = 1;
				this.intersectionsToWrite.add(new Intersection(1, (this.arrayDim - 1) * MIN_DIST_BET_CITIES, (this.arrayDim - 1) * MIN_DIST_BET_CITIES));
			}else{
				this.intersectionLocs[0][0] = 1;
				this.intersectionsToWrite.add(new Intersection(1, 0 * MIN_DIST_BET_CITIES, 0 * MIN_DIST_BET_CITIES));
			}
		}
	}
	
	private void designateRoads(){
		for(int i = 0; i < this.arrayDim; i++){
			for(int j = 0; j < this.arrayDim; j++ ){
				if(this.intersectionLocs[i][j] > -1){					
					connectIntersections(j,i);
				}
			}
		}
	}
	int roadId = 0;
	private void connectIntersections(int x,int y){
		int currentIndex = this.intersectionLocs[y][x];
		
		//northern
		int bestIndex = -1;
		int proximity = -1;
		
		for(int i = 0; i < this.arrayDim; i++){
			if(y == 0){ 
				break;
			}
			if(this.intersectionLocs[y-1][i] == -1){
				continue;
			}
			
			if(Math.abs(x - i) < proximity || bestIndex == -1){
				bestIndex = this.intersectionLocs[y-1][i];
				proximity = x - i;
			}
		}
		Random r = new Random();
		double speedLimit = r.nextDouble() + 1;
		
		if(bestIndex > -1){
			this.roadsToWrite.add(new Road(this.roadId++, this.intersectionsToWrite.get(bestIndex), this.intersectionsToWrite.get(currentIndex), speedLimit));
			this.roadsToWrite.add(new Road(this.roadId++, this.intersectionsToWrite.get(currentIndex), this.intersectionsToWrite.get(bestIndex), speedLimit));
		}
		
		//eastern
		bestIndex = -1;
		
		for(int j = x-1; 0 <= j && j < this.arrayDim;j--){
			if(this.intersectionLocs[y][j] == -1){
				continue;
			}
			
			bestIndex = this.intersectionLocs[y][j];
			break;
		}
		
		speedLimit = r.nextDouble() + 1;
		if(bestIndex > -1){
			this.roadsToWrite.add(new Road(this.roadId++, this.intersectionsToWrite.get(bestIndex),this.intersectionsToWrite.get(currentIndex),speedLimit));
			this.roadsToWrite.add(new Road(this.roadId++, this.intersectionsToWrite.get(currentIndex),this.intersectionsToWrite.get(bestIndex),speedLimit));
		}
		
	}
	int carId = 0;
	public void designateCars(){
		Random r = new Random();
		int carCounterCap = (int) (this.percentAStar * this.totalNumOfCars); 
		char type = '*';
		for(int i = 0; i < this.totalNumOfCars; i ++){
			if(i == carCounterCap){
				type = '$';
			}
			int start = r.nextInt(this.intersectionsToWrite.size());
			int end = r.nextInt(this.intersectionsToWrite.size());
			
			while(start == end){
				System.out.println(start + " " + end);
				end = r.nextInt(this.intersectionsToWrite.size());
			}

			int startTime = (int) (r.nextFloat() * this.duration);
			
			this.carTypes.add(type);
			
			if(type == '*'){	
				this.carsToWrite.add(new CarAStar(carId++, this.intersectionsToWrite.get(start), this.intersectionsToWrite.get(end), startTime));
			}else{
				this.carsToWrite.add(new CarAStar(carId++, this.intersectionsToWrite.get(start), this.intersectionsToWrite.get(end), startTime));
			}
		}
	}
	
	public void writeDataToFile(){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename));
			
			writer.write("" + this.duration + "\n");
			
			for(Intersection i : this.intersectionsToWrite){
				writer.write("i," + i.id + ","+ i.location.x + "," + i.location.y + "\n");
			}
			for(Road r : this.roadsToWrite){
				writer.write("r," + r.id + "," + r.start.id + "," + r.end.id + "," + r.speedLimit + "\n");
			}
			int counter = 0;
			for(Car c : this.carsToWrite){
				writer.write("c," + c.id + "," + c.start.id + "," + c.destination.id + "," + c.startTime + "," + this.carTypes.get(counter) + "\n");
				counter++;
			}
			
			writer.close();
			
		} catch (IOException e) {
			System.out.println("Generator failed to write to file");
			e.printStackTrace();
		}
		
	}
	
}
