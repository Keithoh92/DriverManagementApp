package com.example.drivermanagement.DriversFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.drivermanagement.DriversDashboardActivity;
import com.example.drivermanagement.OCRExtractionActivity;
import com.example.drivermanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EditOrderDialog extends AppCompatDialogFragment {

    private String currentDate, currentTime;
    private DatabaseReference OrdersRef;
    private FirebaseAuth fAuth;
    FirebaseUser currentUser;
    String userID, addr;


    EditText editAddress, editOrderNo, editPrice, editNotes, editDeliveryCharge, editName, editCompanyName;
    long noOFOrders = 0;
    int listPosition;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_orders_dialog, null);

        fAuth = FirebaseAuth.getInstance();

        currentUser = fAuth.getCurrentUser();
        assert currentUser != null;
        userID = currentUser.getUid();

        OrdersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        //Get current time
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh-mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());

        Bundle fromOrdersFrag = getArguments();
        assert fromOrdersFrag != null;
        addr = fromOrdersFrag.getString("Address");
        String noOfOrders = fromOrdersFrag.getString("NoOFOrders");
        String DateSelected = fromOrdersFrag.getString("DateSelected");
        String coName = fromOrdersFrag.getString("CompanyName");
        String delCharge = fromOrdersFrag.getString("DeliveryCharge");
        String ordNotes = fromOrdersFrag.getString("OrderNotes");
        String ordNumber = fromOrdersFrag.getString("OrderNumber");
        String price = fromOrdersFrag.getString("Price");
        String recName = fromOrdersFrag.getString("RecipientsName");


        editAddress = view.findViewById(R.id.edit_address_edit_orders);
        editOrderNo = view.findViewById(R.id.order_no_edit_edit_orders);
        editPrice = view.findViewById(R.id.order_price_edit_edit_orders);
        editNotes = view.findViewById(R.id.order_notes_edit_edit_orders);
        editDeliveryCharge = view.findViewById(R.id.order_delivery_edit_edit_orders);
        editName = view.findViewById(R.id.edit_name_edit_orders);
        editCompanyName = view.findViewById(R.id.edit_company_name_edit_orders);
        //Firebase initialisation

        editAddress.setText(addr);
        editOrderNo.setText(ordNumber);
        editPrice.setText(price);
        editNotes.setText(ordNotes);
        editDeliveryCharge.setText(delCharge);
        editName.setText(recName);
        editCompanyName.setText(coName);

        builder.setView(view)
                .setTitle("Add to My Orders")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String address = editAddress.getText().toString();
                String orderNo = editOrderNo.getText().toString();
                String price = editPrice.getText().toString();
                String notes = editNotes.getText().toString();
                String deliveryCharge = editDeliveryCharge.getText().toString();
                String nameOfRecipient = editName.getText().toString();
                String companyName = editCompanyName.getText().toString();
//                listenerEditOrders.editedOrder(address, orderNo, price, notes, deliveryCharge, nameOfRecipient, companyName);
                Log.d("TAG", "User submit order form");

                HashMap<String, String> updateTheOrderMap = new HashMap<>();
                updateTheOrderMap.put("Address", address);
                updateTheOrderMap.put("Recipients Name", nameOfRecipient);
                updateTheOrderMap.put("Order Number", orderNo);
                updateTheOrderMap.put("Price", price);
                updateTheOrderMap.put("Delivery Charge", deliveryCharge);
                updateTheOrderMap.put("Company Name", companyName);
                updateTheOrderMap.put("Order Notes", notes);
                updateTheOrderMap.put("Time Updated", currentTime);
                OrdersRef.child("Users").child(userID).child("Orders").child(DateSelected).child(noOfOrders).setValue(updateTheOrderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("testing", "Successfully updated orders in DB");
                    }
                });


            }
        });


        return builder.create();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}
