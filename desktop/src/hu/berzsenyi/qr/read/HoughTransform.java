package hu.berzsenyi.qr.read;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class HoughTransform {
	private int widthIn, heightIn, widthOut, heightOut;
	private boolean[][] pixels;
	private int maxStrength;
	private int[][] houghSpace;
	
	public void setData(int width, int height, boolean[][] pixels) {
		this.widthIn = width;
		this.heightIn = height;
		this.pixels = pixels;
	}
	
	public void transform() {
		this.widthOut = 360;
		this.heightOut = (int)Math.sqrt(this.widthIn*this.widthIn + this.heightIn*this.heightIn)*2;
		//this.heightOut = (this.widthIn+this.heightIn)*2;
		this.maxStrength = 0;
		this.houghSpace = new int[this.widthOut][this.heightOut];
		for(int sx = 0; sx < this.widthIn; sx++)
			for(int sy = 0; sy < this.heightIn; sy++)
				if(this.pixels[sx][sy])
					for(int hx = 0; hx < this.widthOut; hx++) {
						int hy = this.heightOut/2-1-(int)(sx*MathHelper.cos(hx) + sy*MathHelper.sin(hx));
						if(0 <= hy && hy < this.heightOut) {
							this.houghSpace[hx][hy]++;
							if(this.maxStrength < this.houghSpace[hx][hy])
								this.maxStrength = this.houghSpace[hx][hy];
						}
					}
	}
	
	public int[][] getOutput() {
		return this.houghSpace;
	}
	
	public BufferedImage getOutputAsImg() {
		BufferedImage ret = new BufferedImage(this.widthOut, this.heightOut, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < this.widthOut; x++)
			for(int y = 0; y < this.heightOut; y++) {
				int c = this.houghSpace[x][y]*255/this.maxStrength;
				ret.setRGB(x, y, new Color(c, c, c).getRGB());
			}
		return ret;
	}
}
