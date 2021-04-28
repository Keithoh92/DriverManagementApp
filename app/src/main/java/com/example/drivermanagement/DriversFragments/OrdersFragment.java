package com.example.drivermanagement.DriversFragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.drivermanagement.DriversDashboardActivity;
import com.example.drivermanagement.OrderDialog;
import com.example.drivermanagement.Orders;
import com.example.drivermanagement.R;
import com.example.drivermanagement.TinyDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/*
THIS FRAGMENT IS CAUSING A LOT OF PROBLEMS ON THE DRIVERS DASHBOARD
HOWEVER, THIS IS WHERE THE DATES OF THE ORDERS ARE LOADED INTO THE DROPDOWN
AND THE USER CAN SELECT THE ORDERS FRO THAT DAY AND EDIT THEM AS THEY WISH
 */


public class OrdersFragment extends Fragment {

    Activity listener;
    RecyclerView ordersListView;
    RecyclerOrdersList recyclerOrdersList;
    Button addOrders;

    List<String> listOfOrders = new ArrayList<>();
    List<String> dateList = new ArrayList<>();
    ValueEventListener ordersListener, ordersListener2;
    ChildEventListener ordersListenerChild;
    private boolean isInForeground;

    ArrayAdapter adapter;
    Spinner spinner;
    Orders ordersClass;

    private DatabaseReference UsersRef, OrdersRef, DatesRef;
    private FirebaseAuth fAuth;
    FirebaseUser currentUser;
    Query query;
    String userID, noOfOrders, dateAdded, companyName, deliveryCharge, orderNotes, orderNumber, price, recipientsName, address, dateSelected, listOfOrdersString;




    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof DriversDashboardActivity){
            this.listener = (DriversDashboardActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addOrders = view.findViewById(R.id.add_order_button);
        ordersListView = view.findViewById(R.id.orders_recyclerview2);
        Spinner spinner = view.findViewById(R.id.orders_spinner);


        fAuth = FirebaseAuth.getInstance();

        currentUser = fAuth.getCurrentUser();
        assert currentUser != null;
        userID = currentUser.getUid();

        UsersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        DatesRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        OrdersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        recyclerOrdersList = new RecyclerOrdersList(listOfOrders);
        ordersListView.setAdapter(recyclerOrdersList);

        //Fill the dates dropdown with the saved dateslist that is retrieved from firebase
        TinyDB tinyDB = new TinyDB(getContext());
        if(!tinyDB.getListString("DatesList").isEmpty()) {
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, tinyDB.getListString("DatesList"));
        }else{
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, dateList);
        }
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //On date selected save to date slected variable
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listOfOrders.clear();
                dateSelected = parent.getItemAtPosition(position).toString();
                Log.d("OrdersFrag", "Date Selected: "+dateSelected);
                ordersListenerChild = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(snapshot.exists()){
                            String address = snapshot.child("Address").getValue().toString();
                            listOfOrders.add(address);
                            recyclerOrdersList.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                UsersRef.child(userID).child("Orders").child(dateSelected).addChildEventListener(ordersListenerChild);
  }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        GetOrders();

        //ON ORDER SELECTED, OPEN DIALOG AND FILL ORDER DETAILS WITH THE ORDER SELECTED DETAILS
        recyclerOrdersList.setClickListener(new RecyclerOrdersList.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                listOfOrdersString = listOfOrders.get(position);
                List<String> orderDetails = new ArrayList<>();
                query = OrdersRef.child("Users").child(userID).child("Orders").child(dateSelected).orderByChild("Address").equalTo(listOfOrdersString);

                Log.d("OrdersFrag", "Chosen address: " + listOfOrdersString);
                if (isInForeground) {
                    ordersListener2 = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dss : snapshot.getChildren()) {
                                    Log.d("OrdersFrag", "order no. in DB: " + dss.getKey());
                                    noOfOrders = dss.getKey();
                                    address = dss.child("Address").getValue().toString();
                                    if (dss.hasChild("Company Name")) {
                                        companyName = dss.child("Company Name").getValue().toString();
                                    }
                                    if (dss.hasChild("Delivery Charge")) {
                                        deliveryCharge = dss.child("Delivery Charge").getValue().toString();
                                    }
                                    if (dss.hasChild("Order Notes")) {
                                        orderNotes = dss.child("Order Notes").getValue().toString();
                                    }
                                    if (dss.hasChild("Order Number")) {
                                        orderNumber = dss.child("Order Number").getValue().toString();
                                    }
                                    if (dss.hasChild("Price")) {
                                        price = dss.child("Price").getValue().toString();
                                    }
                                    if (dss.hasChild("Recipients Name")) {
                                        recipientsName = dss.child("Recipients Name").getValue().toString();
                                    }
                                }
                                //CODE TO PASS SELECTED ORDER DETAILS AND OPEN EDIT ORDER DIALOG
                                EditOrderDialog editOrderDialog = new EditOrderDialog();
                                Bundle editOrderBundle = new Bundle();
                                editOrderBundle.putString("Address", address);
                                editOrderBundle.putString("NoOFOrders", noOfOrders);
                                editOrderBundle.putString("DateSelected", dateSelected);
                                if (companyName != null) {
                                    editOrderBundle.putString("CompanyName", companyName);
                                }
                                if (deliveryCharge != null) {
                                    editOrderBundle.putString("DeliveryCharge", deliveryCharge);
                                }
                                if (orderNotes != null) {
                                    editOrderBundle.putString("OrderNotes", orderNotes);
                                }
                                if (orderNumber != null) {
                                    editOrderBundle.putString("OrderNumber", orderNumber);
                                }
                                if (price != null) {
                                    editOrderBundle.putString("Price", price);
                                }
                                if (recipientsName != null) {
                                    editOrderBundle.putString("RecipientsName", recipientsName);
                                }
                                editOrderDialog.setArguments(editOrderBundle);
                                editOrderDialog.show(getChildFragmentManager(), "Edit Order Dialog");
                                noOfOrders = "";
                                recipientsName = "";
                                companyName = "";
                                deliveryCharge = "";
                                orderNotes = "";
                                orderNumber = "";
                                price = "";
                                recipientsName = "";
                            } else {
                                Log.d("OrdersFrag", "No snapshot found");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("OrdersFrag", error.getMessage());
                        }
                    };
                    query.addListenerForSingleValueEvent(ordersListener2);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        isInForeground = true;

    }

    //
    private void GetOrders()
    {
        Log.d("OrdersFragment", "Get Orders Called");
        ordersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    TinyDB tinyDb = new TinyDB(getContext());
                    ArrayList<String> tempListOfDates = new ArrayList<>();
                    tempListOfDates.clear();
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        dateAdded = ds.getKey();

                        tempListOfDates.add(dateAdded);
                        tinyDb.putListString("DatesList", tempListOfDates);
                    }
                    if(!tinyDb.getListString("DatesList").isEmpty()){
                        dateList.clear();
                        dateList.addAll(tinyDb.getListString("DatesList"));
                        recyclerOrdersList.notifyDataSetChanged();
                    }
//                    UsersRef.child(userID).child("Orders").orderByChild("Address").removeEventListener(ordersListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        DatesRef.child("Users").child(userID).child("Orders").orderByChild("Address").addValueEventListener(ordersListener);
        for(int i = 0; i < dateList.size(); i++){
            Log.d("OrdersFragment", "dateList elements: "+dateList.get(i));
        }

    }

    //ON PAUSE REMOVE ALL THE FIREBASE LISTENERS
    @Override
    public void onPause() {
        super.onPause();
        isInForeground = false;
        dateList.clear();
        UsersRef.child(userID).child("Orders").orderByChild("Address").removeEventListener(ordersListener);
        UsersRef.child(userID).child("Orders").child(dateSelected).removeEventListener(ordersListenerChild);
        DatesRef.child("Users").child(userID).child("Orders").orderByChild("Address").removeEventListener(ordersListener);
        query.removeEventListener(ordersListener2);
//        OrdersRef.child("Users").child(userID).child("Orders").child(dateSelected).orderByChild("Address").equalTo(listOfOrdersString).removeEventListener(ordersListener2);
    }
}