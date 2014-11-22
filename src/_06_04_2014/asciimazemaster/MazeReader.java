package _06_04_2014.asciimazemaster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MazeReader {
	int width;
	int height;
	char[][] maze;
	public MazeReader(String input) throws IOException{
		FileReader fb = new FileReader(input);
		BufferedReader br = new BufferedReader(fb);
		
		String firstLine = br.readLine();
		String[] splitFirstLine = firstLine.split("\\s");
		width = Integer.parseInt(splitFirstLine[0]);
		height = Integer.parseInt(splitFirstLine[1]);
		
		maze = new char[height][width];
		for(int i = 0; i < height; i++){
			maze[i] = br.readLine().toCharArray();
		}
		br.close();
	}
	
	public char[][] getMaze(){
		return maze;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getWidth(){
		return width;
	}
}
