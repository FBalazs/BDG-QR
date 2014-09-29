package hu.berzsenyi.qr.read;

public class Coords {
	public float x, y;
	
	public Coords() {
		this(0F, 0F);
	}
	
	public Coords(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Coords(Coords src) {
		this(src.x, src.y);
	}
}
