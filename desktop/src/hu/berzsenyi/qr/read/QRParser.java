package hu.berzsenyi.qr.read;

import java.util.ArrayList;
import java.util.List;

public class QRParser {
	public int width, height;
	public boolean[][] bitmap;
	public List<Vector2F> finderPatterns, aligmentPatterns;
	
	private List<ShapeF> qrPositions;
	private List<QRBitmap> qrCodes;
	
//	public Vector2F findAligmentPattern(int cx, int cy, int radius) {
//		System.out.println("findAligmentPattern("+cx+", "+cy+", "+radius+")");
//		
//		int x1 = cx-radius;
//		int y1 = cy-radius;
//		int x2 = cx+radius;
//		int y2 = cy+radius;
//		
//		List<LineF> linesH = new ArrayList<LineF>();
//		for(int y = y1; y < y2; y++) {
//			int clength = 1;
//			boolean ppixel = false;
//			LinkedList<Integer> lengths = new LinkedList<Integer>();
//			for(int x = x1; x < x2; x++)
//				if(this.bitmap[x][y] == ppixel)
//					clength++;
//				else {
//					lengths.add(clength);
//					if(lengths.size() == 3) {
//						int patternSize = 0;
//						for(int i = 0; i < 3; i++)
//							patternSize += lengths.get(i);
//						if(MathHelper.equalRel(patternSize/3F, lengths.get(0), 0.25F)
//							&& MathHelper.equalRel(patternSize/3F, lengths.get(1), 0.25F)
//							&& MathHelper.equalRel(patternSize/3F, lengths.get(2), 0.25F))
//							linesH.add(new LineF(x-patternSize, y, x-1, y));
//						lengths.poll();
//					}
//					clength = 1;
//					ppixel = !ppixel;
//				}
//		}
//		
//		for(int h1 = 0; h1 < linesH.size()-1; h1++)
//			for(int h2 = h1+1; h2 < linesH.size(); h2++)
//				if(linesH.get(h1).v2.y+1 == linesH.get(h2).v1.y
//					&& MathHelper.equalRel(linesH.get(h1).v2.x-linesH.get(h1).v1.x, linesH.get(h2).v2.x-linesH.get(h2).v1.x, 0.25F)
//					&& MathHelper.equal(linesH.get(h1).getCenter().x, linesH.get(h2).getCenter().x, 3F)) {
//					LineF newLine = new LineF(Math.min(linesH.get(h1).v1.x, linesH.get(h2).v1.x), linesH.get(h1).v1.y, Math.max(linesH.get(h1).v2.x, linesH.get(h2).v2.x), linesH.get(h2).v2.y);
//					linesH.set(h1, newLine);
//					linesH.remove(h2--);
//				}
//		
//		Vector2F ret = null;
//		for(int h = 0; h < linesH.size(); h++)
//			if(MathHelper.equalRel((linesH.get(h).v2.x-linesH.get(h).v1.x)/3F, linesH.get(h).v2.y-linesH.get(h).v1.y, 0.5F)) {
//				int px = Math.round((linesH.get(h).v1.x + linesH.get(h).v2.x)/2F);
//				int py = Math.round((linesH.get(h).v1.y + linesH.get(h).v2.y)/2F);
//				int[] lengths = new int[4];
//				int i = 1;
//				int clength = 0;
//				boolean ppixel = this.bitmap[px][py];
//				for(int y = py-1; 0 <= i && 0 <= y; y--)
//					if(this.bitmap[px][y] == ppixel)
//						clength++;
//					else {
//						lengths[i--] = clength;
//						clength = 1;
//						ppixel = !ppixel;
//					}
//				i = 2;
//				clength = 0;
//				ppixel = this.bitmap[px][py];
//				for(int y = py+1; i < 4 && y < this.height; y++)
//					if(this.bitmap[px][y] == ppixel)
//						clength++;
//					else {
//						lengths[i++] = clength;
//						clength = 1;
//						ppixel = !ppixel;
//					}
//				int patternSize = 1;
//				for(i = 0; i < 4; i++)
//					patternSize += lengths[i];
////				linesV.add(new LineF(px, py-lengths[0]-lengths[1], px, py+lengths[2]+lengths[3]));
//				if(MathHelper.equalRel(patternSize, linesH.get(h).v2.x-linesH.get(h).v1.x, 0.25F)
//					&& MathHelper.equalRel(patternSize/3F, lengths[0], 0.5F)
//					&& MathHelper.equalRel(patternSize/3F, lengths[1], 0.5F)
//					&& MathHelper.equalRel(patternSize/3F, lengths[2], 0.5F)) {
//					Vector2F cret = new Vector2F(px, (py-lengths[0]-lengths[1] + py+lengths[2]+lengths[3])/2F);
//					if(ret == null || new LineF(cx, cy, cret.x, cret.y).getLengthSquare() < new LineF(cx, cy, ret.x, ret.y).getLengthSquare())
//						ret = cret;
//				}
//			}
//		if(ret == null && radius < (this.width+this.height)/10)
//			return findAligmentPattern(cx, cy, radius*2);
//		return ret;
//	}
	
	public List<QRBitmap> parse(int width, int height, boolean[][] bitmap, List<Vector2F> finderPatterns, List<Vector2F> aligmentPatterns) {
		this.width = width;
		this.height = height;
		this.bitmap = bitmap;
		this.finderPatterns = finderPatterns;
		this.aligmentPatterns = aligmentPatterns;
		this.qrCodes = new ArrayList<QRBitmap>();
		this.qrPositions = new ArrayList<ShapeF>();
		
		for(int i1 = 0; i1 < this.finderPatterns.size()-2; i1++)
			for(int i2 = i1+1; i2 < this.finderPatterns.size()-1; i2++)
				for(int i3 = i2+1; i3 < this.finderPatterns.size(); i3++) {
					System.out.println();
					System.out.println("Trying to read code...");
					try {
						int mi = 1;
						float max = new LineF(this.finderPatterns.get(i2), this.finderPatterns.get(i3)).getLengthSquare();
						float length = new LineF(this.finderPatterns.get(i1), this.finderPatterns.get(i3)).getLengthSquare();
						if(max < length) {
							mi = 2;
							max = length;
						}
						length = new LineF(this.finderPatterns.get(i1), this.finderPatterns.get(i2)).getLengthSquare();
						if(max < length) {
							mi = 3;
							max = length;
						}
						
						Vector2F patternTopLeft = null, patternBottomLeft = null, patternTopRight = null;
						
						switch(mi) {
						case 1:
							patternBottomLeft = this.finderPatterns.get(i3);
							patternTopLeft = this.finderPatterns.get(i1);
							patternTopRight = this.finderPatterns.get(i2);
							break;
						case 2:
							patternBottomLeft = this.finderPatterns.get(i1);
							patternTopLeft = this.finderPatterns.get(i2);
							patternTopRight = this.finderPatterns.get(i3);
							break;
						case 3:
							patternBottomLeft = this.finderPatterns.get(i1);
							patternTopLeft = this.finderPatterns.get(i3);
							patternTopRight = this.finderPatterns.get(i2);
							break;
						}
						
//						System.out.println("begin: "+patternTopLeft.pos.x+" "+patternTopLeft.pos.y);
						
						Vector2F one = Vector2F.sub(patternTopRight, patternTopLeft);
						one.scale(1F/one.getLength());
//						System.out.println("one: "+one.x+" "+one.y);
						Vector2F cpos = patternTopLeft.clone();
						int edge = 0;
						boolean ppixel = this.bitmap[Math.round(cpos.x)][Math.round(cpos.y)];
						while(edge < 3) {
							if(this.bitmap[Math.round(cpos.x)][Math.round(cpos.y)] != ppixel) {
								edge++;
								ppixel = !ppixel;
							}
							cpos.add(one);
						}
						cpos.sub(one);
						float moduleSizeX = new LineF(patternTopLeft, cpos).getLength()/3.5F;
						
//						this.qrPositions.add(new ShapeF(new Vector2F[]{patternTopLeft.pos, cpos.clone()}));
//						System.out.println("end: "+cpos.x+" "+cpos.y);
						
						one = Vector2F.sub(patternBottomLeft, patternTopLeft);
						one.scale(1F/one.getLength());
//						System.out.println("one: "+one.x+" "+one.y);
						cpos = patternTopLeft.clone();
						edge = 0;
						ppixel = this.bitmap[Math.round(cpos.x)][Math.round(cpos.y)];
						while(edge < 3) {
							if(this.bitmap[Math.round(cpos.x)][Math.round(cpos.y)] != ppixel) {
								edge++;
								ppixel = !ppixel;
							}
							cpos.add(one);
						}
						cpos.sub(one);
						float moduleSizeY = new LineF(patternTopLeft, cpos.clone()).getLength()/3.5F;
						
//						this.qrPositions.add(new ShapeF(new Vector2F[]{patternTopLeft.pos, cpos.clone()}));
//						System.out.println("end: "+cpos.x+" "+cpos.y);
						
						int sizeX = Math.round(new LineF(patternTopLeft, patternTopRight).getLength()/moduleSizeX)+7;
						int sizeY = Math.round(new LineF(patternTopLeft, patternBottomLeft).getLength()/moduleSizeY)+7;
						System.out.println("sizeX="+sizeX+" sizeY="+sizeY);
						int versionX = Math.round((sizeX-17)/4F);
						int versionY = Math.round((sizeY-17)/4F);
						System.out.println("versionX="+versionX+" versionY="+versionY);
//						versionX = (versionX+versionY)/2;
//						versionY = versionX;
						if(versionX == versionY) {
							int version = versionX;
							int size = version*4 + 17;
							if(7 <= version) {
								// TODO read version information
							}
							
							System.out.println("size="+size);
							
							Vector2F baseX1 = Vector2F.sub(patternTopRight, patternTopLeft).scale(size/(float)(size-7));
							Vector2F baseY1 = Vector2F.sub(patternBottomLeft, patternTopLeft).scale(size/(float)(size-7));
							
							Vector2F origin = Vector2F.add(patternTopLeft, Vector2F.linearComb(baseX1, baseY1, -3.5F/size, -3.5F/size));
							
//							Vector2F innerTopLeft = Vector2F.add(patternTopLeft, Vector2F.linearComb(baseX1, baseY1, 3F/size, 3F/size));
//							Vector2F innerTopRight = Vector2F.add(patternTopRight, Vector2F.linearComb(baseX1, baseY1, -3F/size, 3F/size));
							Vector2F innerBottomLeft = Vector2F.add(patternBottomLeft, Vector2F.linearComb(baseX1, baseY1, 3F/size, -3F/size));
							Vector2F innerBottomRight = Vector2F.add(origin, Vector2F.linearComb(baseX1, baseY1, (size-6.5F)/size, (size-6.5F)/size));
							
							System.out.println("innerBottomRight=("+innerBottomRight.x+", "+innerBottomRight.y+")");
							if(2 <= version) {
								Vector2F closest = null;
								for(Vector2F aligmentPattern : this.aligmentPatterns)
									if(closest == null || new LineF(innerBottomRight, aligmentPattern).getLengthSquare() < new LineF(innerBottomRight, closest).getLengthSquare())
										closest = aligmentPattern;
//								if(new LineF(innerBottomRight, closest).getLengthSquare() < 100F*100F) {
//									innerBottomRight = closest;
//									System.out.println("Found aligment pattern!");
//								}
								if(closest != null && new LineF(closest, innerBottomRight).getLengthSquare() < 50F*50F) {
									innerBottomRight = closest;
									System.out.println("Found aligment pattern!");
								}
							}
							System.out.println("innerBottomRight=("+innerBottomRight.x+", "+innerBottomRight.y+")");
							
							Vector2F baseX2 = Vector2F.sub(innerBottomRight, innerBottomLeft).scale(size/(float)(size-13));
//							Vector2F baseY2 = Vector2F.sub(innerBottomRight, innerTopRight).scale(size/(float)(size-13));
							
							Vector2F topLeft = origin.clone();
							Vector2F bottomLeft = Vector2F.add(topLeft, baseY1);
							Vector2F bottomRight = Vector2F.add(bottomLeft, baseX2);
							Vector2F topRight = Vector2F.add(topLeft, baseX1);
							
							QRBitmap qr = new QRBitmap(size);
							for(int x = 0; x < size; x++)
								for(int y = 0; y < size; y++) {
//									Vector2F baseX = Vector2F.linearComb(baseX1, baseX2, 1F-(y+0.5F)/size, (y+0.5F)/size);
//									Vector2F baseY = Vector2F.linearComb(baseY1, baseY2, 1F-(x+0.5F)/size, (x+0.5F)/size);
//									Vector2F pos = Vector2F.linearComb(baseX, baseY, (x+0.5F)/size, (y+0.5F)/size).add(origin);
									Vector2F pos = Vector2F.linearComb(Vector2F.linearComb(topLeft, topRight, 1F-(x+0.5F)/size, (x+0.5F)/size), Vector2F.linearComb(bottomLeft, bottomRight, 1F-(x+0.5F)/size, (x+0.5F)/size), 1F-(y+0.5F)/size, (y+0.5F)/size);
									qr.bitmap[x][y] = !this.bitmap[Math.round(pos.x)][Math.round(pos.y)];
									qr.from.add(pos.clone());
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
							ok = true;
							if(ok) {
								System.out.println("Found a "+size+"x"+size+" qr code!");
//								this.qrPositions.add(new ShapeF(new Vector2F[]{innerTopLeft, innerBottomLeft, innerBottomRight, innerTopRight}));
								this.qrPositions.add(new ShapeF(new Vector2F[]{origin, origin.clone().add(baseY1), origin.clone().add(baseY1).add(baseX2), origin.clone().add(baseX1)}));
								this.qrCodes.add(qr);
							}
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
					System.out.println("End.");
					System.out.println();
				}
		
		return this.qrCodes;
	}
	
	public List<QRBitmap> getCodes() {
		return this.qrCodes;
	}
	
	public List<ShapeF> getPositions() {
		return this.qrPositions;
	}
}