package _03_12_2014.spaceprobe;

import java.awt.Point;
import java.util.Random;

public class Main {

	public static void main(String[] args) {
		int dimension = 6;
		Point start = new Point(0, 0);
		Point end = new Point(5, 5);
		Map map = new Map(dimension, start, end, new Random());

		System.out.println(map.toString());
		
		map.findShortestPath();
	}

}
