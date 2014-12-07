package _03_12_2014.spaceprobe;

import java.awt.Point;
import java.security.InvalidParameterException;
import java.util.Random;

public class Map {
	private Entity[][] map;
	private final double ASTEROID_PERCENTAGE = 0.3;
	private final double GRAVITYWELL_PERCENTAGE = 0.05;
	private Point start;
	private Point end;
	private Random random;
	
	//TODO remove exceptions from flow control
	public Map(int dimension, Point start, Point end, Random random) throws InvalidParameterException{

		this.random = random;
		this.start = start;
		this.end = end;
		if(ASTEROID_PERCENTAGE + GRAVITYWELL_PERCENTAGE > 1){
			throw new InvalidParameterException();
		}
			map = new Entity[dimension][dimension];
			generateMap();
		try{
			setStartEnd(this.start, this.end);
		}
		catch(ArrayIndexOutOfBoundsException e){
			throw new ArrayIndexOutOfBoundsException("Start end points on map are out of map bounds");
		}
		
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
	
	private void setStartEnd(Point start, Point end){
		map[this.start.x][this.start.y] = Entity.start;
		map[this.end.x][this.end.y] = Entity.end; 
	}
	
	/**
	 * a* search from start point to end point.
	 */
	public void findShortestPath(){
		KeyValuePriorityQueue<Double, PathNode> pq = new KeyValuePriorityQueue<Double, PathNode>();
		PathNode[][] pathMap = new PathNode[map.length][map[0].length];
		
		
		//start the origin point
		PathNode currPathNode = new PathNode(null, start, 0);
		pathMap[start.x][start.y] = currPathNode;
		pq.add(calculatePathCost(currPathNode), currPathNode);
		//until the end has not been met or while the priorityqueue still has areas to go through
		//TODO: has an issue when the start point is surrounded on all sides since the beginning
		do{
			//pop off the first item in priorityqueue and make it the currentPoint
			currPathNode = pq.remove();
			//add surrounding non visited nodes to priorityqueue where the key is the stepsFromStart + distance to end and value is the current point
			addSurroundingNodesToPriorityQueue(pq, pathMap, currPathNode);
			if(currPathNode.point.equals(end)){
				break;
			}
		}
		while((pq.peek() != null));
		
		System.out.println(pathMap[end.x][end.y]);
	}
	
	private void addSurroundingNodesToPriorityQueue(KeyValuePriorityQueue<Double, PathNode> pq, PathNode[][] pathMap, PathNode pathNode){
		int originX = pathNode.point.x - 1;
		int originY = pathNode.point.y - 1;
		
		for(int y = originY; y < originY + 3; y++){
			for(int x = originX; x < originX + 3; x++){
				try{
					
					//only add non traversed neighboring path nodes. ignore visited path nodes. also go around entities, asteroids, gravity
					if(pathMap[x][y] == null && !hasEntity(new Point(x, y), true)){
						PathNode neighborNode = new PathNode(pathNode, new Point(x, y),pathNode.stepsFromStart+1);
						if(Math.abs(pathNode.point.x - neighborNode.point.x) >= 2 || Math.abs(pathNode.point.y - neighborNode.point.y) >= 2){
							System.out.println("something went wrong.");
						}
						pathMap[neighborNode.point.x][neighborNode.point.y] = neighborNode;
						pq.add(new Double(calculatePathCost(neighborNode)), neighborNode);
					}
				}
				catch(IndexOutOfBoundsException e){
					//ignore
				}
			}
		}
		
	}
	
	private double calculatePathCost(PathNode pathNode){
		
		return pathNode.stepsFromStart + Math.pow(pathNode.point.x - end.x, 2) + Math.pow(pathNode.point.y - end.y, 2);
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
					//do nothing continue the program.
				}
			}
		}
	}
	
	private class PathNode{
		public PathNode parent = null;
		public Point point;
		public int stepsFromStart;
		public PathNode(PathNode parent, Point point, int stepsFromStart){
			this.parent = parent;
			this.point = point;
			this.stepsFromStart = stepsFromStart;
		}
		
		public String toString(){
			return this.point.toString();
		}
	}
	
	/**
	 * 
	 * @param entity
	 * @param point
	 * @return true if entity was able to be filled
	 */
	private boolean fillEntity(Entity entity, Point point){
		boolean countGravityPull = true;
		if(entity == Entity.gravity){
			countGravityPull = false;
		}
		if(!hasEntity(point, countGravityPull)){
			map[point.x][point.y] = entity;
			if(entity == Entity.gravity){
				paintGravityPull(point);
			}
			return true;
		}
		return false;
	}
	
	private boolean hasEntity(Point point, boolean countGravityPull){
		if(map[point.x][point.y] == Entity.asteroid || map[point.x][point.y] == Entity.gravity || (map[point.x][point.y] == Entity.gravitypull && countGravityPull)){
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
				else if(map[x][y] == Entity.start){
					sb.append('S');
				}
				else if(map[x][y] == Entity.end){
					sb.append('E');
				}
				//sb.append(", ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
