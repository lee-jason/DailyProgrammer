package _06_04_2014.asciimazemaster;

import java.util.LinkedList;

public class MazeGrapher {
	char[][] maze;
	Coordinate[][] coordHistory;
	Coordinate start = null;
	Coordinate end = null;
	public MazeGrapher(char[][] maze){
		this.maze = maze;
		coordHistory = new Coordinate[maze.length][maze[0].length];
		
		for(int y = 0; y < maze.length; y++){
			for(int x = 0; x < maze[0].length; x++){
				if(maze[y][x] == 'S'){
					start = new Coordinate(x, y, null);
					break;
				}
			}
		}
		
		BFSIt();
	}
	
	private void BFSIt(){
		end = recursiveTraverse(start, new LinkedList<Coordinate>());
		backtrackPath(end);
	}
	
	private Coordinate recursiveTraverse(Coordinate currentCoord, LinkedList<Coordinate> coordQueue){
		Coordinate tempCoord;
		//once we reach the end. travel backwards along the path and mark the road along the way
		if(maze[currentCoord.y][currentCoord.x] == 'E'){
			return currentCoord;
		}
		//check north only if we didn't already come from there
		if((maze[currentCoord.y - 1][currentCoord.x] == ' ' || maze[currentCoord.y - 1][currentCoord.x] == 'E') && currentCoord.dir != Direction.North){
			tempCoord = new Coordinate(currentCoord.x, currentCoord.y - 1, Direction.South);
			coordQueue.add(tempCoord);
			coordHistory[currentCoord.y - 1][currentCoord.x] = currentCoord;
		}
		//east
		if((maze[currentCoord.y][currentCoord.x + 1] == ' ' || maze[currentCoord.y][currentCoord.x + 1] == 'E') && currentCoord.dir != Direction.East){
			tempCoord = new Coordinate(currentCoord.x + 1, currentCoord.y, Direction.West);
			coordQueue.add(tempCoord);
			coordHistory[currentCoord.y][currentCoord.x + 1] = currentCoord;
		}
		//south
		if((maze[currentCoord.y + 1][currentCoord.x] == ' ' || maze[currentCoord.y + 1][currentCoord.x] == 'E') && currentCoord.dir != Direction.South){
			tempCoord = new Coordinate(currentCoord.x, currentCoord.y + 1, Direction.North);
			coordQueue.add(tempCoord);
			coordHistory[currentCoord.y + 1][currentCoord.x] = currentCoord;
		}
		//west
		if((maze[currentCoord.y][currentCoord.x - 1] == ' ' || maze[currentCoord.y][currentCoord.x - 1] == 'E') && currentCoord.dir != Direction.West){
			tempCoord = new Coordinate(currentCoord.x - 1, currentCoord.y, Direction.East);
			coordQueue.add(tempCoord);
			coordHistory[currentCoord.y][currentCoord.x - 1] = currentCoord;
		}
		
		return recursiveTraverse(coordQueue.poll(), coordQueue);
	}
	
	private void backtrackPath(Coordinate coord){
		if(coord.compareTo(start) == 0){
			maze[coord.y][coord.x] = 'S'; 
			return;
		}
		
		Coordinate prevCoord = coordHistory[coord.y][coord.x];
		maze[prevCoord.y][prevCoord.x] = '*';
		
		backtrackPath(prevCoord);
		
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < maze.length; i++){
			sb.append(maze[i]);
			sb.append("\r\n");
		}
		return sb.toString();
	}
	
	private class Coordinate implements Comparable<Coordinate>{
		public int x;
		public int y;
		public Direction dir;
		public Coordinate(int x, int y, Direction dir){
			this.x = x;
			this.y = y;
			this.dir = dir;
		}

		@Override
		public int compareTo(Coordinate o) {
			if(this.x == o.x && this.y == o.y){
				return 0;
			}
			else{
				return 1;
			}
		}
	}
}
