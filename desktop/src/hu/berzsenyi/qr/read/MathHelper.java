package hu.berzsenyi.qr.read;

public class MathHelper {
	public static float atan(float tan) {
		return (float)(Math.atan(tan)*180/Math.PI);
	}
	
	public static float sin(float deg) {
		return (float)Math.sin(deg*Math.PI/180);
	}
	
	public static float cos(float deg) {
		return sin(90F-deg);
	}
	
	public static float[][] matrixTranslate(float x, float y) {
		return new float[][]{{1, 0, x},
							{0, 1, y},
							{0, 0, 1}};
	}
	
	public static float[][] matrixScale(float w, float h) {
		return new float[][]{{w, 0, 0},
							{0, h, 0},
							{0, 0, 1}};
	}
	
	public static float[][] matrixRotate(float deg) {
		return new float[][]{{cos(deg), sin(deg), 0},
							{-sin(deg), cos(deg), 0},
							{0, 0, 1}};
	}
	
	public static float[][] matrixShearX(float a) {
		return new float[][]{{1, 0, 0},
							{a, 1, 0},
							{0, 0, 1}};
	}
	
	public static float[][] matrixShearY(float b) {
		return new float[][]{{1, b, 0},
							{0, 1, 0},
							{0, 0, 1}};
	}
	
	public static boolean equal(float a, float b, float r) {
		return Math.abs(a-b) < r;
	}
	
	public static boolean equalRel(float a, float b, float r) {
		return equal(a, b, (a+b)*r);
	}
	
	public static float[] matrixDotVector(float[][] mat, float[] vec) {
		return new float[]{mat[0][0]*vec[0] + mat[1][0]*vec[1] + mat[2][0]*vec[2],
						mat[0][1]*vec[0] + mat[1][1]*vec[1] + mat[2][1]*vec[2],
						mat[0][2]*vec[0] + mat[1][2]*vec[1] + mat[2][2]*vec[2]};
	}
	
	public static float[][] matrixDotMatrix(float[][] mat1, float[][] mat2) {
		float[][] ret = new float[3][3];
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				for(int k = 0; k < 3; k++)
					ret[i][j] += mat1[k][i]*mat2[j][k];
		return ret;
	}
}
