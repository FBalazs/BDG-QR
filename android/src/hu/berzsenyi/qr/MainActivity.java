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
		if(this.showPicture) {
			ImageView imgView = new ImageView(this);
			imgView.setImageBitmap(img);
			this.setContentView(imgView);
		}
		
		long time = System.currentTimeMillis();
		
		int[] pixels = new int[img.getWidth()*img.getHeight()];
		img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
		long brightAvg = 0;
		for(int i = 0; i < img.getWidth(); i++)
			for(int j = 0; j < img.getHeight(); j++) {
				int rgb = pixels[j*img.getWidth()+i];
				brightAvg += rgb >> 16 + (rgb >> 8) & 255 + rgb & 255;
			}
		brightAvg /= img.getWidth()*img.getHeight();
		boolean[][] imgBW = new boolean[img.getWidth()][img.getHeight()];
		for(int i = 0; i < img.getWidth(); i++)
			for(int j = 0; j < img.getHeight(); j++) {
				int rgb = pixels[j*img.getWidth()+i];
				imgBW[i][j] = (rgb >> 16 + (rgb >> 8) & 255 + rgb & 255) > brightAvg;
			}
		Log.i(TAG, "avgBright="+brightAvg);
		
		Log.i(TAG, "deltaTime="+(System.currentTimeMillis()-time));
		
		this.processQR(null);
		
		if(this.showPicture)
			this.setContentView(this.view);
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
