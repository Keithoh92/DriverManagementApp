package com.example.drivermanagement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class OrderDialog extends AppCompatDialogFragment {

    private EditText editAddress, editOrderNo, editPrice, editNotes;
    private DialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.scanner_order_dialog, null);

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
                listener.applyTexts(address, orderNo, price, notes);
            }
        });
        editAddress = view.findViewById(R.id.editAddress);
        editOrderNo = view.findViewById(R.id.order_no_edit);
        editPrice = view.findViewById(R.id.order_price_edit);
        editNotes = view.findViewById(R.id.order_notes_edit);

        return builder.create();
    }

    public interface DialogListener{
        void applyTexts(String address, String orderNo, String price, String notes);
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
