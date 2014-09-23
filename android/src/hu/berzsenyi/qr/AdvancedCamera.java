package hu.berzsenyi.qr;

import java.util.List;

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
			//this.camera.setDisplayOrientation(0);
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
			this.isTakingPicture = true;
			this.camera.takePicture(null, null, new Camera.PictureCallback() {
				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					camera.startPreview();
					callback.onPictureTaken(data, camera);
					isTakingPicture = false;
				}
			});
			return 0;
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			return -1;
		}
	}
	
	public Camera.Size setPreviewSize(int width, int height) {
		Log.d(TAG, "setPreviewSize");
		try {
			this.camera.stopPreview();
			Camera.Parameters params = this.camera.getParameters();
			List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
			Camera.Size previewSize = null;
			for(Camera.Size size : previewSizes)
				if(previewSize == null || previewSize.width < size.width)
					previewSize = size;
			Log.i(TAG, "setting preview size to "+previewSize.width+"*"+previewSize.height);
			params.setPreviewSize(previewSize.width, previewSize.height);
			if(width > height) {
				params.setRotation(0);
			} else {
				params.setRotation(90);
				int s = previewSize.width;
				previewSize.width = previewSize.height;
				previewSize.height = s;
			}
			
			List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
			Camera.Size pictureSize = null;
			for(Camera.Size size : pictureSizes)
				if(pictureSize == null || pictureSize.width > size.width)
					pictureSize = size;
			Log.i(TAG, "setting picture size to "+pictureSize.width+"*"+pictureSize.height);
			params.setPictureSize(pictureSize.width, pictureSize.height);
			
			this.camera.setParameters(params);
			this.camera.startPreview();
			return previewSize;
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			this.release();
			return null;
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
