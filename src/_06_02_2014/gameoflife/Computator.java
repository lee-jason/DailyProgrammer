package _06_02_2014.gameoflife;

public class Computator {
	String[][] chart;
	String[][] newGenChart;
	int generations;
	int width;
	int height;
	
	public Computator(String[][] chart, int generations, int width, int height){
		this.chart = chart;
		this.generations = generations;
		this.width = width;
		this.height = height;
		
		System.out.println(toString());
		for(int i = 0; i < generations; i++){
			advanceGeneration();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(toString());
		}
	}
	
	private void advanceGeneration(){
		
		//neighbor 3 cells are on, main cell turns on
		//neighbor 2- cells are on, main cell turns off
		//neighbor 3+ cells are on, main cell turns off 
		newGenChart = new String[height][width];
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int onNeighbors = checkOnNeighbors(x, y);
				if(chart[y][x].equals("#")){
					if(onNeighbors < 2 || onNeighbors > 3){
						newGenChart[y][x] = ".";
					}
					else{
						newGenChart[y][x] = "#";
					}
				}
				else{
					if(onNeighbors == 3){
						newGenChart[y][x] = "#";
					}
					else{
						newGenChart[y][x] = ".";
					}
				}
			}
		}
		chart = newGenChart;
	}
	
	private int checkOnNeighbors(int x, int y){
		//all eight neighbors
		int totalOn = 0;
		int currX = 0;
		int currY = 0;
		
		for (int offsetX = -1; offsetX <= 1; offsetX++){
			for(int offsetY = -1; offsetY <= 1; offsetY++){
				if(offsetX == 0 && offsetY == 0){
					continue;
				}
				currX = x + offsetX;
				currY = y + offsetY;
				
				if(currX < 0){
					currX = width - 1;
				}
				else if(currX > width - 1){
					currX = 0;
				}
				if(currY < 0){
					currY = height - 1;
				}
				else if(currY > height - 1){
					currY = 0;
				}
				
				if(chart[currY][currX].equals("#")){
					totalOn++;
				}
			}
		}

		return totalOn;
	}
	
	public String[][] getGeneratedChart(){
		return chart;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				sb.append(chart[y][x]);
			}
			sb.append("\r\n");
		}
		
		return sb.toString();
	}
}
