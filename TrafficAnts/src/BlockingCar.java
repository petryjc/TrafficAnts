import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;


public class BlockingCar extends Car{

		public BlockingCar(int id, Intersection start, Intersection destionation, int startTime) {
			super(id, start, destionation, startTime);
		}

		@Override
		public Road nextRoad() {
			return null;
		}
		
		@Override
		public void advanceTime(){
			if(this.startTime == Time.ticks){
				this.currentRoad = this.start.getOutgoingRoads().get(0);
				this.distanceAlongRoad = this.currentRoad.distance() - 3;
				this.currentRoad.currentCars.add(this);
			}
			
			if(Time.ticks == this.startTime + 100){
				this.finished = true;
				this.currentRoad.currentCars.remove(this);
				this.currentRoad = null;
			}
		}
		@Override
		public void draw(Graphics g){
			if(this.finished){
				return;
			}
			if(this.currentRoad == null){
				return;
			}
			Double x = this.currentRoad.start.location.getX() + (this.currentRoad.dir.getX() * this.distanceAlongRoad);
			Double y = this.currentRoad.start.location.getY() + (this.currentRoad.dir.getY() * this.distanceAlongRoad);
			Point2D.Double carLoc = new Point2D.Double((x*DrawPanel.offset)+DrawPanel.xOffset ,(y*DrawPanel.offset)+DrawPanel.yOffset);
		
			
			g.setColor(Color.ORANGE);
			
			g.fillRect((int)carLoc.getX() - 2, (int)carLoc.getY() - 2,30,30);
			
		}
		
}
