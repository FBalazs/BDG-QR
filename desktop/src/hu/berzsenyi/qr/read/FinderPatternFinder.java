package hu.berzsenyi.qr.read;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FinderPatternFinder {
	private int width, height;
	private boolean[][] bitmap;
	private List<FinderPattern> output;
	private float range = 0.5F; // one equals one's range
	private int range2 = 100; // finder pattern equals squared range
	
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
			for(int y = 0; y < this.height; y++)
				if(this.bitmap[x][y]) {
					if(!pedge) {
						pastEdges.add(y);
						if(6 <= pastEdges.size()) {
							int one1 = pastEdges.get(1)-pastEdges.get(0);
							int one2 = pastEdges.get(2)-pastEdges.get(1);
							int three = pastEdges.get(3)-pastEdges.get(2);
							int one3 = pastEdges.get(4)-pastEdges.get(3);
							int one4 = pastEdges.get(5)-pastEdges.get(4);
							if(MathHelper.equal(one1/(float)one2, 1F, this.range)
								&& MathHelper.equal(one2/(float)one3, 1F, this.range)
								&& MathHelper.equal(one3/(float)one4, 1F, this.range)
								&& MathHelper.equal(three/(float)one2, 3F, this.range)) {
								int y2 = (pastEdges.get(2)+pastEdges.get(3))/2;
								int[] pointsX = new int[6];
								boolean pedge2 = false;
								int i = 2;
								for(int x2 = x; 0 <= x2 && 0 <= i; x2--)
									if(this.bitmap[x2][y2]) {
										if(!pedge2) {
											pointsX[i] = x2;
											i--;
										}
										pedge2 = true;
									} else
										pedge2 = false;
								pedge2 = false;
								i = 3;
								for(int x2 = x; x2 < this.width && i < 6; x2++)
									if(this.bitmap[x2][y2]) {
										if(!pedge2) {
											pointsX[i] = x2;
											i++;
										}
										pedge2 = true;
									} else
										pedge2 = false;
								one1 = pointsX[1]-pointsX[0];
								one2 = pointsX[2]-pointsX[1];
								three = pointsX[3]-pointsX[2];
								one3 = pointsX[4]-pointsX[3];
								one4 = pointsX[5]-pointsX[4];
								if(MathHelper.equal(one1/(float)one2, 1F, this.range)
									&& MathHelper.equal(one2/(float)one3, 1F, this.range)
									&& MathHelper.equal(one3/(float)one4, 1F, this.range)
									&& MathHelper.equal(three/(float)one2, 3F, this.range)) {
									int x2 = (pointsX[2]+pointsX[3])/2;
									boolean added = false;
									for(int j = 0; j < this.output.size() && !added; j++)
										if((this.output.get(j).x-x2)*(this.output.get(j).x-x2) + (this.output.get(j).y-y2)*(this.output.get(j).y-y2) <= this.range2)
											added = true;
									if(!added) {
										this.output.add(new FinderPattern(x2, y2, (one1+one2+one3+one4+three)/7F));
										//System.out.println("Found finder pattern at: "+x2+", "+y2);
									}
								}
							}
							pastEdges.poll();
						}
					}
					pedge = true;
				} else
					pedge = false;
		}
	}
	
	public List<FinderPattern> getOutput() {
		return this.output;
	}
}
