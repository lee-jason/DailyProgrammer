package _06_02_2014.gameoflife;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class InputReader {
	private FileReader fr;
	private BufferedReader br;
	
	private int generations = 0;
	private int width = 0;
	private int height = 0;
	
	private String chart[][];
	
	public InputReader(String inputFile){
		try {
			fr = new FileReader(inputFile);
			br = new BufferedReader(fr);
			
			try {
				String firstLine[] = br.readLine().split(" ");
				generations = Integer.parseInt(firstLine[0]);
				width = Integer.parseInt(firstLine[1]);
				height = Integer.parseInt(firstLine[2]);
				
				chart = new String[width][height];
				for(int y = 0; y < height; y++){
					String[] aLine = br.readLine().split("");
					for(int x = 0; x < width; x++){
						chart[y][x] = aLine[x+1];
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String[][] getChart(){
		return chart;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getGenerations(){
		return generations;
	}
}
