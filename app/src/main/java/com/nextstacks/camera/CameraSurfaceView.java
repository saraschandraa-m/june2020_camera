package com.nextstacks.camera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    private Camera camera;
    private SurfaceHolder surfaceHolder;


    public CameraSurfaceView(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (surfaceHolder != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.setDisplayOrientation(90);
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (surfaceHolder != null) {
            camera.stopPreview();
            camera.release();
        }
    }
}
