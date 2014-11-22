package _06_04_2014.asciimazemaster;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		MazeReader mr = null;
		try {
			mr = new MazeReader("src/_06_04_2014/asciimazemaster/input.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MazeGrapher mg = new MazeGrapher(mr.getMaze());	
		System.out.println(mg.toString());
	}

}
