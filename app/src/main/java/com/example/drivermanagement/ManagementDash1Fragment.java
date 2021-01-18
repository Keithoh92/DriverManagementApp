package com.example.drivermanagement;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ManagementDash1Fragment extends Fragment {
    Activity listener;
    Button send, chooseRecipients;
    TextView chosenRecipients;

    String[] contacts, managementNotifications2, messageArray, locationArray;
    boolean[] checkedContacts;
    ArrayList<Integer> selectedContacts = new ArrayList<>();

//    String[] locationsArray = {"Locations", "Baldoyle", "Coolock", "Blanchardstown", "Santry"};
//    String[] messageArray = {"Choose message", "How long will you be?", "How many deliveries have you left?", "What is your status", "Can you call when you get the chance"};


    public ManagementDash1Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ManagementDashboard){
            this.listener = (ManagementDashboard) context;
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
        return inflater.inflate(R.layout.fragment_management_dashboard1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        managementNotifications2 = getResources().getStringArray(R.array.manage_notifications2);
        chooseRecipients = (Button) view.findViewById(R.id.chooseRecipients);
        chosenRecipients = (TextView) view.findViewById(R.id.chosenRecipients);
        send = (Button) view.findViewById(R.id.sendButton);
        messageArray = getResources().getStringArray(R.array.message_array);
        locationArray = getResources().getStringArray(R.array.locations_array);
        contacts = getResources().getStringArray(R.array.recipents);
        checkedContacts = new boolean[contacts.length];

        chooseRecipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle(R.string.dialog_title);
                mBuilder.setMultiChoiceItems(contacts, checkedContacts, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if(isChecked){
                            if(!selectedContacts.contains(position)){
                                selectedContacts.add(position);
                            }else{
                                selectedContacts.remove(position);
                            }
                        }
                    }
                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = "";
                        for(int i = 0; i < selectedContacts.size(); i++){
                            item = item + contacts[selectedContacts.get(i)];
                            if(i != selectedContacts.size() -1){
                                item = item + ", ";
                            }
                        }
                        chosenRecipients.setText(item);
                    }
                });
                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setNeutralButton(R.string.clear_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < checkedContacts.length; i++){
                            checkedContacts[i] = false;
                            selectedContacts.clear();
                            chosenRecipients.setText("");
                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
////////////////////////first dropdown/////////////////////////////////////////////////
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, messageArray);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner  = view.findViewById(R.id.not1);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
////////////////////////Second dropdown/////////////////////////////////////////////////
        ArrayAdapter adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, managementNotifications2);
        adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner1  = view.findViewById(R.id.not2);
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ////////////////////////Locations dropdown/////////////////////////////////////////////////
        ArrayAdapter adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, locationArray);
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner2  = view.findViewById(R.id.not3);
        spinner2.setAdapter(adapter2);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}