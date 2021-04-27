package com.example.drivermanagement.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drivermanagement.Contacts;
import com.example.drivermanagement.CustomMessagesDialog;
import com.example.drivermanagement.CustomMessagesDialogManagersDash;
import com.example.drivermanagement.Groups;
import com.example.drivermanagement.R;
import com.example.drivermanagement.SaveGroupMessageToDatabase;
import com.example.drivermanagement.SendMessage;
import com.example.drivermanagement.TinyDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ManagementDash1Fragment extends Fragment {
    Activity listener;
    Button send, chooseRecipients, chooseGroups;
    TextView chosenRecipients;
    ImageButton menuOptionNotify1, menuOptionNotify2, menuOptionNotify3;

    String[] contacts, groups;
    ArrayList<String> ordersArray, messageArray, locationArray;
    boolean[] checkedContacts, checkedGroups;
    ArrayList<Integer> selectedContacts = new ArrayList<>();
    ArrayList<Integer> selectedGroups = new ArrayList<>();
    List<String> contactsList = new ArrayList<>();
    List<String> groupsList = new ArrayList<>();
    Contacts contactsClass;
    Groups groupsClass;

    ValueEventListener driverListener, groupListener;

    private DatabaseReference UsersRef, DriverRef, GroupRef, groupMessageKeyRef1, sendToGroupsRef;
    private FirebaseAuth fAuth;
    FirebaseUser currentUser;
    String userID, messageToSend1, messageToSend2, messageToSend3, currentUsername, currentDate, currentTime;
    ArrayAdapter adapter1, adapter2, adapter3;


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
        chooseRecipients = (Button) view.findViewById(R.id.chooseRecipients);
        chooseGroups = (Button) view.findViewById(R.id.choose_groups);
        send = (Button) view.findViewById(R.id.sendButton);
        messageArray = new ArrayList<>();
        locationArray = new ArrayList<>();
        ordersArray = new ArrayList<>();


        messageArray = new ArrayList<>();
        messageArray.add("Please add your custom quick messages");
        ordersArray = new ArrayList<>();
        ordersArray.add("Please add your custom quick messages");
        locationArray = new ArrayList<>();
        locationArray.add("Please add your custom quick messages");

        TinyDB tinyDB = new TinyDB(getContext());
        fAuth = FirebaseAuth.getInstance();

        currentUser = fAuth.getCurrentUser();
        assert currentUser != null;
        userID = currentUser.getUid();

        UsersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        DriverRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers");
        GroupRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups");
        sendToGroupsRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups");


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh-mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());

        menuOptionNotify1 = (ImageButton) view.findViewById(R.id.menu_option1);
        menuOptionNotify2 = (ImageButton) view.findViewById(R.id.menu_option2);
        menuOptionNotify3 = (ImageButton) view.findViewById(R.id.menu_option3);


        menuOptionNotify1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> ordersList = new ArrayList<>();
                ordersList.addAll(tinyDB.getListString("OrdersList"));
                String listName = "OrdersList";
                openMessagesDialogMultiple(ordersList, listName);
            }
        });
        menuOptionNotify2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> locationsList = new ArrayList<>();
                locationsList.addAll(tinyDB.getListString("LocationsList"));
                String listName = "LocationsList";
                openMessagesDialogMultiple(locationsList, listName);
            }
        });
        menuOptionNotify3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> messagesList = new ArrayList<>();
                messagesList.addAll(tinyDB.getListString("MessagesList2"));
                String listName = "MessagesList2";
                openMessagesDialogMultiple(messagesList, listName);
            }
        });

        /////////////////////////ADAPTOR INITIALISATION//////////////////////
        ////////////////////////////////////////////////////////////////////

        ///Spinner 1
        if (tinyDB.getListString("OrdersList").isEmpty()) {
            adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, ordersArray);
        }
        if (!tinyDB.getListString("OrdersList").isEmpty()) {
            adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, tinyDB.getListString("OrdersList"));
        }
        //Spinner 2
        if (tinyDB.getListString("LocationsList").isEmpty()) {
            adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, locationArray);
        }
        if (!tinyDB.getListString("LocationsList").isEmpty()) {
            adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, tinyDB.getListString("LocationsList"));
        }
        ///Spinner 2
        if (tinyDB.getListString("MessagesList2").isEmpty()) {
            adapter3 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, messageArray);
        }
        if (!tinyDB.getListString("MessagesList2").isEmpty()) {
            adapter3 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, tinyDB.getListString("MessagesList2"));
        }

        ////////////////////////first dropdown/////////////////////////////////////////////////
        adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner1 = view.findViewById(R.id.not1);
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                messageToSend1 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
////////////////////////Second dropdown/////////////////////////////////////////////////
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner2 = view.findViewById(R.id.not2);
        spinner2.setAdapter(adapter2);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                messageToSend2 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ////////////////////////Third dropdown/////////////////////////////////////////////////
        adapter3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner3 = view.findViewById(R.id.not3);
        spinner3.setAdapter(adapter3);

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                messageToSend3 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

////////////////////////////Set up the choose recipients dialog boxes fro contacts and groups
        driverListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if theres no drivers in the DB
                if (!snapshot.exists()) {
                    Log.d("DashTesting", "No Drivers found");

                    //AND if the user is normal driver display this message
//                    if (isNormalUser) {
//                        Toast.makeText(getContext(), "Your manager has not added other contacts yet or you are not yet in your managements system", Toast.LENGTH_LONG).show();
//                        //ELSE if its management user display this message
//                    } else {
//                        Toast.makeText(getContext(), "You have not yet added any Drivers to the system, please go to add drivers in menu", Toast.LENGTH_LONG).show();
//                    }
                } else {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        contactsClass = ds.getValue(Contacts.class);
                        Log.d("DashTesting", "Drivers retrieved: " + contactsClass.getUsername());
                        contactsList.add(contactsClass.getUsername());
                    }
                    for (int i = 0; i < contactsList.size(); i++) {
                        Log.d("DashTesting", "Drivers in contactsList: " + contactsList.get(i));
                    }

                    contacts = new String[contactsList.size()];
                    Log.d("DashTesting", "contactsList size: " + contactsList.size());

                    for (int i = 0; i < contactsList.size(); i++) {
                        contacts[i] = contactsList.get(i);
                        Log.d("DashTesting", "Adding to contacts: " + contacts[i]);
                    }
                    checkedContacts = new boolean[contacts.length];
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        DriverRef.child(userID).addValueEventListener(driverListener);

        groupListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    groupsClass = ds.getValue(Groups.class);
                    Log.d("DashTesting", "Groups retrieved: " + groupsClass.getGroupname());
                    groupsList.add(groupsClass.getGroupname());
                }
                for (int i = 0; i < groupsList.size(); i++) {
                    Log.d("DashTesting", "Groups in groupsList: " + groupsList.get(i));
                }

                groups = new String[groupsList.size()];
                Log.d("DashTesting", "groupsList size: " + groupsList.size());

                for (int i = 0; i < groupsList.size(); i++) {
                    groups[i] = groupsList.get(i);
                    Log.d("DashTesting", "Adding to contacts: " + groups[i]);
                }
                checkedGroups = new boolean[groups.length];
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        GroupRef.child(userID).child("GroupInfo").addValueEventListener(groupListener);

    }

    @Override
    public void onStart() {
        super.onStart();

///////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////           CONTACTS DROPDOWN                               ///////////////////////////////////////
/////////////////////////                                       ////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////

        chooseRecipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle(R.string.dialog_title);
                mBuilder.setMultiChoiceItems(contacts, checkedContacts, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!selectedContacts.contains(position)) {
                                selectedContacts.add(position);
                            } else {
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
                        for (int i = 0; i < selectedContacts.size(); i++) {
                            item = item + contacts[selectedContacts.get(i)];
                            if (i != selectedContacts.size() - 1) {
                                item = item + ", ";
                            }
                        }
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
                        for (int i = 0; i < checkedContacts.length; i++) {
                            checkedContacts[i] = false;
                            selectedContacts.clear();
                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////           GROUPS DROPDOWN                               ///////////////////////////////////////
/////////////////////////                                       ////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////
        chooseGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle(R.string.dialog_title);
                mBuilder.setMultiChoiceItems(groups, checkedGroups, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!selectedGroups.contains(position)) {
                                selectedGroups.add(position);
                            } else {
                                selectedGroups.remove(position);
                            }
                        }
                    }
                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = "";
                        for (int i = 0; i < selectedGroups.size(); i++) {
                            item = item + groups[selectedGroups.get(i)];
                            if (i != selectedGroups.size() - 1) {
                                item = item + ", ";
                            }
                        }
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
                        for (int i = 0; i < checkedGroups.length; i++) {
                            checkedGroups[i] = false;
                            selectedGroups.clear();
                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });






        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Prepare messages to send from dropdowns
                ArrayList<String> prepareMessages = new ArrayList<>();
                prepareMessages.clear();
                StringBuilder sb = new StringBuilder();
                String mess;
                String sep = "";

                if(!messageToSend1.equals("NA")) {
                    prepareMessages.add(messageToSend1);
                }
                if(!messageToSend2.equals("NA")){
                    prepareMessages.add(messageToSend2);
                }
                if(!messageToSend3.equals("NA")){
                    prepareMessages.add(messageToSend3);
                }

                if(prepareMessages.size() == 0){
                    Toast.makeText(getContext(), "Please Select a Message to Send", Toast.LENGTH_SHORT).show();
                }else{
                    for(int i = 0; i < prepareMessages.size(); i++) {
                        sb.append(sep + prepareMessages.get(i));
                        sep = ", ";
                    }
                }
                if (selectedContacts.size() != 0) {
                    SendMessage sendMessage = new SendMessage();
                    for (int i = 0; i < selectedContacts.size(); i++) {
                        Log.d("DashFrag", "Testing send Message selected contacts retrieval: " + contacts[selectedContacts.get(i)]);
                        UsersRef.orderByChild("username").equalTo(contacts[selectedContacts.get(i)]).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        String receiverUserId1 = ds.getKey();
                                        Log.d("SendMessageTest", "Retrieving users ID: " + receiverUserId1);
                                        sendMessage.SendingMessage(sb.toString(), userID, receiverUserId1);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                    for (int i = 0; i < checkedContacts.length; i++) {
                        checkedContacts[i] = false;
                        selectedContacts.clear();
                    }
//                Toast.makeText(getContext(), "Sending to - "+contacts[selectedContacts(i)]);
                }

                if (selectedGroups.size() != 0) {
                    SaveGroupMessageToDatabase sendGroupMessage = new SaveGroupMessageToDatabase();
                    for(int i = 0; i < selectedGroups.size(); i++) {
                        sendGroupMessage.SendingGroupMessage(groups[selectedGroups.get(i)], userID, sb.toString());
                    }
                    for (int i = 0; i < checkedGroups.length; i++) {
                        checkedGroups[i] = false;
                        selectedGroups.clear();
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        contactsList.clear();
        groupsList.clear();
    }

    private void openMessagesDialogMultiple(List<String> list, String listName) {
        CustomMessagesDialogManagersDash customMessagesDialogManagersDash = new CustomMessagesDialogManagersDash();
        Bundle messageDialogBundle = new Bundle();
        TinyDB tinyDB = new TinyDB(getContext());
        messageDialogBundle.putString("listName", listName);
        for(int i = 0; i < list.size(); i++){
            messageDialogBundle.putString("message"+i+"", list.get(i));
        }
        customMessagesDialogManagersDash.setArguments(messageDialogBundle);
        customMessagesDialogManagersDash.show(getChildFragmentManager(), "Custom Messages Dialog");
    }
}