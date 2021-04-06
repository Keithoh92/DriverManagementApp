package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.DrawableContainer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

public class OCRExtractionActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private ImageView cameraImage;
    private ActivityToFragment mCallback;
    View myView, fragmentsView;
    Bitmap bitmap;
    FragmentManager fm = getSupportFragmentManager();
    Fragment myFrag, scannedFrag;
    public EditTextFragment etf;
    public ScannedOrdersFragment sof;
    EditText editText;

    String textRecognitionresult;
    StringBuilder sb = new StringBuilder();
    String seperator = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_c_r_extraction);
        bottomNavigationView = findViewById(R.id.bottom_nav_extract);
        toolbar = findViewById(R.id.toolbar_extract);
        cameraImage = findViewById(R.id.camera_image_view);
        myView = (RelativeLayout) findViewById(R.id.extraction_main_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Text Extraction");
        etf = new EditTextFragment();



        myFrag = fm.findFragmentById(R.id.edit_text_fragment);
        scannedFrag = fm.findFragmentById(R.id.scanned_orders_frag);


        assert myFrag != null;
        fm.beginTransaction()
                .hide(myFrag)
                .hide(scannedFrag)
                .commit();

//        editText = fm.getView().findViewById(R.id.edit_text);


        if(hasCameraPermission()) {
            captureImageIntent();
            Toast.makeText(OCRExtractionActivity.this, "Capture image of invoice address area", Toast.LENGTH_LONG).show();
        }else{
            requestPermission();
        }

//        Intent intent = getIntent();
//        Bitmap bmp = (Bitmap) intent.getParcelableExtra("image");
////        cameraImage.setImageBitmap(bmp);
//            Uri imageUri = getImageUri(OCRExtractionActivity.this, bmp);
//            CropImage.activity(imageUri)
//                    .setAspectRatio(1, 1)
//                    .start(this);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d("testing", "Received cropped image from camera check 1");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Log.d("testing", "Received cropped image from camera check 2");
                Uri resultUri = result.getUri();
                Log.d("testing", "Attempting to convert uri to bitmap");
                cameraImage.setImageURI(resultUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputImage image = InputImage.fromBitmap(bitmap, (int) cameraImage.getRotation());

                TextRecognizer recognizer = TextRecognition.getClient();
                Task<Text> resultML = recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
//                                String textRecognitionresult = text.getText();
                                Log.d("testing", "Received from image text extraction: "+text.getText());
//                                BufferedReader bf = new BufferedReader(new StringReader(text.getText()));

//                                    String lineReader = bf.readLine();
//                                    while(lineReader != null) {
//                                        sb.append(seperator + lineReader + ", ");
//                                        seperator = ",";
//                                    }

                                for(Text.TextBlock block : text.getTextBlocks()){
//                                    String blockText = block.getText();
//                                    Point[] blockCornerpoints = block.getCornerPoints();
//                                    Rect blockFrame = block.getBoundingBox();
                                    for(Text.Line line : block.getLines()){
                                        sb.append(seperator + line.getText());
                                        seperator = ", ";
                                    }
                                }
//                                sendData(sb.toString());

                                textRecognitionresult = sb.toString();
                                Log.d("testing", "StringBuilder result: "+sb.toString());
                                Log.d("testing", "Sending data through interface");

                                Bundle extras = new Bundle();
                                extras.putString("textRecognitionResult", textRecognitionresult);


                                assert  etf != null;
                                etf.setArguments(extras);


//                                getSupportFragmentManager().setFragmentResult()
                                fm.beginTransaction()
                                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                                        .replace(R.id.edit_text_fragment, etf)
                                        .show(myFrag)
                                        .commit();

//                                sendData(textRecognitionresult);

//                                etf.textRecognitionResultFragment = textRecognitionresult;
//                                editText.setText(textRecognitionresult);
                                Log.d("testing", "Sending text extraction to edit_text fragment");
//                                cameraImage.setAlpha((float) 0.5);



//                                getSupportFragmentManager().beginTransaction()
//                                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                                        .show(myFrag)
//                                        .commit();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(OCRExtractionActivity.this, "Failed to recognise text in that image please try again", Toast.LENGTH_LONG).show();
                            }
                        });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("testing", "Failed to get cropped image");
            }
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Log.d("testing", "Received image from camera");
            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap bmp = (Bitmap) extras.get("data");
            assert bmp != null;
            Uri imageUri = getImageUri(OCRExtractionActivity.this, bmp);
            CropImage.activity(imageUri)
                    .setRequestedSize(480, 640)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
            Toast.makeText(OCRExtractionActivity.this, "Move box over Address only", Toast.LENGTH_LONG).show();
            //Here we need to process the text in the bounding box and extract the result
            //Need a fragment that appears with the text extracted and a textarea
        }
    }

//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT)
//        {
//
//        }else if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE)
//        {
//
//        }
//    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Camera Image", null);
        return Uri.parse(path);
    }

    static final int REQUEST_IMAGE_CAPTURE = 222;
    private void captureImageIntent(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(OCRExtractionActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

//    @Override
//    public String getExtractedText() {
//        return textRecognitionresult;
//    }
//
    public interface ActivityToFragment{
        public void communicate(String textRecog);
    }
    private void sendData(String textRecog){
        mCallback.communicate(textRecog);
    }
//
//    @Override
//    protected void onStop() {
//        mCallback = null;
//        super.onStop();
//    }
}