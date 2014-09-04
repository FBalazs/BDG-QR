package hu.berzsenyi.qr;

import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainView extends SurfaceView implements SurfaceHolder.Callback {
	public Camera camera;
	public boolean previewRunning;
	
	public MainView(Context context) {
		super(context);
		this.getHolder().addCallback(this);
	}
	
//	public void loadCamera() {
//		new Thread() {
//			@Override
//			public void run() {
//				camera = Camera.open();
//			}
//		}.start();
//	}
	
	public void readQR() {
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.camera = Camera.open();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(this.previewRunning)
			this.camera.stopPreview();
		this.previewRunning = false;
		Camera.Parameters params = this.camera.getParameters();
		params.setPreviewSize(width, height);
		this.camera.setParameters(params);
		try {
			this.camera.setPreviewDisplay(this.getHolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.camera.startPreview();
		this.previewRunning = true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(this.previewRunning) {
			
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.camera.stopPreview();
		this.previewRunning = false;
		this.camera.release();
	}
}
