public class SwarmPoint {
    public int x;
    public int y;
    
    public SwarmPoint(int $x, int $y) {
        x = $x;
        y = $y;
    }
    
    public int getX(){
    	return x;
    }    
    
    public int getY(){
    	return y;
    }
    
    public boolean equals(SwarmPoint a) {
        return (a.x == x && a.y == y);
    }
    
    public double distanceTo(SwarmPoint a) {
        if(a.x < 0 || a.y < 0 || x < 0 || y < 0) // if it is a wrap-around, it is a nonexistant distance
            return Double.POSITIVE_INFINITY;
        
        return Math.sqrt(Math.pow(x - a.x, 2) + Math.pow(y - a.y, 2));
    }
    
    public String toString() {
        return "(" + String.valueOf(x) + ", " + String.valueOf(y) + ")";
    }
    
    public boolean morePositiver(SwarmPoint otherPoint){
    	if ((otherPoint.x > x) || (otherPoint.y > y))
    		return true;
    	return false;
    }
}
