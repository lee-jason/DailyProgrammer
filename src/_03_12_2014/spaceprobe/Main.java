package _03_12_2014.spaceprobe;

import java.awt.Point;
import java.util.Random;

public class Main {

	public static void main(String[] args) {
		int dimension = 20;
		Point start = new Point(0, 0);
		Point end = new Point(19, 19);
		Map map = new Map(dimension, start, end, new Random());

		System.out.println("starting map");
		System.out.println(map.toString());
		
		map.findShortestPath();
		System.out.println("pathed map");
		System.out.println(map.toString());
	}

}
