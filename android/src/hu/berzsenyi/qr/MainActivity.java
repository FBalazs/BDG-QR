package hu.berzsenyi.qr;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class MainActivity extends Activity {
	public static final String TAG = "BDG-QR";
	
	private Camera camera;
	private SurfaceView view;
	private boolean isTakingPicture;
	
	private void processImage(byte[] data) {
		Log.d(TAG, "processImage");
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		Log.d(TAG, "onTouchEvent");
		if (e.getAction() == MotionEvent.ACTION_DOWN && this.view.getWidth() / 10 < e.getX() && e.getX() < this.view.getWidth() * 9 / 10
				&& this.view.getHeight() / 10 < e.getY() && e.getY() < this.view.getHeight() * 9 / 10) {
			if(this.camera != null) {
				if(!this.isTakingPicture) {
					this.isTakingPicture = true;
					new Thread() {
						@Override
						public void run() {
							Log.d(TAG, "takePicture");
							camera.takePicture(null, new Camera.PictureCallback() {
								@Override
								public void onPictureTaken(byte[] data, Camera camera) {
									processImage(data);
								}
							}, null);
							isTakingPicture = false;
						}
					}.start();
				}
			} else {
				Log.d(TAG, "camera is null");
			}
			return true;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		this.view = new SurfaceView(this);
		this.setContentView(this.view);
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();

		try {
			this.camera = Camera.open();
			this.camera.lock();
			this.camera.setPreviewDisplay(this.view.getHolder());
			this.camera.startPreview();
		} catch (Exception e) {
			Log.d(TAG, "couldn't open camera");
			if (this.camera != null) {
				this.camera.stopPreview();
				this.camera.unlock();
				this.camera.release();
				this.camera = null;
			}
			// TODO: close application
		}
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();

		if (this.camera != null) {
			this.camera.stopPreview();
			this.camera.unlock();
			this.camera.release();
			this.camera = null;
		}
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}
}
