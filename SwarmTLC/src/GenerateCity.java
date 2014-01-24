import java.util.*;

public class GenerateCity {
    //java GenerateCity [width] [height] [density] [popdensity]
    
    //Where 'density' is defined as the likelihood that any
    //given intersection has an attractive building on one
    //of the four corner's
    
    //Where 'popdensity' is defined as the maximal amount any 
    //single building will contain at start
    
    public static void main(String[] args) {
	    int width = Integer.parseInt(args[0]);
	    int height = Integer.parseInt(args[1]);
	    float density = Float.parseFloat(args[2]);
	    int popdensity = Integer.parseInt(args[3]);
	    
	    Random rand = new Random();
	    
	    System.out.println("S " + width + " " + height);
	    
	    for(int i = 0; i < width; i++) {
	        for(int j = 0; j < height; j++) {
	        	for(int k = 0; k < 4; k++) {
	        	    if(rand.nextFloat() <= density) {
	        	        System.out.println("A " + i + " " + j + " " + rand.nextInt(popdensity) + " | 10 0 0 10 | 10 0 0 10");
	        	    }
	        	}
	        }
	    }
	}
}
