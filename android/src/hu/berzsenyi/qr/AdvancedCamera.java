package hu.berzsenyi.qr;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

public class AdvancedCamera {
	public static final String TAG = "AdvancedCamera.java";
	
	private Camera camera = null;
	private boolean isTakingPicture = false;
	
	public int open(int cameraId) {
		Log.d(TAG, "open");
		try {
			this.camera = Camera.open(cameraId);
			return cameraId;
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			this.release();
			return -1;
		}
	}
	
	public int startPreview(SurfaceHolder holder) {
		Log.d(TAG, "startPreview");
		try {
			this.camera.setPreviewDisplay(holder);
			this.camera.startPreview();
			return 0;
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			this.release();
			return -1;
		}
	}
	
	public int takePicture(final Camera.PictureCallback callback) {
		Log.d(TAG, "takePicture");
		try {
			if(this.isTakingPicture)
				return 0;
			this.camera.stopPreview();
			this.isTakingPicture = true;
			this.camera.takePicture(null, null, new Camera.PictureCallback() {
				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					callback.onPictureTaken(data, camera);
					camera.startPreview();
					isTakingPicture = false;
				}
			});
			return 0;
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			return -1;
		}
	}
	
	public int setPreviewSize(int width, int height) {
		Log.d(TAG, "setPreviewSize");
		try {
			this.camera.stopPreview();
			Camera.Parameters params = this.camera.getParameters();
			params.setPreviewSize(width, height);
			this.camera.setParameters(params);
			this.camera.startPreview();
			return 0;
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			this.release();
			return -1;
		}
	}
	
	public int stopPreview() {
		Log.d(TAG, "stopPreview");
		try {
			this.camera.stopPreview();
			return 0;
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			this.release();
			return -1;
		}
	}
	
	public int release() {
		Log.d(TAG, "release");
		try {
			this.camera.release();
			this.camera = null;
			return 0;
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			return -1;
		}
	}
}
