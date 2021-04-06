package com.example.drivermanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class ScannedOrdersFragment extends Fragment implements OrderDialog.DialogListener {

    Activity listener;

    RecyclerView recyclerView;
    RecyclerScannedList recyclerScannedViewAdapter;

    private FirebaseAuth fAuth;
    private DatabaseReference OrderRef;

    private String currentDate, currentTime;
    TextView header;
    Button addOrders;
    String chosenAddress, address, orderNo, price, notes;
    List<String> scannedList;
    private Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    public ScannedOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OCRExtractionActivity){
            this.listener = (OCRExtractionActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public void onStart() {
        super.onStart();

//        Bundle getAddress = this.getArguments();
//        if(getAddress != null){
//            chosenAddress = getArguments().getString("ChosenAddress");
//            Log.d("testing", "Received from edit text fragment: "+chosenAddress);
//            scannedList.add(chosenAddress);
//            recyclerView.getAdapter().notifyDataSetChanged();
//            String listStr = scannedList.get(0);
//            Log.d("testing", "Received from edit text fragment: "+listStr);
//        }

//        addOrders.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openDialog();
//            }
//        });
    }

    @Override
    public void onPause() {
        super.onPause();
//        onSaveInstanceState();
    }

    private void openDialog() {
        OrderDialog orderDialog = new OrderDialog();
        orderDialog.show(getChildFragmentManager(), "Order Dialog");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scanned_orders, container, false);
        header = view.findViewById(R.id.scanned_heading);
        addOrders = view.findViewById(R.id.add_to_orders_button);
        scannedList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycleScannedOrdersList);
        recyclerScannedViewAdapter = new RecyclerScannedList(scannedList);
        recyclerView.setAdapter(recyclerScannedViewAdapter);

        //cloud initialisation
        fAuth = FirebaseAuth.getInstance();
        OrderRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        Bundle getAddress = this.getArguments();
        if(getAddress != null){
            chosenAddress = getArguments().getString("ChosenAddress");
            Log.d("testing", "Received from edit text fragment: "+chosenAddress);
            scannedList.add(chosenAddress);
            recyclerView.getAdapter().notifyDataSetChanged();
            String listStr = scannedList.get(0);
            Log.d("testing", "Received from edit text fragment: "+listStr);
        }
        final Animation animTranslate = AnimationUtils.loadAnimation(getContext(), R.anim.translate);

        addOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animTranslate);
                openDialog();
//                CreateNewOrder();
            }
        });

        bottomNavigationView = view.findViewById(R.id.bottom_nav_scanned);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.add_more_menu_item:
                        SendUserToOCRActivityy();
                        break;
                    case R.id.go_to_maps_menu_item:
                        SendUserToRoutesActivityy();
                        break;
                }
                return true;
            }
        });
        return view;

    }

    private void CreateNewOrder()
    {

        Log.d("TAG", "Create new order got called");

//        String orderKey = OrderRef.push().getKey();
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh-mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());

        String currentUserId = fAuth.getCurrentUser().getUid();
        OrderRef.child(currentUserId).child("Orders").child(currentDate).setValue("");
        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("Order Number", orderNo);
        orderMap.put("Address", address);
        orderMap.put("Price", price);
        orderMap.put("Order Notes", notes);

    }

    private void SendUserToOCRActivityy()
    {
        Intent addOCRIntent = new Intent(getContext(), OCRExtractionActivity.class);
        startActivity(addOCRIntent);
    }
    private void SendUserToRoutesActivityy()
    {
        Intent goToRoutes = new Intent(getContext(), RoutesActivity.class);
        startActivity(goToRoutes);
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
        }
    };

    @Override
    public void applyTexts(String addressDialog, String orderNoDialog, String priceDialog, String notesDialog) {
        address = addressDialog;
        orderNo = orderNoDialog;
        price = priceDialog;
        notes = notesDialog;
    }
}