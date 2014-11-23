package _14_11_2014.arrowsandarrows;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LargestLoop {

	
	public static void main(String[] args){
		FileReader fr = null;
		BufferedReader br;
		String currLine;
		int width;
		int height;
		Direction[][] map = null;
		ArrayList<Cycle> cycleList = new ArrayList<Cycle>();
		
		try {
			fr = new FileReader("src/_14_11_2014/arrowsandarrows/input");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("file not found");
		}
		
		br = new BufferedReader(fr);
		
		try {
			String[] dimensions = br.readLine().split(" ");
			width = Integer.parseInt(dimensions[0]);
			height = Integer.parseInt(dimensions[1]);
			
			//initialize map for consumption
			map = new Direction[width][height];
			
			int heightCounter = 0;
			currLine = br.readLine();
			while(currLine != null){
				for(int i = 0; i < currLine.length(); i++){
					if(currLine.charAt(i) == '^'){
						map[i][heightCounter] = Direction.up;
					}
					else if(currLine.charAt(i) == '>'){
						map[i][heightCounter] = Direction.right;
					}
					else if(currLine.charAt(i) == '<'){
						map[i][heightCounter] = Direction.left;
					}
					else if(currLine.charAt(i) == 'v'){
						map[i][heightCounter] = Direction.down;
					}
				}
				
				currLine = br.readLine();
				heightCounter++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("no more lines to read");
		}
		
		//map is ready to be consumed and analyzed
		for(int y = 0; y < map[0].length; y++){
			for(int x = 0; x < map.length; x++){
				if(map[x][y] != Direction.visited){
					cycleUntilLoop(map, new Point(x, y), new Point(x, y), cycleList);
				}
				else{
					continue;
				}
				
			}
		}
		
		System.out.println("complete list of loops");
		System.out.println(cycleList);
		System.out.println("longest cycle");
		System.out.println(longestCycleInList(cycleList));
		
		
	}
	
	private static Cycle longestCycleInList(List<Cycle> cycleList){
		Cycle longestCycle = null;
		for(int i = 0; i < cycleList.size(); i++){
			if(longestCycle == null || cycleList.get(i).length > longestCycle.length){
				longestCycle = cycleList.get(i);
			}
		}
		return longestCycle;
	}
	
	private static Cycle cycleUntilLoop(Direction[][] map, Point beginning, Point currPoint, List<Cycle> cycleList){
		//end condition, check whether cycle loops back to start of cycle or start of where cycle search initially started
		if(map[currPoint.x][currPoint.y] == Direction.visited){
			Cycle newCycle = new Cycle(new Point(currPoint.x, currPoint.y), 1);
			return newCycle;
		}
		else if(currPoint.equals(beginning) && map[currPoint.x][currPoint.y] == Direction.visited){
			return null;
		}
		
		
		//set current point as visited then go through the map
		Point nextPoint = getNextDirectedPoint(map, currPoint);
		map[currPoint.x][currPoint.y] = Direction.visited;
		Cycle cycle = cycleUntilLoop(map, beginning, nextPoint, cycleList);
		if(cycle != null){
			if(currPoint.equals(cycle.startingPoint)){
				cycleList.add(cycle);
				return null;
			}
			cycle.length += 1;
			return cycle;
		}
		else{
			return null;
		}
	}
	
	private static Point getNextDirectedPoint(Direction[][] map, Point currPoint){
		Point nextPoint = (Point) currPoint.clone();
		
		if(map[currPoint.x][currPoint.y] == Direction.down){
			//if we get a cycle back, increase the length, and return it
			nextPoint.y += 1;
		}
		else if(map[currPoint.x][currPoint.y] == Direction.up){
			nextPoint.y -= 1;
		}
		else if(map[currPoint.x][currPoint.y] == Direction.left){
			nextPoint.x -= 1;
		}
		else if(map[currPoint.x][currPoint.y] == Direction.right){
			nextPoint.x += 1;
		}
		
		if(nextPoint.x == -1){
			nextPoint.x = map.length - 1;
		}
		else if(nextPoint.y == -1){
			nextPoint.y = map[0].length - 1;
		}
		else if(nextPoint.x == map.length){
			nextPoint.x = 0;
		}
		else if(nextPoint.y == map[0].length){
			nextPoint.y = 0;
		}
		
		return nextPoint;
	}
	
	private enum Direction{
		up, down, left, right, visited;
	}
	
	private static class Cycle{
		Point startingPoint;
		int length;
		
		public Cycle(Point startingPoint, int length){
			this.startingPoint = startingPoint;
			this.length = length;
		}
		
		public String toString(){
			return String.format("start: (%s, %s), length: %s", startingPoint.x, startingPoint.y, length);
		}
	}
}
