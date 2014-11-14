package hu.berzsenyi.qr.read;

public class BitExtractor {
	private int width, height;
	private int[] grayScale;
	private long[] histogram;
	private int threshold;
	private boolean[][] bitmap;
	
	public boolean[][] extract(int width, int height, int[] grayScale) {
		this.width = width;
		this.height = height;
		this.grayScale = grayScale;
		
		this.histogram = new long[256];
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++)
				this.histogram[this.grayScale[y*this.width + x]]++;
		
		this.threshold = 0;
		float maxDist = 0F;
		long sumAll = 0;
		for(int i = 0; i < 256; i++)
			sumAll += this.histogram[i]*i;
		long nBlack = 0;
		long sumBlack = 0;
		for(int t = 0; t < 256; t++) {
			nBlack += this.histogram[t];
			sumBlack += this.histogram[t]*t;
			float dist = (sumAll-sumBlack)/(float)(this.width*this.height-nBlack) - sumBlack/(float)nBlack;
			dist = dist*dist*dist*dist*nBlack*(this.width*this.height-nBlack);
			if(maxDist < dist) {
				maxDist = dist;
				this.threshold = t;
			}
		}
		
		this.bitmap = new boolean[this.width][this.height];
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++)
				this.bitmap[x][y] = this.threshold < this.grayScale[x + y*this.width];
		
		return this.bitmap;
	}
	
	public boolean[][] getBitmap() {
		return this.bitmap;
	}
}
