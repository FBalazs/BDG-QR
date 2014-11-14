package hu.berzsenyi.qr.read;

public class EdgeDetector {
	private static final int[][] sobelMaskX = new int[][]{{-1, 0, 1},
														{-2, 0, 2},
														{-1, 0, 1}},
								sobelMaskY = new int[][]{{-1, -2, -1},
														{0, 0, 0},
														{1, 2, 1}};
	
	private int width, height;
	private int[] pixels;
	private int[][] gradientSize;
	private int maxGradient;
	private boolean[][] edgeMap;
	
	public boolean[][] detect(int width, int height, int[] pixels) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
		
		long gradientAvg = 0;
		maxGradient = 0;
		this.gradientSize = new int[this.width][this.height];
		for(int x = 1; x < this.width-1; x++)
			for(int y = 1; y < this.height-1; y++) {
				int gx = 0, gy = 0;
				for(int cx = -1; cx <= 1; cx++)
					for(int cy = -1; cy <= 1; cy++) {
						gx += this.pixels[(y+cy)*this.width + x+cx]*sobelMaskX[cx+1][cy+1];
						gy += this.pixels[(y+cy)*this.width + x+cx]*sobelMaskY[cx+1][cy+1];
					}
				this.gradientSize[x][y] = gx*gx + gy*gy;
				gradientAvg += this.gradientSize[x][y];
				if(this.maxGradient < this.gradientSize[x][y])
					this.maxGradient = this.gradientSize[x][y];
			}
		gradientAvg /= this.width*this.height*3;
		
		this.edgeMap = new boolean[this.width][this.height];
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++) {
				this.edgeMap[x][y] = gradientAvg < this.gradientSize[x][y];
			}
		
		return this.edgeMap;
	}
	
	public boolean[][] getEdgeMap() {
		return this.edgeMap;
	}
	
	public int[][] getGradients() {
		return this.gradientSize;
	}
	
	public int getMaxGradient() {
		return this.maxGradient;
	}
}
