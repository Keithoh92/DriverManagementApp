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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.drivermanagement.R;

import java.util.ArrayList;


public class ManagementDash2Fragment extends Fragment {
    Activity listener;
    Button send, chooseRecipients1, chooseButton;
    TextView chosenRecipients1;
    ImageView menuOption;
    int count = 0;

    String[] contacts, messageArray;
    boolean[] checkedContacts;
    ArrayList<Integer> selectedContacts = new ArrayList<>();

    public ManagementDash2Fragment() {
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
        return inflater.inflate(R.layout.fragment_management_dash2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chooseRecipients1 = (Button) view.findViewById(R.id.chooseRecipients1);
        chosenRecipients1 = (TextView) view.findViewById(R.id.chosenRecipients1);
        send = (Button) view.findViewById(R.id.sendButton);
        messageArray = getResources().getStringArray(R.array.message_array);
        contacts = getResources().getStringArray(R.array.recipents);
        checkedContacts = new boolean[contacts.length];
//        menuOption = (ImageView) view.findViewById(R.id.menuOption);
//        chooseButton = (Button)getActivity().findViewById(R.id.chooseButton);


//        menuOption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                menuOption.setVisibility(View.INVISIBLE);
//                if(count == 0){
//                    Toast.makeText(getActivity(), "Click menu button again to choose new top dashboard", Toast.LENGTH_SHORT).show();
//                    count++;
//                }
//                if(count == 1){
//                    FragmentManager fragment = getActivity().getSupportFragmentManager();
//                    Fragment myFrag = fragment.findFragmentById(R.id.chooserFrag);
//                    fragment.beginTransaction()
//                            .show(myFrag)
//                            .commit();
//
//                    count = 0;
//                    FragmentManager fragment2 = getActivity().getSupportFragmentManager();
//                    Fragment myFrag2 = fragment2.findFragmentById(R.id.manDash1);
//                    fragment2.beginTransaction()
//                            .hide(myFrag2)
//                            .commit();
//
//                    FragmentManager fragment3 = getActivity().getSupportFragmentManager();
//                    Fragment myFrag3 = fragment3.findFragmentById(R.id.manDash2);
//                    fragment3.beginTransaction()
//                            .hide(myFrag3)
//                            .commit();
//
////                    Activity act = getActivity();
////                    Button btn = act.getResources().getLayout(R.id.chooseButton);
////                    chooseButton.
//
//
//                }
//            }
//        });

        chooseRecipients1.setOnClickListener(new View.OnClickListener() {
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
                        chosenRecipients1.setText(item);
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
                            chosenRecipients1.setText("");
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
        Spinner spinner  = view.findViewById(R.id.messagesSpinnerMDash2);
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