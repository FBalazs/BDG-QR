package hu.berzsenyi.qr.read;

public class GaussianBlur {
	public int radius;
	public float threshold;
	private float[][] mask;
	
	private int width, height;
	private int[] pixels, blured;
	
	public GaussianBlur() {
		this(2, 1F);
	}
	
	public GaussianBlur(int radius, float threshold) {
		this.radius = radius;
		this.threshold = threshold;
	}
	
	public int[] blur(int width, int height, int[] pixels) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
		
		this.mask = new float[this.radius * 2 + 1][this.radius * 2 + 1];
		float sum = 0F;
		for (int x = -this.radius; x <= this.radius; x++)
			for (int y = -this.radius; y <= this.radius; y++) {
				this.mask[x + this.radius][y + this.radius] = (float)(Math.exp(-(x*x + y*y)/(2*this.threshold*this.threshold))/(2*Math.PI*this.threshold*this.threshold));
				sum += this.mask[x+this.radius][y+this.radius];
			}
		for (int x = -this.radius; x <= this.radius; x++)
			for (int y = -this.radius; y <= this.radius; y++)
				this.mask[x + this.radius][y + this.radius] /= sum;
		
		this.blured = new int[this.pixels.length];
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++)
				if(this.radius <= x && x < this.width-this.radius && this.radius <= y && y < this.height-this.radius) {
					sum = 0F;
					for(int ox = -this.radius; ox <= this.radius; ox++)
						for(int oy = -this.radius; oy <= this.radius; oy++)
							sum += this.mask[ox+this.radius][oy+this.radius]*this.pixels[(y+oy)*this.width + x+ox];
					this.blured[y*this.width + x] = (int)sum;
				} else
					this.blured[y*this.width + x] = this.pixels[y*this.width + x];
		
		return this.blured;
	}
	
	public int[] getResult() {
		return this.blured;
	}
}
