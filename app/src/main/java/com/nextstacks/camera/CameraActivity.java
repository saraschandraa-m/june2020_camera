package com.nextstacks.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {

    private FrameLayout mCameraFrame;
    private Camera camera;
    private int cameraID;
    private ImageView mIvCaptureImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCameraFrame = findViewById(R.id.camera_frame);

        ImageView mIvChangeCamera = findViewById(R.id.iv_change_camera);
        ImageView mIvTakePhoto = findViewById(R.id.iv_capture);
        mIvCaptureImage = findViewById(R.id.iv_catpured_image);

        mIvChangeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.stopPreview();
                boolean isFacingFront;
                if (cameraID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    isFacingFront = true;
                } else {
                    isFacingFront = false;
                }
                initiateCamera(!isFacingFront);
            }
        });

        mIvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes, Camera camera) {
                        Bitmap capturedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);


                        saveImageToDevice(capturedImage);


                    }
                });
            }
        });

        checkPermissions();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void checkPermissions() {
        if ((ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initiateCamera(false);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 999);
        }
    }


    private void initiateCamera(boolean isFrontCamera) {
        if (isFrontCamera) {
            cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        camera = Camera.open(cameraID);
        CameraSurfaceView surfaceView = new CameraSurfaceView(CameraActivity.this, camera);
        mCameraFrame.addView(surfaceView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 999) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                initiateCamera(false);
            } else {
                Toast.makeText(CameraActivity.this, "User Denied Permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void saveImageToDevice(Bitmap capturedImage) {

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Batch2Camera");

        if (!directory.exists()) {
            directory.mkdir();
        }

        File imageName = new File(directory, "IMG_" + System.currentTimeMillis() + ".png");
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(imageName);
            capturedImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mIvCaptureImage.setImageBitmap(capturedImage);
        camera.startPreview();
    }

    private void readImagesFromDevice() {
        Uri imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] proj = new String[]{MediaStore.Images.Media.DATA};


        ArrayList<String> images = new ArrayList<>();

        Cursor cursor = getApplicationContext().getContentResolver().query(imageURI, proj, null, null, null);

        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToFirst()) {
                String image = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                images.add(image);
            }
        }
    }
}