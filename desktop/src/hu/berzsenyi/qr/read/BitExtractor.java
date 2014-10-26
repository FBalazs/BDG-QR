package hu.berzsenyi.qr.read;

public class BitExtractor {
	private int width, height;
	private int[] grayScale;
	private boolean[][] bitmap;
	
	public BitExtractor() {
		
	}
	
	public boolean[][] extract(int width, int height, int[] grayScale) {
		this.width = width;
		this.height = height;
		this.grayScale = grayScale;
		
		long gray = 0;
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++)
				gray += grayScale[x + y*this.width];
		gray /= this.width*this.height;
		
		this.bitmap = new boolean[this.width][this.height];
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++)
				this.bitmap[x][y] = gray < this.grayScale[x + y*this.width];
		
		return this.bitmap;
	}
}
