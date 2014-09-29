package hu.berzsenyi.qr.read;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

public class QRParser {
	int width, height;
	int[] pixels;
	boolean[][] bitmap;
	List<FinderPattern> finderPatterns;
	int qrSize;
	boolean[][] qr;
	boolean[] examined;
	
	public void setData(int width, int height, int[] pixels, boolean[][] bitmap, List<FinderPattern> finderPatterns) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
		this.bitmap = bitmap;
		this.finderPatterns = finderPatterns;
	}
	
	/**
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param n - the number of the last edge
	 * @return
	 */
	public Coords rayTrace(float x1, float y1, float x2, float y2, int n) {
		float x = x1;
		float y = y1;
		float dx = x2-x1;
		float dy = y2-y1;
		float div = Math.max(Math.abs(dx), Math.abs(dy));
		dx /= div;
		dy /= div;
		int e = 0;
		float sx = 0F, sy = 0F;
		boolean pedge = false;
		while(e < n && Math.abs(x-x1) <= Math.abs(x2-x1)) {
			if(this.bitmap[(int)x][(int)y]) {
				if(!pedge) {
					sx = x;
					sy = y;
				}
				pedge = true;
			} else {
				if(pedge) {
					e++;
					if(e == n)
						return new Coords((x+sx)/2, (y+sy)/2);
				}
				pedge = false;
			}
			x += dx;
			y += dy;
		}
		return null;
	}
	
	public int countEdges(float x1, float y1, float x2, float y2) {
		System.out.println("countEdges("+x1+", "+y1+", "+x2+", "+y2+")");
		float x = x1;
		float y = y1;
		float dx = x2-x1;
		float dy = y2-y1;
		float div = Math.max(Math.abs(dx), Math.abs(dy));
		dx /= div;
		dy /= div;
		int ret = 0;
		boolean pedge = false;
		while(Math.abs(x-x1) <= Math.abs(x2-x1) && Math.abs(y-y1) <= Math.abs(y2-y1)) {
			if(this.bitmap[(int)x][(int)y]) {
				if(!pedge)
					ret++;
				pedge = true;
			} else
				pedge = false;
			x += dx;
			y += dy;
		}
		return ret;
	}
	
	public void parse() {
		for(int i1 = 0; i1 < this.finderPatterns.size(); i1++)
			for(int i2 = 0; i2 < this.finderPatterns.size(); i2++)
				for(int i3 = 0; i3 < this.finderPatterns.size(); i3++)
					if(i1 != i2 && i2 != i3 && i1 != i3 && i1 == 1 && i2 == 0 && i3 == 2) {
						FinderPattern f1 = this.finderPatterns.get(i1);
						FinderPattern f2 = this.finderPatterns.get(i2);
						FinderPattern f3 = this.finderPatterns.get(i3);
						
						System.out.println();
						System.out.println("finder patterns:");
						System.out.println(i2+"("+f2.x+", "+f2.y+") "+i3+"("+f3.x+", "+f3.y+")");
						System.out.println(i1+"("+f1.x+", "+f1.y+")");
						
						try {
							Coords e2 = this.rayTrace(f2.x, f2.y, f3.x, f3.y, 3);
							Coords e1 = this.rayTrace(f2.x, f2.y, f2.x+(f2.x-f3.x), f2.y+(f2.y-f3.y), 3);
							Coords oneX = new Coords((e2.x-e1.x)/7F, (e2.y-e1.y)/7F);
							e1 = this.rayTrace(f1.x, f1.y, f2.x, f2.y, 3);
							e2 = this.rayTrace(f1.x, f1.y, f1.x+(f1.x-f2.x), f1.y+(f1.y-f2.y), 3);
							Coords oneY = new Coords((e2.x-e1.x)/7F, (e2.y-e1.y)/7F);
							
							this.qrSize = this.countEdges(f2.x+oneX.x*3F+oneY.x*3F, f2.y+oneX.y*3F+oneY.y*3F, f3.x-oneX.x*3F-oneY.x*3F, f3.y+oneX.y*3F+oneY.y*3F)-1 + 14;
							//System.out.println(this.qrSize + " " + (this.countEdges(f2.x+oneX.x*3F+oneY.x*3F, f2.y+oneX.y*3F+oneY.y*3F, f1.x+oneX.x*3F+oneY.x*3F, f1.y-oneX.y*3F-oneY.y*3F)-1 + 14));
							if(this.qrSize == this.countEdges(f2.x+oneX.x*3F+oneY.x*3F, f2.y+oneX.y*3F+oneY.y*3F, f1.x+oneX.x*3F+oneY.x*3F, f1.y-oneX.y*3F-oneY.y*3F)-1 + 14) {
								System.out.println("Size match!");
								this.qr = new boolean[this.qrSize][this.qrSize];
								float ox = f2.x-oneX.x*3F-oneY.x*3F;
								float oy = f2.y-oneX.y*3F-oneY.y*3F;
								float x = ox;
								float y = oy;
								this.examined = new boolean[this.width*this.height];
								for(int j = 0; j < this.qrSize; j++) {
									for(int i = 0; i < this.qrSize; i++) {
										int index = ((int)y)*this.width+(int)x;
										this.examined[index] = true;;
										this.qr[i][j] = this.pixels[index] > 127;
										x += oneX.x;
										y += oneX.y;
									}
									x -= oneX.x*this.qrSize;
									y -= oneX.y*this.qrSize;
									x += oneY.x;
									y += oneY.y;
								}
								return;
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
	}
	
	public BufferedImage getOutputAsImg() {
		if(this.qr == null)
			return null;
		BufferedImage ret = new BufferedImage(this.qrSize, this.qrSize, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < this.qrSize; i++)
			for(int j = 0; j < this.qrSize; j++)
				ret.setRGB(i, j, this.qr[i][j] ? Color.white.getRGB() : Color.black.getRGB());
		return ret;
	}
	
	public int getOutputSize() {
		return this.qrSize;
	}
	
	public boolean[][] getOutput() {
		return this.qr;
	}
}
