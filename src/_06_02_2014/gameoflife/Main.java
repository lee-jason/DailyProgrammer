package _06_02_2014.gameoflife;

public class Main {
	public static void main(String[] args) {
		InputReader ir = new InputReader("src/_06_02_2014/gameoflife/input.txt");
		new Computator(ir.getChart(), ir.getGenerations(), ir.getWidth(), ir.getHeight());
	}
}
