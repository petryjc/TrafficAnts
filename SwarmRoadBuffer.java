public class SwarmRoadBuffer {
    public ACar[] buffer;
    public static final int numPossCars = 15;

    public SwarmRoadBuffer() {
        // Default to 10 cars per lane
        buffer = new ACar[numPossCars];
    }

    public boolean addCar(ACar car) {
        if(buffer[0] != null)
            return false;

        buffer[0] = car;
        return true;
    }

    public boolean hasWaitingCar() {
        return (buffer[buffer.length - 1] != null);
    }

    public ACar getWaitingCar() {
        if(buffer[buffer.length - 1] == null)
            return null;
        else
            return buffer[buffer.length - 1];
    }

    public ACar takeWaitingCar() {
        if(buffer[buffer.length - 1] == null)
            return null;

        ACar ret = buffer[buffer.length - 1];
        buffer[buffer.length - 1] = null;
        return ret;
    }

    public int getNumCars() {
        int total = 0;
        for(int i = 0; i < buffer.length; i++) {
			if (buffer[i] != null)
				total++;
        }
        return total;
    }
    
    public void tick() {
        // Shift all of the cars towards the end of the buffer
        for(int i = 0; i < buffer.length; i++) {
            if(buffer[i] != null) {
                if(i+1 < buffer.length && buffer[i+1] == null) {
                    buffer[i].statsMoved();
                
                    buffer[i+1] = buffer[i];
                    buffer[i] = null;
                    i++; // move the increment and extra one so that we only move this car one spot
                } else {
                    buffer[i].statsWaited();
                }
            }
        }
    }
}
