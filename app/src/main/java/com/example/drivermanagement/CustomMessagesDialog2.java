package com.example.drivermanagement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.drivermanagement.fragments.ManagementDashboard;

public class CustomMessagesDialog2 extends AppCompatDialogFragment {



    private DialogListener listenerCustomMessages2;

    EditText message1, message2, message3, message4, message5;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_messages_dialog, null);


        message1 = view.findViewById(R.id.editMessage1);
        message2 = view.findViewById(R.id.editMessage2);
        message3 = view.findViewById(R.id.editMessage3);
        message4 = view.findViewById(R.id.editMessage4);
        message5 = view.findViewById(R.id.editMessage5);

        Bundle receivedMessages = getArguments();
        assert receivedMessages != null;


        if(receivedMessages.containsKey("message0")){
            Log.d("CustomOrdersFrag2", "This is NA not adding to edit messages form");
        }if(receivedMessages.containsKey("message1")){
            message1.setText(receivedMessages.getString("message1"));
        }if(receivedMessages.containsKey("message2")){
            message2.setText(receivedMessages.getString("message2"));
        }if(receivedMessages.containsKey("message3")){
            message3.setText(receivedMessages.getString("message3"));
        }if(receivedMessages.containsKey("message4")){
            message4.setText(receivedMessages.getString("message4"));
        }if(receivedMessages.containsKey("message5")){
            message5.setText(receivedMessages.getString("message5"));
        }

        //Firebase initialisation

        builder.setView(view)
                .setTitle("Edit Custom Messages")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String messageA = message1.getText().toString();
                String messageB = message2.getText().toString();
                String messageC = message3.getText().toString();
                String messageD = message4.getText().toString();
                String messageE = message5.getText().toString();
                listenerCustomMessages2.applyMessages2(messageA, messageB, messageC, messageD, messageE);
                Log.d("TAG", "User submit order form, reloading activity");
                Intent reloadOnChange = new Intent(getContext(), DriversDashboardActivity.class);
                startActivity(reloadOnChange);

            }
        });


        return builder.create();
    }

    public interface DialogListener{
        void applyMessages2(String messageA, String messageB, String messageC, String messageD, String messageE);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listenerCustomMessages2 = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }
    }
}
