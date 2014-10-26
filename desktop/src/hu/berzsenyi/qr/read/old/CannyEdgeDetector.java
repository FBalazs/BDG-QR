package hu.berzsenyi.qr.read.old;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class CannyEdgeDetector {
	private int width, height;
	private int[] pixels;
	private float gdtMaxStrength;
	private float[][] gdtStrength;
	private int[][] gdtDir;
	private boolean[][] edges;
	private int gausRadius;
	private float gausThreshold;
	private float[][] gausMask;
	private float thresholdLow, thresholdHigh;
	
	public CannyEdgeDetector() {
		this(2, 1F, 150F, 250F);
	}
	
	public CannyEdgeDetector(int gausR, float gausT, float thresholdLow, float thresholdHigh) {
		this.gausRadius = gausR;
		this.gausThreshold = gausT;
		this.thresholdLow = thresholdLow;
		this.thresholdHigh = thresholdHigh;
	}

	public void setData(int width, int height, int[] pixels) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}

	void gausSmoothing() {
		this.gausMask = new float[this.gausRadius * 2 + 1][this.gausRadius * 2 + 1];
		float sum = 0F;
		for (int x = -this.gausRadius; x <= this.gausRadius; x++)
			for (int y = -this.gausRadius; y <= this.gausRadius; y++) {
				this.gausMask[x + this.gausRadius][y + this.gausRadius] = (float)(Math.exp(-(x*x + y*y)/(2*this.gausThreshold*this.gausThreshold))/(2*Math.PI*this.gausThreshold*this.gausThreshold));
				sum += this.gausMask[x+this.gausRadius][y+this.gausRadius];
			}
		for (int x = -this.gausRadius; x <= this.gausRadius; x++)
			for (int y = -this.gausRadius; y <= this.gausRadius; y++)
				this.gausMask[x + this.gausRadius][y + this.gausRadius] /= sum;
		
		int[] ppixels = new int[this.pixels.length];
		for(int i = 0; i < ppixels.length; i++)
			ppixels[i] = this.pixels[i];
		
		for(int x = this.gausRadius; x < this.width-this.gausRadius; x++)
			for(int y = this.gausRadius; y < this.height-this.gausRadius; y++) {
				sum = 0F;
				for(int ox = -this.gausRadius; ox <= this.gausRadius; ox++)
					for(int oy = -this.gausRadius; oy <= this.gausRadius; oy++)
						sum += this.gausMask[ox+this.gausRadius][oy+this.gausRadius]*ppixels[(y+oy)*this.width + x+ox];
				this.pixels[y*this.width + x] = (int)sum;
			}
	}
	
	int[][] sobelMaskX = new int[3][3];
	int[][] sobelMaskY = new int[3][3];
	
	void sobelMasking() {
		this.sobelMaskX[0][0] = -1; this.sobelMaskX[1][0] = 0; this.sobelMaskX[2][0] = 1;
		this.sobelMaskX[0][1] = -2; this.sobelMaskX[1][1] = 0; this.sobelMaskX[2][1] = 2;
		this.sobelMaskX[0][2] = -1; this.sobelMaskX[1][2] = 0; this.sobelMaskX[2][2] = 1;
		this.sobelMaskY[0][0] = -1; this.sobelMaskY[1][0] = -2; this.sobelMaskY[2][0] = -1;
		this.sobelMaskY[0][1] = 0; this.sobelMaskY[1][1] = 0; this.sobelMaskY[2][1] = 0;
		this.sobelMaskY[0][2] = 1; this.sobelMaskY[1][2] = 2; this.sobelMaskY[2][2] = 1;
		
		this.gdtMaxStrength = 0;
		this.gdtStrength = new float[this.width][this.height];
		this.gdtDir = new int[this.width][this.height];
		
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++)
				if(0 < x && x < this.width-1 && 0 < y && y < this.height-1) {
					int gx = 0, gy = 0;
					for(int ox = -1; ox <= 1; ox++)
						for(int oy = -1; oy <= 1; oy++) {
							gx += this.sobelMaskX[ox+1][oy+1]*this.pixels[(y+oy)*this.width + x+ox];
							gy += this.sobelMaskY[ox+1][oy+1]*this.pixels[(y+oy)*this.width + x+ox];
						}
					this.gdtStrength[x][y] = (float)Math.sqrt(gx*gx + gy*gy);
					if(this.gdtMaxStrength < this.gdtStrength[x][y])
						this.gdtMaxStrength = this.gdtStrength[x][y];
					float deg = MathHelper.atan(gy/(float)gx);
					if(-90F <= deg && deg < -67.5F)
						this.gdtDir[x][y] = 90;
					else if(-67.5F < deg && deg <= -22.5F)
						this.gdtDir[x][y] = 135;
					else if(-22.5F < deg && deg <= 22.5F)
						this.gdtDir[x][y] = 0;
					else if(22.5F < deg && deg <= 67.5F)
						this.gdtDir[x][y] = 45;
					else if(67.5F < deg && deg <= 90F)
						this.gdtDir[x][y] = 90;
					else
						this.gdtDir[x][y] = -1;
				} else {
					this.gdtStrength[x][y] = 0F;
					this.gdtDir[x][y] = -1;
				}
	}
	
	void parseEdge(int x, int y) {
		int dx = 0, dy = 0;
		if(this.gdtDir[x][y] == 0)
			dx = 1;
		else if(this.gdtDir[x][y] == 45)
			dx = dy = 1;
		else if(this.gdtDir[x][y] == 90)
			dy = 1;
		else if(this.gdtDir[x][y] == 135) {
			dx = -1;
			dy = 1;
		}
		int nx = x+dx, ny = y+dy;
		while(0 <= nx && nx < this.width && 0 <= ny && ny < this.height && this.gdtDir[x][y] == this.gdtDir[nx][ny] && this.thresholdLow <= this.gdtStrength[nx][ny]) {
			this.edges[nx][ny] = true;
			nx += dx;
			ny += dy;
		}
	}
	
	void parseEdges() {
		this.edges = new boolean[this.width][this.height];
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++)
				if(this.thresholdHigh <= this.gdtStrength[x][y] && this.gdtDir[x][y] != -1) {
					this.edges[x][y] = true;
					this.parseEdge(x, y);
				}
	}
	
	public BufferedImage getBlured() {
		BufferedImage ret = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++)
				ret.setRGB(x, y, (this.pixels[y*this.width + x] << 16) + (this.pixels[y*this.width + x] << 8) + this.pixels[y*this.width + x]);
		return ret;
	}
	
	public BufferedImage getSobelMasked() {
		BufferedImage ret = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++)
				if(this.gdtDir[x][y] == 0)
					ret.setRGB(x, y, new Color((float)(this.gdtStrength[x][y]/this.gdtMaxStrength), 0F, 0F).getRGB());
				else if(this.gdtDir[x][y] == 45)
					ret.setRGB(x, y, new Color(0F, (float)(this.gdtStrength[x][y]/this.gdtMaxStrength), 0F).getRGB());
				else if(this.gdtDir[x][y] == 90)
					ret.setRGB(x, y, new Color(0F, 0F, (float)(this.gdtStrength[x][y]/this.gdtMaxStrength)).getRGB());
				else if(this.gdtDir[x][y] == 135)
					ret.setRGB(x, y, new Color((float)(this.gdtStrength[x][y]/this.gdtMaxStrength), (float)(this.gdtStrength[x][y]/this.gdtMaxStrength), 0F).getRGB());
				else
					ret.setRGB(x, y, Color.black.getRGB());
		return ret;
	}
	
	public BufferedImage getTraced() {
		BufferedImage ret = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++)
				if(this.edges[x][y])
					ret.setRGB(x, y, Color.white.getRGB());
				else
					ret.setRGB(x, y, Color.black.getRGB());
		return ret;
	}

	public void process() {
		this.gausSmoothing();
		this.sobelMasking();
		this.parseEdges();
	}

	public boolean[][] getOutput() {
		return this.edges;
	}
}
