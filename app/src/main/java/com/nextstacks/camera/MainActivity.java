package com.nextstacks.camera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static int CAMERA_INTENT = 1000;

    private ImageView mIvImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIvImage = findViewById(R.id.imageView);

    }


    public void doTakePicture(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_INTENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap capturedBitmap = null;

        if (requestCode == CAMERA_INTENT) {
            if (data.getExtras() != null) {
                capturedBitmap = (Bitmap) data.getExtras().get("data");
            } else {
                try {
                    capturedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (capturedBitmap != null) {
                Matrix matrix = new Matrix();
                matrix.setRotate(90);
                Bitmap newBitmap = Bitmap.createScaledBitmap(capturedBitmap, capturedBitmap.getWidth(), capturedBitmap.getHeight(), false);
                Bitmap rotateBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(), newBitmap.getHeight(), matrix, false);
                mIvImage.setImageBitmap(rotateBitmap);
            }
        }
    }
}