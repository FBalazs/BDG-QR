package hu.berzsenyi.qr;

import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainView extends SurfaceView implements SurfaceHolder.Callback {
	public Camera camera;
	public boolean previewRunning;
	
	public MainView(Context context) {
		super(context);
		this.getHolder().addCallback(this);
		this.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	public void releaseCamera() {
		if(this.previewRunning)
			this.camera.stopPreview();
		this.previewRunning = false;
		this.camera.release();
	}
	
	public void readQR(byte[] data) {
		try {
			Log.i("hu.berzsenyi.qr", "reading image");
			
			Log.i("hu.berzsenyi.qr", "processed qr code");
		} catch(Exception e) {
			this.releaseCamera();
		}
	}
	
	@Override
	public boolean performClick() {
		return super.performClick();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		try {
			if(this.getWidth()/10 < e.getX() && e.getX() < this.getWidth()*9/10 && this.getHeight()/10 < e.getY() && e.getY() < this.getHeight()*9/10) {
				this.camera.takePicture(null, new Camera.PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						readQR(data);
					}
				}, null);
				this.performClick();
				return true;
			}
			return super.onTouchEvent(e);
		} catch(Exception exc) {
			this.releaseCamera();
			return false;
		}
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			this.camera = Camera.open();
		} catch(Exception e) {
			this.releaseCamera();
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		try {
			if(this.previewRunning)
				this.camera.stopPreview();
			this.previewRunning = false;
			Camera.Parameters params = this.camera.getParameters();
			params.setPreviewSize(width, height);
			this.camera.setParameters(params);
			try {
				this.camera.setPreviewDisplay(this.getHolder());
			} catch (IOException e) {
				//e.printStackTrace();
				this.camera.release();
				return;
			}
			this.camera.startPreview();
			this.previewRunning = true;
		} catch(Exception e) {
			this.releaseCamera();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.releaseCamera();
	}
}
