package hu.berzsenyi.qr.read;

public class Vector2F {
	public static Vector2F add(Vector2F a, Vector2F b) {
		return a.clone().add(b);
	}
	
	public static Vector2F sub(Vector2F a, Vector2F b) {
		return a.clone().sub(b);
	}
	
	public static Vector2F scale(Vector2F a, float scale) {
		return a.clone().scale(scale);
	}
	
	public static Vector2F scale(Vector2F a, Vector2F b) {
		return a.clone().scale(b);
	}
	
	public static Vector2F linearComb(Vector2F ax, Vector2F ay, float x, float y) {
		return add(scale(ax, x), scale(ay, y));
	}
	
	public float x, y;
	
	public Vector2F() {
		this(0F, 0F);
	}
	
	public Vector2F(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2F(Vector2F src) {
		this(src.x, src.y);
	}
	
	@Override
	public Vector2F clone() {
		return new Vector2F(this);
	}
	
	public float getLength() {
		return (float)Math.sqrt(this.getLengthSquare());
	}
	
	public float getLengthSquare() {
		return this.x*this.x + this.y*this.y;
	}
	
	public Vector2F add(Vector2F vec) {
		this.x += vec.x;
		this.y += vec.y;
		return this;
	}
	
	public Vector2F sub(Vector2F vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		return this;
	}
	
	public Vector2F scale(float scale) {
		this.x *= scale;
		this.y *= scale;
		return this;
	}
	
	public Vector2F scale(Vector2F vec) {
		this.x *= vec.x;
		this.y *= vec.y;
		return this;
	}
}
