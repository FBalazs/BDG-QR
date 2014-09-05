package hu.berzsenyi.qr;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class MainActivity extends Activity implements Camera.PictureCallback {
	public static final String TAG = "MainActivity.java";
	
	private AdvancedCamera camera;
	private CameraView view;
	
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.d(TAG, "onPictureTaken");
		if(data == null)
			Log.e(TAG, "null picture");
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
