package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class OCRExtractionActivity extends AppCompatActivity implements OrderDialog.DialogListener {

    private static final int PERMISSION_REQUEST_CODE = 200;
    //Recycler View save statekey
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;

    //define the navigation
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    private ImageView cameraImage;
    View myView;
    Bitmap bitmap;
    EditText editText;
    TextView ocrTextview;
    Button addToOrders, correct, retry;
    String textRecognitionresult, userClickedAddress;
    StringBuilder sb = new StringBuilder();
    String seperator = "";

    RecyclerView recyclerView;
    RecyclerScannedList recyclerScannedViewAdapter;
    List<String> scannedList;
    ArrayList<String> routeList;

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
        editText = findViewById(R.id.ocr_top_cardview_edit_text);
        correct = findViewById(R.id.correct_button);
        retry = findViewById(R.id.retry_button);
        scannedList = new ArrayList<>();
        routeList = new ArrayList<>();
        myView = (RelativeLayout) findViewById(R.id.extraction_main_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Text Extraction");

        recyclerView = findViewById(R.id.ocr_recyclerview);
        recyclerScannedViewAdapter = new RecyclerScannedList(scannedList);
        recyclerView.setAdapter(recyclerScannedViewAdapter);

        if (mBundleRecyclerViewState != null) {
            Parcelable listState= mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }

        //Recycler view line divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(OCRExtractionActivity.this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        //Recycler view list touch event listener
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
            //Animation for add to orders button
            final Animation animTranslate = AnimationUtils.loadAnimation(OCRExtractionActivity.this, R.anim.translate);

            addToOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animTranslate);

                //get size of list to loop through and add addresses to hashmaps
                int numberOfOrdersToAdd = recyclerScannedViewAdapter.getItemCount();

                //fill in addresses of maps if user has not filled in order details
                for(int i = 0; i < numberOfOrdersToAdd; i++) {
                    String listAddress = scannedList.get(i);
                    if((myHashMaps.get(String.valueOf(i)).get("Address")) == null) {
                        myHashMaps.get(String.valueOf(i)).put("Address", listAddress);
                    }
                }
                Log.d("testing", "Scanned list size: "+numberOfOrdersToAdd);

                //Call method to create new order in firebase database
                CreateNewOrder(numberOfOrdersToAdd);
                Log.d("testing", "User selected add to orders button, calling createNewOrder in OCR activity");
            }
        });
        //Firebase database initialisation
        fAuth = FirebaseAuth.getInstance();
        OrderRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        //Order details hashmaps for firebase
        HashMap<String, String> map0 = new HashMap<>();
        HashMap<String, String> map1 = new HashMap<>();
        HashMap<String, String> map2 = new HashMap<>();
        HashMap<String, String> map3 = new HashMap<>();
        HashMap<String, String> map4 = new HashMap<>();
        //parent hashmap that stores reference to hashmaps
        //that correspond to the position of the address in the Scanned list
        myHashMaps.put("0", map0);
        myHashMaps.put("1", map1);
        myHashMaps.put("2", map2);
        myHashMaps.put("3", map3);
        myHashMaps.put("4", map4);

        //on activity created check for camera permissions
        if(hasCameraPermission()) {
            captureImageIntent();
        }else{
            requestPermission();
        }

        //navigation bar click event listeners
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.add_more_menu_item:
                        ReopenCamera();
                        break;
                    case R.id.go_to_maps_menu_item:
                        GoToMaps();
                        break;
                }
                return true;
            }
        });

        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add address scanned to scanned lists and update recycler view
                scannedList.add(editText.getText().toString());
                recyclerScannedViewAdapter.notifyDataSetChanged();
                //clear the text in the edit text
                editText.setText("");
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReopenCamera();
                editText.setText("");
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Save state of list of addresses
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //restore recycler view state of list of addresses
        if (mBundleRecyclerViewState != null) {
            Parcelable listState= mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //save state of list
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    private void GoToMaps() {
        //get latlng of addresses
        Log.d("testing", "Go To Maps clicked");
        if(scannedList.size() != 0) {
            Log.d("testing", "Staring Async task to get geo locations of each address");
                //Call Async task to download latlngs from geocoder on separate UI thread
                GetLatLngs task = new GetLatLngs();
                task.execute();
        }else{
            Toast.makeText(OCRExtractionActivity.this, "There is no addresses in list, please scan addresses or go to route finder to manually find add routes", Toast.LENGTH_SHORT).show();
        }
    }
    //method to open camera
    private void ReopenCamera() {
        editText.setText("");
        captureImageIntent();
    }

    //Gets the image taken from camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //get saved state of list on resume from camera intent
        if (mBundleRecyclerViewState != null) {
            Parcelable listState= mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
        //Ensure result is an image from the crop image feature
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d("testing", "Received cropped image from camera check 1");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Log.d("testing", "Received cropped image from camera check 2");
                Uri resultUri = result.getUri();//get uri of image returned
                Log.d("testing", "Attempting to convert uri to bitmap");
                //Set hidden image to cropped image returned
                cameraImage.setImageURI(resultUri);
                //Convert image uri to bitmap
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Convert bitmap to Image
                InputImage image = InputImage.fromBitmap(bitmap, (int) cameraImage.getRotation());

                //Passing image to firebase ML to extract text
                TextRecognizer recognizer = TextRecognition.getClient();
                Task<Text> resultML = recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
                                Log.d("testing", "Received from image text extraction: "+text.getText());
                                //get each block of text returned
                                sb.delete(0, sb.length());
                                seperator="";
                                for(Text.TextBlock block : text.getTextBlocks()){

                                    for(Text.Line line : block.getLines()){
                                        //Form String of full address scanned
                                        sb.append(seperator + line.getText());
                                        seperator = ", ";
                                    }
                                }

                                textRecognitionresult = sb.toString();
                                Log.d("testing", "StringBuilder result: "+sb.toString());
                                Log.d("testing", "Sending data through interface");
                                //Set text in Cardview for user to validate
                                editText.setText(textRecognitionresult);
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
        //Result returned from camera
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Log.d("testing", "Received image from camera");
            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap bmp = (Bitmap) extras.get("data");
            assert bmp != null;
            //Convert bitmap to URI
            Uri imageUri = getImageUri(OCRExtractionActivity.this, bmp);
            //pass converted camera image uri to cropped image activity where user can crop out the area of address
            CropImage.activity(imageUri)
                    .setRequestedSize(480, 640)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
            Toast.makeText(OCRExtractionActivity.this, "Move box over Address only", Toast.LENGTH_LONG).show();

        }
    }

    //Order form dialog user can fill in when they click on item in list of addresses that will be used to fill in the
    //respective addresses hashmap to be uploaded to users orders firebase database
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
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        Toast.makeText(OCRExtractionActivity.this, "Capture image of invoice address area", Toast.LENGTH_LONG).show();
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

    //Receiving back order details from order dialog interface
    @Override
    public void applyTexts(int listPosition3, String address, String orderNo, String price, String notes, String deliveryCharge, String name, String companyName) {
        Log.d("TAG", "Received data from order dialog");
        //position is the item number in list user clicked on
        // used to identify the correct address and hashmap to insert values into
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

    public void CreateNewOrder(int noOFOrdersToAdd)
    {

        Log.d("TAG", "Create New Order called: " + noOFOrdersToAdd);
        for(int i = 0; i < noOFOrdersToAdd; i++) {
            //Get number of orders to process and the respective hashmap with the order details
            //and pass to firebase method
            CreateNewOrderDBEntry(myHashMaps.get(String.valueOf(i)));
            Log.d("TAG", "Processing order " + i+ ", passing to CreateNewEntryInDB");

        }
    }

    public void CreateNewOrderDBEntry(HashMap map) {

            Log.d("TAG", "Create new order got called");

            //Get Current Date
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            //Get current time
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh-mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            String currentUserId = fAuth.getCurrentUser().getUid();

                OrderRef.child(currentUserId).child("Orders").child(currentDate).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //We will use this to increment the order number in the DB
                        noOFOrders = snapshot.getChildrenCount();
                        Log.d("testing", "Number of orders currently in database: " + noOFOrders);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
                int noOFOrdersd = Integer.parseInt(String.valueOf(noOFOrders));
                noOFOrdersd = noOFOrdersd+1;
                OrderRef.child(currentUserId).child("Orders").child(currentDate).child(String.valueOf(noOFOrdersd)).setValue(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(OCRExtractionActivity.this, "Successfully uploaded order details to Order Database", Toast.LENGTH_SHORT).show();
                        Log.d("testing", "Successfully added orders to DB");

                    }
                }
            });
    }
    //swipe list item to delete
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
            Log.d("TAG", "Size of list after deletion scannedList in ocr activity: "+scannedList.size());
        }
    };

    //Async task method to get latlngs of eazch address in list
    private class GetLatLngs extends AsyncTask<Void, Void, ArrayList<String>>{
    ProgressDialog dialog;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(OCRExtractionActivity.this);
        dialog.setTitle("Calculating routes");
        dialog.setMessage("please wait...");
        dialog.setIndeterminate(true);
        dialog.show();
    }

    @Override
    protected ArrayList doInBackground(Void... addresses) {
        Log.d("testing", "GETLATLNGS called - processing request");
        LatLng returnedLatLng = new LatLng(53.3498, -6.266155);

        try{
            Log.d("testing", "Attempting to connect to geo API");
            for(int i = 0; i < scannedList.size(); i++) {
                String addr = scannedList.get(i);
                Log.d("testing", "Address to process: " + addr);
                returnedLatLng = getAddressLatLngs(addr);
                if (returnedLatLng.latitude == 53.3498 && returnedLatLng.longitude == -6.266155) {
                    Toast.makeText(OCRExtractionActivity.this, "Could not find coordinates for address: " + addr + ", Please enter manually in route finder", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("testing", "LatLng returned for address at postion " + i + " in Scanned List is " + returnedLatLng);
                    Log.d("testing", "Adding " + addr + " and its Lat(" + returnedLatLng.latitude + ") and its Lng(" + returnedLatLng.longitude + ") to routes list");
                    routeList.add(scannedList.get(i) + "," + returnedLatLng.latitude + "," + returnedLatLng.longitude);
                    //Now we have list with address and latlngs
                }
            }
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return routeList;
    }

    @Override
    protected void onPostExecute(ArrayList routes) {
        super.onPostExecute(routes);
        Intent routesIntent = new Intent(OCRExtractionActivity.this, RoutesActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("listOfAddress", (Serializable) routes);
        routesIntent.putExtra("BUNDLE", args);
        startActivity(routesIntent);
        dialog.dismiss();
        routeList.clear();
    }
}

    private LatLng getAddressLatLngs(String strAddress){
        Log.d("testing", "Geocoder called - processing API request"+strAddress);
        Geocoder geocoder = new Geocoder(this);
        List<Address> address;
        try{
            address = geocoder.getFromLocationName(strAddress, 1);
            Log.d("testing", "Returned from geocoder: "+address.get(0));
            if(address == null && address.size() > 0){
                Toast.makeText(OCRExtractionActivity.this, "No coordinates returned for "+strAddress+", please add address manually in route finder to add to your route", Toast.LENGTH_SHORT).show();
                Log.d("testing", "No address found for "+strAddress);
                return new LatLng(53.3498, -6.266155);
            }
            Address location = address.get(0);
            return new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
            return new LatLng(53.3498, -6.266155);
        }
    }


}