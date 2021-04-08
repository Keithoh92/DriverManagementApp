package com.example.drivermanagement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class OrderDialog extends AppCompatDialogFragment {

    private String currentDate, currentTime;
    private DialogListener listener;

    EditText editAddress, editOrderNo, editPrice, editNotes, editDeliveryCharge, editName, editCompanyName;
    long noOFOrders = 0;
    int listPosition;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.scanner_order_dialog, null);

        Bundle scannedReceiver = getArguments();
        assert scannedReceiver != null;
        String scannedAddress = scannedReceiver.getString("Dialog Address");
        listPosition = scannedReceiver.getInt("List position");

        editAddress = view.findViewById(R.id.editAddress);
        editOrderNo = view.findViewById(R.id.order_no_edit);
        editPrice = view.findViewById(R.id.order_price_edit);
        editNotes = view.findViewById(R.id.order_notes_edit);
        editDeliveryCharge = view.findViewById(R.id.order_delivery_edit);
        editName = view.findViewById(R.id.editName);
        editCompanyName = view.findViewById(R.id.editCompanyName);
        //Firebase initialisation

        editAddress.setText(scannedAddress);

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
                listener.applyTexts(listPosition, address, orderNo, price, notes, deliveryCharge, nameOfRecipient, companyName);
                Log.d("TAG", "User submit order form");

//                CreateNewOrder(address, orderNo, price, notes);
            }
        });


        return builder.create();
    }

    public interface DialogListener{
        void applyTexts(int listPosition3, String address, String orderNo, String price, String notes, String deliveryCharge, String name, String companyName);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }
    }
}
