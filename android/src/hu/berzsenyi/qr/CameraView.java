package hu.berzsenyi.qr;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
	public static final String TAG = "CameraView.java";
	
	public AdvancedCamera camera;
	
	public CameraView(Context context, AdvancedCamera camera) {
		super(context);
		this.camera = camera;
		this.getHolder().addCallback(this);
		this.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		this.getHolder().setKeepScreenOn(true);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");
		if(this.camera.open(Camera.CameraInfo.CAMERA_FACING_BACK) == -1)
			this.camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		this.camera.startPreview(this.getHolder());
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(TAG, "surfaceChanged");
		this.camera.setPreviewSize(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");
		this.camera.stopPreview();
		this.camera.release();
	}
}
