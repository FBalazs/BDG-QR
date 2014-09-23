package hu.berzsenyi.qr;

import java.util.Random;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MainActivity extends Activity implements Camera.PictureCallback {
	public static final String TAG = "MainActivity.java";
	
	public static final int gausMaskDiv = 159;
	public static final int[][] gausMask = new int[5][5];
	
	static {
		gausMask[0][0] = 2;	 gausMask[0][1] = 4;  gausMask[0][2] = 5;  gausMask[0][3] = 4;  gausMask[0][4] = 2;	
		gausMask[1][0] = 4;	 gausMask[1][1] = 9;  gausMask[1][2] = 12; gausMask[1][3] = 9;  gausMask[1][4] = 4;	
		gausMask[2][0] = 5;	 gausMask[2][1] = 12; gausMask[2][2] = 15; gausMask[2][3] = 12; gausMask[2][4] = 2;	
		gausMask[3][0] = 4;	 gausMask[3][1] = 9;  gausMask[3][2] = 12; gausMask[3][3] = 9;  gausMask[3][4] = 4;	
		gausMask[4][0] = 2;	 gausMask[4][1] = 4;  gausMask[4][2] = 5;  gausMask[4][3] = 4;  gausMask[4][4] = 2;
	}

	private AdvancedCamera camera;
	private CameraView view;

	private boolean showPicture = false;

	private void processQR(boolean[][] bits) {

	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.d(TAG, "onPictureTaken");
		if (data == null)
			Log.e(TAG, "null picture");
		Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length);
		int width = img.getWidth();
		int height = img.getHeight();
		if (this.showPicture) {
			ImageView imgView = new ImageView(this);
			imgView.setImageBitmap(img);
			this.setContentView(imgView);
		}

		long time = System.currentTimeMillis();
		
//		int[][] grayscale = new int[width][height];
//		for (int x = 0; x < width; x++)
//			for (int y = 0; y < height; y++) {
//				int rgb = img.getPixel(x, y);
//				grayscale[x][y] = (rgb >> 16) + ((rgb >> 8) & 255) + (rgb & 255);
//			}
//
//		int[][] blured = new int[width][height];
//		for (int x = 0; x < width; x++)
//			for (int y = 0; y < height; y++)
//				if (2 < x && x < width - 2 && 2 < y && y < height - 2) {
//					blured[x][y] = 0;
//					for(int ox = -2; ox <= 2; ox++)
//						for(int oy = -2; oy <= 2; oy++)
//							blured[x][y] += grayscale[x+ox][y+oy]*gausMask[ox+2][oy+2];
//					blured[x][y] /= 159;
//				} else
//					blured[x][y] = grayscale[x][y];
		// TODO read qr code

		Log.i(TAG, "deltaTime=" + (System.currentTimeMillis() - time));
		this.processQR(null);

		if (this.showPicture)
			this.setContentView(this.view);
		img.recycle();
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// Log.d(TAG, "onTouchEvent");
		if (e.getAction() == MotionEvent.ACTION_DOWN && this.view.getWidth() / 10 < e.getX() && e.getX() < this.view.getWidth() * 9 / 10
				&& this.view.getHeight() / 10 < e.getY() && e.getY() < this.view.getHeight() * 9 / 10) {
			this.camera.takePicture(this);
			return true;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.camera = new AdvancedCamera();
		this.view = new CameraView(this, this.camera);
		this.setContentView(this.view);
		
		long time = System.currentTimeMillis();
		int width = 480, height = 320;
		Random rand = new Random(time);
		int[][] grayscale = new int[width][height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				int rgb = rand.nextInt();
				grayscale[x][y] = (rgb >> 16) + ((rgb >> 8) & 255) + (rgb & 255);
			}

		int[][] blured = new int[width][height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				if (2 < x && x < width - 2 && 2 < y && y < height - 2) {
					blured[x][y] = 0;
					for(int ox = -2; ox <= 2; ox++)
						for(int oy = -2; oy <= 2; oy++)
							blured[x][y] += grayscale[x+ox][y+oy]*gausMask[ox+2][oy+2];
					blured[x][y] /= 159;
				} else
					blured[x][y] = grayscale[x][y];
		Log.i(TAG, "deltaTime=" + (System.currentTimeMillis() - time));
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}
}
