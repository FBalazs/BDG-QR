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
	
	private boolean showPicture = true;
	
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
		
		// TODO: detect qr code
//		double avgBright = 0;
//		for(int i = 0; i < img.getWidth(); i++)
//			for(int j = 0; j < img.getHeight(); j++) {
//				int rgb = img.getPixel(i, j);
//				int c = (rgb >> 16 + (rgb >> 8) & 255 + rgb & 255)/3;
//				avgBright += c/(double)(img.getWidth()*img.getHeight());
//				img.setPixel(i, j, c);
//			}
//		boolean[][] imgBW = new boolean[img.getWidth()][img.getHeight()];
//		for(int i = 0; i < img.getWidth(); i++)
//			for(int j = 0; j < img.getHeight(); j++)
//				imgBW[i][j] = img.getPixel(i, j) > avgBright;
//		Log.d(TAG, "avgBright="+avgBright);
		
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
