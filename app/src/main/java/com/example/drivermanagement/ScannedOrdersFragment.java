package com.example.drivermanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*

    NOT USED - CHANGED FOR ACTIVITY

 */
public class ScannedOrdersFragment extends Fragment implements OrderDialog.DialogListener, RecyclerScannedList.ItemClickListener {

    Activity listener;

    RecyclerView recyclerView;
    RecyclerScannedList recyclerScannedViewAdapter;

//    private FirebaseAuth fAuth;
//    private DatabaseReference OrderRef;

    TextView header;
    Button addOrders;
    String userClickedAddress;
    String chosenAddress, currentDate, currentTime, dialogAddress, orderNumber, orderPrice, orderNotes, orderDeliveryCharge, orderName, orderCompanyName;
    List<String> scannedList;
//    HashMap<String, String> map0, map1, map2, map3, map4;
//    HashMap<String, HashMap<String, String>> myHashMaps = new HashMap<String, HashMap<String, String>>();
    long noOFOrders = 0;
    int listPosition, listPosition2;
    int numberOfOrdersToAdd;
    private ScannedListItemCount sCallback;
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

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(), "Swipe on list items to remove", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void openDialog() {
        OrderDialog orderDialog = new OrderDialog();
//        orderDialog.editAddress.setText(userClickedAddress);
        Bundle dialogBundle = new Bundle();
        dialogBundle.putString("Dialog Address", userClickedAddress);
        dialogBundle.putInt("List position", listPosition);
        orderDialog.setArguments(dialogBundle);
        Log.d("TAG", "Setting dialog address field to the address selected by user: "+userClickedAddress);
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

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
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

        final Animation animTranslate = AnimationUtils.loadAnimation(getContext(), R.anim.translate);

        addOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animTranslate);

                int numberOfOrdersToAdd = recyclerScannedViewAdapter.getItemCount();
                Log.d("testing", "Scanned list size: "+numberOfOrdersToAdd);

                ((OCRExtractionActivity)getActivity()).CreateNewOrder(numberOfOrdersToAdd);
                Log.d("testing", "User selected add to orders button, calling createNewOrder in OCR activity");
//                }else{
//                    Toast.makeText(getContext(), "There is no items in the list to add to orders, Scan some orders first", Toast.LENGTH_SHORT).show();
//                }
//                openDialog();
//                CreateNewOrder();
            }
        });

        Bundle getAddress = this.getArguments();
        if(getAddress != null){
            chosenAddress = getArguments().getString("ChosenAddress");
            Log.d("testing", "Received from edit text fragment: "+chosenAddress);
            scannedList.add(chosenAddress);
//            numberOfOrdersToAdd = scannedList.size();
//            sendData(numberOfOrdersToAdd);
            Log.d("testing", "List size after eceiving from bundle before notifying data set changed: "+scannedList.size());
            recyclerScannedViewAdapter.notifyDataSetChanged();
            Log.d("testing", "List size after receiving address from bundle and notifying data set changed: "+recyclerScannedViewAdapter.getItemCount());

            String listStr = scannedList.get(0);
            Log.d("testing", "Received from edit text fragment: "+listStr);
        }

        return view;
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
            Log.d("TAG", "User Removed Item From List");
        }
    };


//    @Override
//    public void applyTexts(int listPosition3, String address, String orderNo, String price, String notes, String deliveryCharge, String name, String companyName) {
//        Log.d("TAG", "Received data from order dialog");
//        String position = String.valueOf(listPosition3);
//        //listPosiiton = 0,
//
//        dialogAddress = address;
//        orderNumber = orderNo;
//        orderPrice = price;
//        orderNotes = notes;
//        orderDeliveryCharge = deliveryCharge;
//        orderName = name;
//        orderCompanyName = companyName;
//        //we get that hashmap from the list of hashmaps
//        myHashMaps.get(position).put("Address", address);
//        myHashMaps.get(position).put("Recipients Name", name);
//        myHashMaps.get(position).put("Order Number", orderNo);
//        myHashMaps.get(position).put("Price", price);
//        myHashMaps.get(position).put("Delivery Charge", deliveryCharge);
//        myHashMaps.get(position).put("Company Name", companyName);
//        myHashMaps.get(position).put("Order Notes", notes);
//        myHashMaps.get(position).put("Time Entered", currentTime);
//        Log.d("TAG", "Updating Hashmap at position: "+position);
//
//
//    }


    @Override
    public void onItemClick(View view, int position) {
        listPosition2 = position;
        Log.d("testing", "Item position from method in on interface: " +listPosition);

    }



    @Override
    public void applyTexts(int listPosition3, String address, String orderNo, String price, String notes, String deliveryCharge, String name, String companyName) {
        Log.d("TAG", "Received data from order dialog");
//        String position = String.valueOf(listPosition3);
//        myHashMaps.get(position).put("Address", address);
//        myHashMaps.get(position).put("Recipients Name", name);
//        myHashMaps.get(position).put("Order Number", orderNo);
//        myHashMaps.get(position).put("Price", price);
//        myHashMaps.get(position).put("Delivery Charge", deliveryCharge);
//        myHashMaps.get(position).put("Company Name", companyName);
//        myHashMaps.get(position).put("Order Notes", notes);
//        myHashMaps.get(position).put("Time Entered", currentTime);
//        Log.d("TAG", "Updating Hashmap at position: "+position);
    }

    public interface ScannedListItemCount{
        public void communicate(int orderCount);
    }

    private void sendData(int orderCount){ sCallback.communicate(orderCount);}

    @Override
    public void onDetach() {
        super.onDetach();
    }
}