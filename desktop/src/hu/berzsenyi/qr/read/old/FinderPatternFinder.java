package hu.berzsenyi.qr.read.old;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FinderPatternFinder {
	private int width, height;
	private boolean[][] bitmap;
	private List<FinderPattern> output;
	private float range; // one equals one's range
	private int range2; // finder pattern equals squared range
	
	public FinderPatternFinder() {
		this(0.5F, 100);
	}
	
	public FinderPatternFinder(float range, int range2) {
		this.range = range;
		this.range2 = range2;
	}
	
	public void setData(int width, int height, boolean[][] bitmap) {
		this.width = width;
		this.height = height;
		this.bitmap = bitmap;
	}
	
	public void process() {
		this.output = new ArrayList<FinderPattern>();
		for(int x = 0; x < this.width; x++) {
			LinkedList<Integer> pastEdges = new LinkedList<Integer>();
			boolean pedge = false;
			int edgeBegin = 0;
			for(int y = 0; y < this.height; y++)
				if(this.bitmap[x][y]) {
					if(!pedge)
						edgeBegin = y;
					pedge = true;
				} else {
					if(pedge) {
						pastEdges.add((y-1+edgeBegin)/2);
						if(pastEdges.size() == 6) {
							int one1 = pastEdges.get(1)-pastEdges.get(0);
							int one2 = pastEdges.get(2)-pastEdges.get(1);
							int three = pastEdges.get(3)-pastEdges.get(2);
							int one3 = pastEdges.get(4)-pastEdges.get(3);
							int one4 = pastEdges.get(5)-pastEdges.get(4);
							float one = (one1+one2+three+one3+one4)/7F;
							if(MathHelper.equal(one, one1, 0.5F)
								&& MathHelper.equal(one, one2, 0.5F)
								&& MathHelper.equal(one*3F, three, 0.5F)
								&& MathHelper.equal(one, one3, 0.5F)
								&& MathHelper.equal(one, one4, 0.5F)) {
								int centerY = (pastEdges.get(0)+pastEdges.get(5))/2;
								int[] edges = new int[6];
							}
							
							pastEdges.poll();
						}
					}
					pedge = false;
				}
		}
	}
	
	public List<FinderPattern> getOutput() {
		return this.output;
	}
}
