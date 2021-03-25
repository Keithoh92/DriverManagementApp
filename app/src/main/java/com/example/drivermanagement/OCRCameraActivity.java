package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class OCRCameraActivity extends AppCompatActivity {

    private static final String TAG = OCRCameraActivity.class.getSimpleName();
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    Button cameraButton;
    PreviewView previewView;
    private ImageCapture imageCapture;
    private ImageAnalysis imageAnalysis;
    BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private Executor exec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_c_r_camera);
        cameraButton = findViewById(R.id.camera_take_pic);
        toolbar = findViewById(R.id.toolbar_ocr);
        previewView = findViewById(R.id.camera_prev);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder()
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Set up the capture use case to allow users to take photos
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .build();

                imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setTargetResolution(new Size(640, 480))
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                Camera camera = (Camera) cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                cameraButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageCapture.takePicture(exec, new ImageCapture.OnImageCapturedCallback() {
                            @SuppressLint("UnsafeExperimentalUsageError")
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy image) {
                                super.onCaptureSuccess(image);
                                Bitmap bmp = imageProxyToBitmap(image);
                                image.close();
                                Intent imageIntent = new Intent(OCRCameraActivity.this, OCRExtractionActivity.class);
                                imageIntent.putExtra("image", bmp);
                                startActivity(imageIntent);
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                Log.d(TAG, "CAPTURE ERROR" + exception.toString());
                            }
                        });
                    }
                });


            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("OCR Camera");



    }
//    Bitmap bmp = imageProxyToBitmap(image);
//    Intent imageIntent = new Intent(OCRCameraActivity.this, OCRExtractionActivity.class);
//                        imageIntent.putExtra("image", bmp);
//    startActivity(imageIntent);
//    private void bindPreview(ProcessCameraProvider cameraProvider) {
//        Preview preview = new Preview.Builder()
//                .build();
//
//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build();
//
////        // Set up the capture use case to allow users to take photos
////        imageCapture = new ImageCapture.Builder()
////                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
////                .build();
//
////        imageAnalysis =
////                new ImageAnalysis.Builder()
////                        .setTargetResolution(new Size(640, 480))
////                        .build();
//
//        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//        Camera camera = (Camera) cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
//    }


    private void SendUserToMessagesActivityy()
    {
        Intent messagesIntent = new Intent(OCRCameraActivity.this, MessagesActivity.class);
        startActivity(messagesIntent);
    }

    private void SendUserToOCRCameraActivity()
    {
        Intent findDriversIntent = new Intent(OCRCameraActivity.this, FindDriversActivity.class);
        startActivity(findDriversIntent);
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length,null);
    }

//    public Bitmap imageToBitmap(Image image, float rotationDegrees) {
//
//        assert (image.getFormat() == ImageFormat.NV21);
//
//        // NV21 is a plane of 8 bit Y values followed by interleaved  Cb Cr
//        ByteBuffer ib = ByteBuffer.allocate(image.getHeight() * image.getWidth() * 2);
//
//        ByteBuffer y = image.getPlanes()[0].getBuffer();
//        ByteBuffer cr = image.getPlanes()[1].getBuffer();
//        ByteBuffer cb = image.getPlanes()[2].getBuffer();
//        ib.put(y);
//        ib.put(cb);
//        ib.put(cr);
//
//        YuvImage yuvImage = new YuvImage(ib.array(),
//                ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        yuvImage.compressToJpeg(new Rect(0, 0,
//                image.getWidth(), image.getHeight()), 50, out);
//        byte[] imageBytes = out.toByteArray();
//        Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//        Bitmap bitmap = bm;
//
//        // On android the camera rotation and the screen rotation
//        // are off by 90 degrees, so if you are capturing an image
//        // in "portrait" orientation, you'll need to rotate the image.
//        if (rotationDegrees != 0) {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(rotationDegrees);
//            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm,
//                    bm.getWidth(), bm.getHeight(), true);
//            bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
//                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
//        }
//        return bitmap;
//    }
}