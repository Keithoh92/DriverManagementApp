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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class OCRExtractionActivity extends AppCompatActivity implements OrderDialog.DialogListener, EditTextFragment.FragmentTActivity2 {

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
    TextView ocrTextview;
    Button addToOrders;
    String textRecognitionresult, userClickedAddress, chosenAddress;
    StringBuilder sb = new StringBuilder();
    String seperator = "";

    RecyclerView recyclerView;
    RecyclerScannedList recyclerScannedViewAdapter;
    List<String> scannedList;




    //Order Database
    private FirebaseAuth fAuth;
    private DatabaseReference OrderRef;
    HashMap<String, HashMap<String, String>> myHashMaps = new HashMap<String, HashMap<String, String>>();
    long noOFOrders = 0;
    int noOFOrdersToAdd;
    String currentDate, currentTime;
    int listPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_c_r_extraction);
        bottomNavigationView = findViewById(R.id.bottom_nav_extract);
        toolbar = findViewById(R.id.toolbar_extract);
        cameraImage = findViewById(R.id.camera_image_view);
        ocrTextview = findViewById(R.id.ocr_textview);
        addToOrders = findViewById(R.id.add_to_orders_button);
        scannedList = new ArrayList<>();
        myView = (RelativeLayout) findViewById(R.id.extraction_main_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Text Extraction");
        etf = new EditTextFragment();

        recyclerView = findViewById(R.id.ocr_recyclerview);
        recyclerScannedViewAdapter = new RecyclerScannedList(scannedList);
        recyclerView.setAdapter(recyclerScannedViewAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(OCRExtractionActivity.this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);



        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        recyclerScannedViewAdapter.setClickListener(new RecyclerScannedList.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                listPosition = position;
                Log.d("TAG", "User clicked on item at position: "+listPosition);
                userClickedAddress = scannedList.get(listPosition);
                Log.d("testing", "Address at item position "+listPosition+": " +userClickedAddress);
                openDialog();
                Log.d("testing", "Item position from method in on createview: " +listPosition);
            }
        });

            final Animation animTranslate = AnimationUtils.loadAnimation(OCRExtractionActivity.this, R.anim.translate);

            addToOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animTranslate);

                int numberOfOrdersToAdd = recyclerScannedViewAdapter.getItemCount();
                //fill in addresses of maps
                for(int i = 0; i < numberOfOrdersToAdd; i++) {
                    String listAddress = scannedList.get(i);
                    if((myHashMaps.get(String.valueOf(i)).get("Address")) == null) {
                        myHashMaps.get(String.valueOf(i)).put("Address", listAddress);
                    }
                }



                Log.d("testing", "Scanned list size: "+numberOfOrdersToAdd);

                CreateNewOrder(numberOfOrdersToAdd);
                Log.d("testing", "User selected add to orders button, calling createNewOrder in OCR activity");
//                }else{
//                    Toast.makeText(getContext(), "There is no items in the list to add to orders, Scan some orders first", Toast.LENGTH_SHORT).show();
//                }
//                openDialog();
//                CreateNewOrder();
            }
        });

        fAuth = FirebaseAuth.getInstance();
        OrderRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        HashMap<String, String> map0 = new HashMap<>();
        HashMap<String, String> map1 = new HashMap<>();
        HashMap<String, String> map2 = new HashMap<>();
        HashMap<String, String> map3 = new HashMap<>();
        HashMap<String, String> map4 = new HashMap<>();
        myHashMaps.put("0", map0);
        myHashMaps.put("1", map1);
        myHashMaps.put("2", map2);
        myHashMaps.put("3", map3);
        myHashMaps.put("4", map4);

        myFrag = fm.findFragmentById(R.id.edit_text_fragment);

        assert myFrag != null;
        fm.beginTransaction()
                .hide(myFrag)
                .commit();


        if(hasCameraPermission()) {
            captureImageIntent();
            Toast.makeText(OCRExtractionActivity.this, "Capture image of invoice address area", Toast.LENGTH_LONG).show();
        }else{
            requestPermission();
        }

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
                                Log.d("testing", "Received from image text extraction: "+text.getText());

                                for(Text.TextBlock block : text.getTextBlocks()){

                                    for(Text.Line line : block.getLines()){
                                        sb.append(seperator + line.getText());
                                        seperator = ", ";
                                    }
                                }

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

                                Log.d("testing", "Sending text extraction to edit_text fragment");
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

    private void openDialog() {
        OrderDialog orderDialog = new OrderDialog();
        Bundle dialogBundle = new Bundle();
        dialogBundle.putString("Dialog Address", userClickedAddress);
        dialogBundle.putInt("List position", listPosition);
        orderDialog.setArguments(dialogBundle);
        Log.d("TAG", "Setting dialog address field to the address selected by user: "+userClickedAddress);
        orderDialog.show(getSupportFragmentManager(), "Order Dialog");
    }

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

    @Override
    public void applyTexts(int listPosition3, String address, String orderNo, String price, String notes, String deliveryCharge, String name, String companyName) {
        Log.d("TAG", "Received data from order dialog");
        String position = String.valueOf(listPosition3);
        myHashMaps.get(position).put("Address", address);
        myHashMaps.get(position).put("Recipients Name", name);
        myHashMaps.get(position).put("Order Number", orderNo);
        myHashMaps.get(position).put("Price", price);
        myHashMaps.get(position).put("Delivery Charge", deliveryCharge);
        myHashMaps.get(position).put("Company Name", companyName);
        myHashMaps.get(position).put("Order Notes", notes);
        myHashMaps.get(position).put("Time Entered", currentTime);
        Log.d("TAG", "Updating Hashmap at position: "+position);
    }


    @Override
    public void passData(String chosenAddress) {
        scannedList.add(chosenAddress);
        recyclerScannedViewAdapter.notifyDataSetChanged();
//        int scannedListSize = scannedList.size();

//        recyclerView.
    }

    public interface ActivityToFragment{
        public void communicate(String textRecog);
    }
    private void sendData(String textRecog){
        mCallback.communicate(textRecog);
    }

    public void CreateNewOrder(int noOFOrdersToAdd)
    {

        Log.d("TAG", "Create New Order called: " + noOFOrdersToAdd);
        for(int i = 0; i < noOFOrdersToAdd; i++) {
            CreateNewOrderDBEntry(myHashMaps.get(String.valueOf(i)));
            Log.d("TAG", "Processing order " + i+ ", passing to CreateNewEntryInDB");

        }
    }

    public void CreateNewOrderDBEntry(HashMap map) {

            Log.d("TAG", "Create new order got called");

//        String orderKey = OrderRef.push().getKey();
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh-mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            String currentUserId = fAuth.getCurrentUser().getUid();

            //Database nodes - users{ date { order info
//            DatabaseReference newOrder = OrderRef.child(currentUserId).child("Orders").child(currentDate);

                OrderRef.child(currentUserId).child("Orders").child(currentDate).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        noOFOrders = snapshot.getChildrenCount();
                        Log.d("testing", "Number of orders currently in database: " + noOFOrders);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            int noOfOrdersInt = (int) noOFOrders;
            noOfOrdersInt = noOfOrdersInt + 1;

                OrderRef.child(currentUserId).child("Orders").child(currentDate).child(String.valueOf(noOFOrders+1)).setValue(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(OCRExtractionActivity.this, "Successfully uploaded order details to Order Database", Toast.LENGTH_SHORT).show();
                        Log.d("testing", "Successfully added orders to DB");

                    }
                }
            });
    }
    //swipe to delete
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            scannedList.remove(position);
            recyclerView.getAdapter().notifyItemRemoved(position);
            Log.d("TAG", "User Removed Item From List");
        }
    };
//
//    @Override
//    protected void onStop() {
//        mCallback = null;
//        super.onStop();
//    }
}