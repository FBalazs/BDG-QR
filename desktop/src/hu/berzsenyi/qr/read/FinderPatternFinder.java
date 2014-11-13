package hu.berzsenyi.qr.read;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FinderPatternFinder {
	private int width, height;
	private boolean[][] bitmap;
	
	public float range, range2, range3;
	
	private List<Vector2F> patterns;
	private List<LineF> patternLinesH, patternLinesV;
	
	public FinderPatternFinder(float range, float range2, float range3) {
		this.range = range;
		this.range2 = range2;
		this.range3 = range3;
	}
	
	public List<Vector2F> findPatterns(int width, int height, boolean[][] bitmap) {
		this.width = width;
		this.height = height;
		this.bitmap = bitmap;
		
		this.patternLinesH = new ArrayList<LineF>();
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
					if(lengths.size() == 5) {
						int patternSize = 0;
						for(int i = 0; i < 5; i++)
							patternSize += lengths.get(i);
						if(MathHelper.equalRel(patternSize/7F, lengths.get(0), this.range)
							&& MathHelper.equalRel(patternSize/7F, lengths.get(1), this.range)
							&& MathHelper.equalRel(patternSize*3/7F, lengths.get(2), this.range)
							&& MathHelper.equalRel(patternSize/7F, lengths.get(3), this.range)
							&& MathHelper.equalRel(patternSize/7F, lengths.get(4), this.range))
							this.patternLinesH.add(new LineF(x-patternSize, y, x-1, y));
						lengths.poll();
					}
					ppixel = !ppixel;
				}
		}
		
		for(int h1 = 0; h1 < this.patternLinesH.size()-1; h1++)
			for(int h2 = h1+1; h2 < this.patternLinesH.size(); h2++)
				if(this.patternLinesH.get(h1).v2.y+1 == this.patternLinesH.get(h2).v1.y
					&& MathHelper.equalRel(this.patternLinesH.get(h1).v2.x-this.patternLinesH.get(h1).v1.x, this.patternLinesH.get(h2).v2.x-this.patternLinesH.get(h2).v1.x, this.range2)
					&& MathHelper.equal(this.patternLinesH.get(h1).getCenter().x, this.patternLinesH.get(h2).getCenter().x, 5F)) {
					LineF newLine = new LineF(Math.min(this.patternLinesH.get(h1).v1.x, this.patternLinesH.get(h2).v1.x), this.patternLinesH.get(h1).v1.y, Math.max(this.patternLinesH.get(h1).v2.x, this.patternLinesH.get(h2).v2.x), this.patternLinesH.get(h2).v2.y);
//					float width1 = this.patternLinesH.get(h1).v2.y-this.patternLinesH.get(h1).v1.y+1;
//					float width2 = this.patternLinesH.get(h2).v2.y-this.patternLinesH.get(h2).v1.y+1;
//					LineF newLine = new LineF((this.patternLinesH.get(h1).v1.x*width1 + this.patternLinesH.get(h2).v1.x*width2)/(width1+width2), this.patternLinesH.get(h1).v1.y, (this.patternLinesH.get(h1).v2.x*width1 + this.patternLinesH.get(h2).v2.x*width2)/(width1+width2), this.patternLinesH.get(h2).v2.y);
					this.patternLinesH.set(h1, newLine);
					this.patternLinesH.remove(h2--);
				}
		
		this.patternLinesV = new ArrayList<LineF>();
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
					if(lengths.size() == 5) {
						int patternSize = 0;
						for(int i = 0; i < 5; i++)
							patternSize += lengths.get(i);
						if(MathHelper.equalRel(patternSize/7F, lengths.get(0), this.range)
							&& MathHelper.equalRel(patternSize/7F, lengths.get(1), this.range)
							&& MathHelper.equalRel(patternSize*3/7F, lengths.get(2), this.range)
							&& MathHelper.equalRel(patternSize/7F, lengths.get(3), this.range)
							&& MathHelper.equalRel(patternSize/7F, lengths.get(4), this.range))
							this.patternLinesV.add(new LineF(x, y-patternSize, x, y-1));
						lengths.poll();
					}
					ppixel = !ppixel;
				}
		}
		
		for(int v1 = 0; v1 < this.patternLinesV.size()-1; v1++)
			for(int v2 = v1+1; v2 < this.patternLinesV.size(); v2++)
				if(this.patternLinesV.get(v1).v2.x+1 == this.patternLinesV.get(v2).v1.x
					&& MathHelper.equalRel(this.patternLinesV.get(v1).v2.y-this.patternLinesV.get(v1).v1.y, this.patternLinesV.get(v2).v2.y-this.patternLinesV.get(v2).v1.y, this.range2)
					&& MathHelper.equal(this.patternLinesV.get(v1).getCenter().y, this.patternLinesV.get(v2).getCenter().y, 5F)) {
					LineF newLine = new LineF(this.patternLinesV.get(v1).v1.x, Math.min(this.patternLinesV.get(v1).v1.y, this.patternLinesV.get(v2).v1.y), this.patternLinesV.get(v2).v2.x, Math.max(this.patternLinesV.get(v1).v2.y, this.patternLinesV.get(v2).v2.y));
//					float width1 = this.patternLinesV.get(v1).v2.x-this.patternLinesV.get(v1).v1.x+1;
//					float width2 = this.patternLinesV.get(v2).v2.x-this.patternLinesV.get(v2).v1.x+1;
//					LineF newLine = new LineF(this.patternLinesV.get(v1).v1.x, (this.patternLinesV.get(v1).v1.y*width1 + this.patternLinesV.get(v2).v1.y*width2)/(width1+width2), this.patternLinesV.get(v2).v2.x, (this.patternLinesV.get(v1).v2.y*width1 + this.patternLinesV.get(v2).v2.y*width2)/(width1+width2));
					this.patternLinesV.set(v1, newLine);
					this.patternLinesV.remove(v2--);
				}
		
		for(int h = 0; h < this.patternLinesH.size(); h++)
			if(!MathHelper.equalRel((this.patternLinesH.get(h).v2.x-this.patternLinesH.get(h).v1.x)/7F, (this.patternLinesH.get(h).v2.y-this.patternLinesH.get(h).v1.y)/3F, this.range3))
				this.patternLinesH.remove(h--);
		
		for(int v = 0; v < this.patternLinesV.size(); v++)
			if(!MathHelper.equalRel((this.patternLinesV.get(v).v2.y-this.patternLinesV.get(v).v1.y)/7F, (this.patternLinesV.get(v).v2.x-this.patternLinesV.get(v).v1.x)/3F, this.range3))
				this.patternLinesV.remove(v--);
		
		this.patterns = new ArrayList<Vector2F>();
		for(int h = 0; h < this.patternLinesH.size(); h++)
			for(int v = 0; v < this.patternLinesV.size(); v++)
				if(MathHelper.equalRel(this.patternLinesH.get(h).v2.x-this.patternLinesH.get(h).v1.x, this.patternLinesV.get(v).v2.y-this.patternLinesV.get(v).v1.y, this.range3)
					&& new LineF(this.patternLinesH.get(h).getCenter(), this.patternLinesV.get(v).getCenter()).getLengthSquare() < 100F) {
//					float patternWidth = this.patternLinesH.get(h).v2.x-this.patternLinesH.get(h).v1.x;
//					float patternHeight = this.patternLinesV.get(v).v2.y-this.patternLinesV.get(v).v1.y;
					this.patterns.add(new Vector2F(this.patternLinesH.get(h).getCenter().x, this.patternLinesV.get(v).getCenter().y));
				}
		
		System.out.println("H="+this.patternLinesH.size()+" V="+this.patternLinesV.size()+" P="+this.patterns.size());
		
		return this.patterns;
	}
	
	public List<Vector2F> getResult() {
		return this.patterns;
	}
	
	public List<LineF> getHorizontalLines() {
		return this.patternLinesH;
	}
	
	public List<LineF> getVerticalLines() {
		return this.patternLinesV;
	}
}
