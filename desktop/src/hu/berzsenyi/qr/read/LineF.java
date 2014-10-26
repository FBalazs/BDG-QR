package hu.berzsenyi.qr.read;

public class LineF {
	public Vector2F v1, v2;
	
	public LineF(Vector2F v1, Vector2F v2) {
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public LineF(float x1, float y1, float x2, float y2) {
		this.v1 = new Vector2F(x1, y1);
		this.v2 = new Vector2F(x2, y2);
	}
	
	public LineF(LineF line) {
		this.v1 = line.v1;
		this.v2 = line.v2;
	}
	
	public Vector2F getCenter() {
		return new Vector2F((this.v1.x+this.v2.x)/2F, (this.v1.y+this.v2.y)/2F);
	}
	
	public float getLengthSquare() {
		return (this.v2.x-this.v1.x)*(this.v2.x-this.v1.x) + (this.v2.y-this.v1.y)*(this.v2.y-this.v1.y);
	}
	
	public float getLength() {
		return (float)Math.sqrt(this.getLengthSquare());
	}
}
