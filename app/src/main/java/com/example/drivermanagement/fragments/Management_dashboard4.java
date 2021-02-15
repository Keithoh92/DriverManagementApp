package com.example.drivermanagement.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.drivermanagement.R;

import java.util.ArrayList;

public class Management_dashboard4 extends Fragment {
    Activity listener;
    Button chooseRecipients, sendButton, circleButton1, circleButton2, circleButton3, circleButton4, clearButton;
    TextView chosenRecipients;

    String[] contacts, locationsArray;
    boolean[] checkedContacts;
    ArrayList<Integer> selectedContacts = new ArrayList<>();


    public Management_dashboard4() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
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
        return inflater.inflate(R.layout.fragment_management_dashboard4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chooseRecipients = view.findViewById(R.id.chooseRecipients);
        sendButton = view.findViewById(R.id.sendButton);
        circleButton1 = view.findViewById(R.id.circleButton1);
        circleButton2 = view.findViewById(R.id.circleButton2);
        circleButton3 = view.findViewById(R.id.circleButton3);
        circleButton4 = view.findViewById(R.id.circleButton4);
        clearButton = view.findViewById(R.id.clearButton);
        locationsArray = getResources().getStringArray(R.array.locations_array);
        chosenRecipients = view.findViewById(R.id.chosenRecipients);
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

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, locationsArray);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner  = view.findViewById(R.id.locations1);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}