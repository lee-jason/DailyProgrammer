package _03_12_2014.spaceprobe;

import java.awt.Point;
import java.security.InvalidParameterException;
import java.util.Random;

public class Map {
	private Entity[][] map;
	private final double ASTEROID_PERCENTAGE = 0.05;
	private final double GRAVITYWELL_PERCENTAGE = 0.03;
	private Random random;
	
	public Map(int dimension, Random random) throws InvalidParameterException{
		this.random = random;
		if(ASTEROID_PERCENTAGE + GRAVITYWELL_PERCENTAGE > 1){
			throw new InvalidParameterException();
		}
		map = new Entity[dimension][dimension];
		generateMap();
	}
	
	//randomly select a square
	//place token in that square
	//otherwise if filled, try random again
	//may need to change search to move to next closest square as percentage of asteroids and gravity wells increase.
	public void generateMap(){
		int units = map[0].length * map.length;
		int asteroidUnits = (int) (units * ASTEROID_PERCENTAGE);
		int gravitywellUnits = (int) (units * GRAVITYWELL_PERCENTAGE);
		int asteroidCount = 0;
		int gravityCount = 0;
		
		while(gravityCount < gravitywellUnits){
			int randX = random.nextInt(map.length);
			int randY = random.nextInt(map.length);
			
			if(fillEntity(Entity.gravity, new Point(randX, randY))){
				gravityCount++;
			}
		}
		while(asteroidCount < asteroidUnits){
			int randX = random.nextInt(map.length);
			int randY = random.nextInt(map.length);
			
			if(fillEntity(Entity.asteroid, new Point(randX, randY))){
				asteroidCount++;
			}
		}
		for(int y = 0; y < map[0].length; y++){
			for(int x = 0; x < map.length; x++){
				fillEntity(Entity.empty, new Point(x, y));
			}
		}
	}
	
	private void paintGravityPull(Point point){
		int originX = point.x - 1;
		int originY = point.y - 1;

		for(int y = originY; y < originY + 3; y++){
			for(int x = originX; x < originX + 3; x++){
				try{
					fillEntity(Entity.gravitypull, new Point(x, y));
				}
				catch(ArrayIndexOutOfBoundsException e){
					System.out.println("gravity well check out of bounds no biggie");
				}
			}
		}
	}
	
	/**
	 * 
	 * @param entity
	 * @param point
	 * @return true if entity was able to be filled
	 */
	private boolean fillEntity(Entity entity, Point point){
		if(!hasEntity(point)){
			map[point.x][point.y] = entity;
			if(entity == Entity.gravity){
				paintGravityPull(point);
			}
			return true;
		}
		return false;
	}
	
	private boolean hasEntity(Point point){
		if(map[point.x][point.y] == Entity.asteroid || map[point.x][point.y] == Entity.gravity || map[point.x][point.y] == Entity.gravitypull){
			return true;
		}
		return false;
	}
	
	public Entity[][] getMap(){
		return map;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(int y = 0; y < map[0].length; y++){
			for(int x = 0; x < map.length; x++){
				if(map[x][y] == Entity.asteroid){
					sb.append('a');
				}
				else if(map[x][y] == Entity.gravity){
					sb.append('G');
				}
				else if(map[x][y] == Entity.gravitypull){
					sb.append('X');
				}
				else if(map[x][y] == Entity.empty){
					sb.append('.');
				}
				//sb.append(", ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
