package hu.berzsenyi.qr.read;

import java.util.ArrayList;
import java.util.List;

public class QRParser {
	int width, height;
	int[] pixels;
	boolean[][] bitmap;
	List<FinderPattern> finderPatterns;
	boolean[] examined;
	List<QRBitmap> qrCodes;
	
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
//		System.out.println("countEdges("+x1+", "+y1+", "+x2+", "+y2+")");
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
		this.qrCodes = new ArrayList<QRBitmap>();
		for(int i1 = 0; i1 < this.finderPatterns.size(); i1++)
			for(int i2 = 0; i2 < this.finderPatterns.size(); i2++)
				for(int i3 = 0; i3 < this.finderPatterns.size(); i3++)
					if(i1 != i2 && i2 != i3 && i1 != i3) {
						FinderPattern f1bottomLeft = this.finderPatterns.get(i1);
						FinderPattern f2topLeft = this.finderPatterns.get(i2);
						FinderPattern f3topRight = this.finderPatterns.get(i3);
						
//						System.out.println();
//						System.out.println("finder patterns:");
//						System.out.println(i2+"("+f2.x+", "+f2.y+") "+i3+"("+f3.x+", "+f3.y+")");
//						System.out.println(i1+"("+f1.x+", "+f1.y+")");
						
						try {
							Coords baseX = new Coords(f3topRight.x-f2topLeft.x, f3topRight.y-f2topLeft.y);
							Coords baseY = new Coords(f1bottomLeft.x-f2topLeft.x, f1bottomLeft.y-f2topLeft.y);
							
							Coords e1 = this.rayTrace(f1bottomLeft.x, f1bottomLeft.y, f1bottomLeft.x-baseX.x, f1bottomLeft.y-baseX.y, 3);
							Coords e2 = this.rayTrace(f1bottomLeft.x, f1bottomLeft.y, f1bottomLeft.x+baseX.x, f1bottomLeft.y+baseX.y, 3);
							Coords oneX1 = new Coords((e2.x-e1.x)/7F, (e2.y-e1.y)/7F);
							e1 = this.rayTrace(f1bottomLeft.x, f1bottomLeft.y, f1bottomLeft.x-baseY.x, f1bottomLeft.y-baseY.y, 3);
							e2 = this.rayTrace(f1bottomLeft.x, f1bottomLeft.y, f1bottomLeft.x+baseY.x, f1bottomLeft.y+baseY.y, 3);
							Coords oneY1 = new Coords((e2.x-e1.x)/7F, (e2.y-e1.y)/7F);
							
							e1 = this.rayTrace(f2topLeft.x, f2topLeft.y, f2topLeft.x-baseX.x, f2topLeft.y-baseX.y, 3);
							e2 = this.rayTrace(f2topLeft.x, f2topLeft.y, f2topLeft.x+baseX.x, f2topLeft.y+baseX.y, 3);
							Coords oneX2 = new Coords((e2.x-e1.x)/7F, (e2.y-e1.y)/7F);
							e1 = this.rayTrace(f2topLeft.x, f2topLeft.y, f2topLeft.x-baseY.x, f2topLeft.y-baseY.y, 3);
							e2 = this.rayTrace(f2topLeft.x, f2topLeft.y, f2topLeft.x+baseY.x, f2topLeft.y+baseY.y, 3);
							Coords oneY2 = new Coords((e2.x-e1.x)/7F, (e2.y-e1.y)/7F);
							
							e1 = this.rayTrace(f3topRight.x, f3topRight.y, f3topRight.x-baseX.x, f3topRight.y-baseX.y, 3);
							e2 = this.rayTrace(f3topRight.x, f3topRight.y, f3topRight.x+baseX.x, f3topRight.y+baseX.y, 3);
							Coords oneX3 = new Coords((e2.x-e1.x)/7F, (e2.y-e1.y)/7F);
							e1 = this.rayTrace(f3topRight.x, f3topRight.y, f3topRight.x-baseY.x, f3topRight.y-baseY.y, 3);
							e2 = this.rayTrace(f3topRight.x, f3topRight.y, f3topRight.x+baseY.x, f3topRight.y+baseY.y, 3);
							Coords oneY3 = new Coords((e2.x-e1.x)/7F, (e2.y-e1.y)/7F);
							
//							System.out.println("1: ("+oneX1.x+", "+oneX1.y+") ("+oneY1.x+", "+oneY1.y+")");
//							System.out.println("2: ("+oneX2.x+", "+oneX2.y+") ("+oneY2.x+", "+oneY2.y+")");
//							System.out.println("3: ("+oneX3.x+", "+oneX3.y+") ("+oneY3.x+", "+oneY3.y+")");
							
							int s = this.countEdges(f2topLeft.x+oneX2.x*4F+oneY2.x*3F, f2topLeft.y+oneX2.y*4F+oneY2.y*3F, f3topRight.x-oneX3.x*4F+oneY3.x*3F, f3topRight.y-oneX3.y*4F+oneY3.y*3F) + 15;
//							System.out.println(s + " " + (this.countEdges(f2topLeft.x+oneX2.x*3F+oneY2.x*4F, f2topLeft.y+oneX2.y*3F+oneY2.y*4F, f1bottomLeft.x+oneX1.x*3F-oneY1.x*4F, f1bottomLeft.y+oneX1.y*3F-oneY1.y*4F) + 14));
							if((s-17)%4 == 0 && s == this.countEdges(f2topLeft.x+oneX2.x*3F+oneY2.x*4F, f2topLeft.y+oneX2.y*3F+oneY2.y*4F, f1bottomLeft.x+oneX1.x*3F-oneY1.x*4F, f1bottomLeft.y+oneX1.y*3F-oneY1.y*4F) + 15) {
								QRBitmap qr = new QRBitmap(s);
								
								Coords origo = new Coords(f2topLeft.x-oneX2.x*3F-oneY2.x*3F, f2topLeft.y-oneX2.y*3F-oneY2.y*3F);
//								Coords topLeftLeftSide = new Coords(f2.x-oneX.x*3F+oneY.x*3F - origo.x, f2.y-oneX.y*3F+oneY.y*3F - origo.y);
//								Coords topLeftTopSide = new Coords(f2.x+oneX.x*3F+oneY.x - origo.x, f2.x+oneX.x*3F+oneY.x - origo.y);
								baseX = new Coords(f3topRight.x+oneX3.x*3F-oneY3.x*3F - origo.x, f3topRight.y+oneX3.y*3F-oneY3.y*3F - origo.y);
								baseY = new Coords(f1bottomLeft.x-oneX1.x*3F+oneY1.x*3F - origo.x, f1bottomLeft.y-oneX1.y*3F+oneY1.y*3F - origo.y);
//								System.out.println("deg1: "+(int)Math.abs(MathHelper.atan(topLeftLeftSide.y/topLeftLeftSide.x)-MathHelper.atan(baseY.y/baseY.x)));
//								System.out.println("deg2: "+(int)Math.abs(MathHelper.atan(topLeftTopSide.y/topLeftTopSide.x)-MathHelper.atan(baseY.y/baseY.x)));
								
								//if(Math.abs(MathHelper.atan(topLeftLeftSide.y/topLeftLeftSide.x)-MathHelper.atan(baseY.y/baseY.x)) < 30F
									//&& Math.abs(MathHelper.atan(topLeftTopSide.y/topLeftTopSide.x)-MathHelper.atan(baseY.y/baseY.x)) < 30F) {
									System.out.println("Found QR code! It's size is "+qr.size+".");
//									System.out.println("topLeft: "+f2topLeft.x+" "+f2topLeft.y);
//									System.out.println("topRight: "+f3topRight.x+" "+f3topRight.y);
//									System.out.println("bottomLeft: "+f1bottomLeft.x+" "+f1bottomLeft.y);
									this.examined = new boolean[this.width*this.height];
									for(int i = 0; i < qr.size; i++)
										for(int j = 0; j < qr.size; j++) {
											int index = ((int)(origo.y+baseX.y*i/(qr.size-1)+baseY.y*j/(qr.size-1)))*this.width+(int)(origo.x+baseX.x*i/(qr.size-1)+baseY.x*j/(qr.size-1));
											this.examined[index] = true;
											qr.bitmap[i][j] = this.pixels[index] < 100;
										}
									boolean ok = true;
									for(int i = 0; i < 7; i++)
										if(!qr.bitmap[i][0] || !qr.bitmap[0][i] || !qr.bitmap[6][i] || !qr.bitmap[i][6])
											ok = false;
									for(int i = 0; i < 5; i++)
										if(qr.bitmap[1+i][1] || qr.bitmap[1][1+i] || qr.bitmap[5][1+i] || qr.bitmap[1+i][5])
											ok = false;
									for(int i = 2; i <= 4; i++)
										for(int j = 2; j <= 4; j++)
											if(!qr.bitmap[i][j])
												ok = false;
//									this.qrCodes.add(qr);
									if(ok) {
										System.out.println("The code is ok!");
										this.qrCodes.add(qr);
										//return;
									} else {
										//this.qrCodes.remove(this.qrCodes.size()-1);
									}
//								}
							}
						} catch(Exception e) {
							e.printStackTrace();
							System.out.println("HANDLED ERROR!");
						}
					}
	}
	
	public List<QRBitmap> getOutput() {
		return this.qrCodes;
	}
}
