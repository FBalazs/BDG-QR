package hu.berzsenyi.qr;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MainActivity extends Activity implements Camera.PictureCallback {
	public static final String TAG = "MainActivity.java";
	
	private AdvancedCamera camera;
	private CameraView view;
	
	private boolean showPicture = false;
	
	private void processQR(boolean[][] bits) {
		
	}
	
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.d(TAG, "onPictureTaken");
		if(data == null)
			Log.e(TAG, "null picture");
		Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length);
		int width = img.getWidth();
		int height = img.getHeight();
		if(this.showPicture) {
			ImageView imgView = new ImageView(this);
			imgView.setImageBitmap(img);
			this.setContentView(imgView);
		}
		
		long time = System.currentTimeMillis();
		
		int[] pixels = new int[width*height];
		img.getPixels(pixels, 0, width, 0, 0, width, height);
		long brightAvg = 0;
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++) {
				int index = j*width+i;
				pixels[index] = pixels[index] >> 16 + (pixels[index] >> 8) & 255 + pixels[index] & 255;
				brightAvg += pixels[index];
			}
		brightAvg /= width*height;
		boolean[][] imgBW = new boolean[width][height];
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
				imgBW[i][j] = pixels[j*width+i] > brightAvg;
		Log.i(TAG, "avgBright="+brightAvg);
		
		Log.i(TAG, "deltaTime="+(System.currentTimeMillis()-time));
		this.processQR(null);
		
		if(this.showPicture)
			this.setContentView(this.view);
		img.recycle();
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		//Log.d(TAG, "onTouchEvent");
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
		this.camera = new AdvancedCamera();
		this.view = new CameraView(this, this.camera);
		this.setContentView(this.view);
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
