package com.example.kameradeneme;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements TakePictureCallBack {
    private static final String TAG = "AndroidCameraApi";
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Button takePictureButton;
    private TextureView textureViewOn;
    private TextureListener tlOn;

    private TextureView textureViewArka;
    private TextureListener tlArka;
    private int sayac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add permission for camera and let user grant the permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            return;
        }

        textureViewOn = findViewById(R.id.textureOn);
        tlOn = new TextureListener(this, 0, textureViewOn);
        textureViewOn.setSurfaceTextureListener(tlOn);

        textureViewArka = findViewById(R.id.textureArka);
        tlArka = new TextureListener(this, 1, textureViewArka);
        textureViewArka.setSurfaceTextureListener(tlArka);

        takePictureButton = findViewById(R.id.btn_takepicture);
        assert takePictureButton != null;

        sayac = 0;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sayac % 2 == 0)
                    tlArka.TakePicture(MainActivity.this::Save);
                else
                    tlOn.TakePicture(MainActivity.this::Save);
                sayac++;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        if (textureViewOn.isAvailable()) {
            tlOn.openCamera();
        } else {
            textureViewOn.setSurfaceTextureListener(tlOn);
        }

        if (textureViewArka.isAvailable()) {
            tlArka.openCamera();
        } else {
            textureViewArka.setSurfaceTextureListener(tlArka);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        tlOn.CloseCamera();
        tlArka.CloseCamera();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(MainActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void Save(TextureListener textureListener, byte[] bytes) {
        String fname = Environment.getExternalStorageDirectory() + "/pic" + textureListener.cameraId + "_" + sayac + ".jpg";
        Log.d(TAG, "Saving:" + fname);
        File file = new File(fname);

        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(bytes);
        } catch (IOException e) {
            Log.d(TAG, "Not saved:" + e.getMessage());
        } finally {
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}