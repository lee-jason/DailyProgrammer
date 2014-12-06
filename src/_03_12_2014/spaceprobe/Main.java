package _03_12_2014.spaceprobe;

import java.util.Random;

public class Main {

	public static void main(String[] args) {
		int dimension = 200;
		Map map = new Map(dimension, new Random());

		System.out.println(map.toString());
	}

}
