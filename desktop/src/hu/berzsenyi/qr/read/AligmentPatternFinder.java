package hu.berzsenyi.qr.read;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AligmentPatternFinder {
	public int width, height;
	public boolean[][] bitmap;
	
	public float range, range2, range3;
	
	private List<Vector2F> patterns;
	private List<LineF> linesH, linesV;
	
	public AligmentPatternFinder(float range, float range2, float range3) {
		this.range = range;
		this.range2 = range2;
		this.range3 = range3;
	}
	
	public List<Vector2F> findPatterns(int width, int height, boolean[][] bitmap) {
		this.width = width;
		this.height = height;
		this.bitmap = bitmap;
		
		this.linesH = new ArrayList<LineF>();
		for(int y = 0; y < this.height; y++) {
			LinkedList<Integer> lengths = new LinkedList<Integer>();
			int clength = 1;
			boolean ppixel = false;
			for(int x = 0; x < this.width; x++)
				if(this.bitmap[x][y] == ppixel)
					clength++;
				else {
					lengths.add(clength);
					clength = 1;
					if(lengths.size() == 3) {
						int patternSize = 0;
						for(int i = 0; i < 3; i++)
							patternSize += lengths.get(i);
						if(MathHelper.equalRel(patternSize/3F, lengths.get(0), this.range)
							&& MathHelper.equalRel(patternSize/3F, lengths.get(1), this.range)
							&& MathHelper.equalRel(patternSize/3F, lengths.get(2), this.range))
							this.linesH.add(new LineF(x-patternSize, y, x-1, y));
						lengths.poll();
					}
					ppixel = !ppixel;
				}
		}
		
		for(int h1 = 0; h1 < this.linesH.size()-1; h1++)
			for(int h2 = h1+1; h2 < this.linesH.size(); h2++)
				if(this.linesH.get(h1).v2.y+1 == this.linesH.get(h2).v1.y
					&& MathHelper.equalRel(this.linesH.get(h1).v2.x-this.linesH.get(h1).v1.x, this.linesH.get(h2).v2.x-this.linesH.get(h2).v1.x, this.range2)
					&& MathHelper.equal(this.linesH.get(h1).getCenter().x, this.linesH.get(h2).getCenter().x, 3F)) {
					LineF newLine = new LineF(Math.min(this.linesH.get(h1).v1.x, this.linesH.get(h2).v1.x), this.linesH.get(h1).v1.y, Math.max(this.linesH.get(h1).v2.x, this.linesH.get(h2).v2.x), this.linesH.get(h2).v2.y);
//					float width1 = this.patternLinesH.get(h1).v2.y-this.patternLinesH.get(h1).v1.y+1;
//					float width2 = this.patternLinesH.get(h2).v2.y-this.patternLinesH.get(h2).v1.y+1;
//					LineF newLine = new LineF((this.patternLinesH.get(h1).v1.x*width1 + this.patternLinesH.get(h2).v1.x*width2)/(width1+width2), this.patternLinesH.get(h1).v1.y, (this.patternLinesH.get(h1).v2.x*width1 + this.patternLinesH.get(h2).v2.x*width2)/(width1+width2), this.patternLinesH.get(h2).v2.y);
					this.linesH.set(h1, newLine);
					this.linesH.remove(h2--);
				}
		
		this.linesV = new ArrayList<LineF>();
		for(int x = 0; x < this.width; x++) {
			LinkedList<Integer> lengths = new LinkedList<Integer>();
			int clength = 1;
			boolean ppixel = false;
			for(int y = 0; y < this.height; y++)
				if(this.bitmap[x][y] == ppixel)
					clength++;
				else {
					lengths.add(clength);
					clength = 1;
					if(lengths.size() == 3) {
						int patternSize = 0;
						for(int i = 0; i < 3; i++)
							patternSize += lengths.get(i);
						if(MathHelper.equalRel(patternSize/3F, lengths.get(0), this.range)
							&& MathHelper.equalRel(patternSize/3F, lengths.get(1), this.range)
							&& MathHelper.equalRel(patternSize/3F, lengths.get(2), this.range))
							this.linesV.add(new LineF(x, y-patternSize, x, y-1));
						lengths.poll();
					}
					ppixel = !ppixel;
				}
		}
		
		for(int v1 = 0; v1 < this.linesV.size()-1; v1++)
			for(int v2 = v1+1; v2 < this.linesV.size(); v2++)
				if(this.linesV.get(v1).v2.x+1 == this.linesV.get(v2).v1.x
					&& MathHelper.equalRel(this.linesV.get(v1).v2.y-this.linesV.get(v1).v1.y, this.linesV.get(v2).v2.y-this.linesV.get(v2).v1.y, this.range2)
					&& MathHelper.equal(this.linesV.get(v1).getCenter().y, this.linesV.get(v2).getCenter().y, 3F)) {
					LineF newLine = new LineF(this.linesV.get(v1).v1.x, Math.min(this.linesV.get(v1).v1.y, this.linesV.get(v2).v1.y), this.linesV.get(v2).v2.x, Math.max(this.linesV.get(v1).v2.y, this.linesV.get(v2).v2.y));
//					float width1 = this.patternLinesV.get(v1).v2.x-this.patternLinesV.get(v1).v1.x+1;
//					float width2 = this.patternLinesV.get(v2).v2.x-this.patternLinesV.get(v2).v1.x+1;
//					LineF newLine = new LineF(this.patternLinesV.get(v1).v1.x, (this.patternLinesV.get(v1).v1.y*width1 + this.patternLinesV.get(v2).v1.y*width2)/(width1+width2), this.patternLinesV.get(v2).v2.x, (this.patternLinesV.get(v1).v2.y*width1 + this.patternLinesV.get(v2).v2.y*width2)/(width1+width2));
					this.linesV.set(v1, newLine);
					this.linesV.remove(v2--);
				}
		
		for(int h = 0; h < this.linesH.size(); h++)
			if(!MathHelper.equalRel((this.linesH.get(h).v2.x-this.linesH.get(h).v1.x)/3F, this.linesH.get(h).v2.y-this.linesH.get(h).v1.y, this.range3))
				this.linesH.remove(h--);
		
		for(int v = 0; v < this.linesV.size(); v++)
			if(!MathHelper.equalRel((this.linesV.get(v).v2.y-this.linesV.get(v).v1.y)/3F, this.linesV.get(v).v2.x-this.linesV.get(v).v1.x, this.range3))
				this.linesV.remove(v--);
		
		this.patterns = new ArrayList<Vector2F>();
		for(int h = 0; h < this.linesH.size(); h++)
			for(int v = 0; v < this.linesV.size(); v++)
				if(MathHelper.equalRel(this.linesH.get(h).v2.x-this.linesH.get(h).v1.x, this.linesV.get(v).v2.y-this.linesV.get(v).v1.y, this.range3)
					&& new LineF(this.linesH.get(h).getCenter(), this.linesV.get(v).getCenter()).getLengthSquare() < 100F) {
//					float patternWidth = this.patternLinesH.get(h).v2.x-this.patternLinesH.get(h).v1.x;
//					float patternHeight = this.patternLinesV.get(v).v2.y-this.patternLinesV.get(v).v1.y;
					Vector2F pattern = new Vector2F(this.linesH.get(h).getCenter().x, this.linesV.get(v).getCenter().y);
					try {
						int x = Math.round(pattern.x);
						int y = Math.round(pattern.y);
						int[] lengths = new int[4];
						int l = 1;
						int clength = 1;
						boolean ppixel = this.bitmap[x][y];
						while(0 <= l) {
							if(this.bitmap[x][y] == ppixel)
								clength++;
							else {
								lengths[l--] = clength;
								clength = 1;
								ppixel = !ppixel;
							}
							x--;
							y--;
						}
						
						x = Math.round(pattern.x);
						y = Math.round(pattern.y);
						l = 2;
						clength = 1;
						ppixel = this.bitmap[x][y];
						while(l < 4) {
							if(this.bitmap[x][y] == ppixel)
								clength++;
							else {
								lengths[l++] = clength;
								clength = 1;
								ppixel = !ppixel;
							}
							x++;
							y++;
						}
						
						int patternSize = 0;
						for(int i = 0; i < 4; i++)
							patternSize += lengths[i];
						if(MathHelper.equalRel(patternSize/3F, lengths[0], this.range)
							&& MathHelper.equalRel(patternSize/3F, lengths[1]+lengths[2], this.range)
							&& MathHelper.equalRel(patternSize/3F, lengths[3], this.range))
							this.patterns.add(pattern);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
		
		System.out.println("H="+this.linesH.size()+" V="+this.linesV.size()+" P="+this.patterns.size());
		
		return this.patterns;
	}
	
	public List<Vector2F> getResult() {
		return this.patterns;
	}
	
	public List<LineF> getHorizontalLines() {
		return this.linesH;
	}
	
	public List<LineF> getVerticalLines() {
		return this.linesV;
	}
}
