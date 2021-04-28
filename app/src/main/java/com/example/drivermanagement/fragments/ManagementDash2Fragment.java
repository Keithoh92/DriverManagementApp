package com.example.drivermanagement.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drivermanagement.Contacts;
import com.example.drivermanagement.CustomMessagesDialog;
import com.example.drivermanagement.DriversDashboardActivity;
import com.example.drivermanagement.GroupChatActivity;
import com.example.drivermanagement.Groups;
import com.example.drivermanagement.OrderDialog;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/*

THIS IS THE FRAGMENT FOR CHOOSING A QUICK MESSAGE OR WRITING A MESSAGE TO SEND
THIS IS USED ON BOTH DRIVER SIDE AND MANAGEMENT SYSTEMS

 */

public class ManagementDash2Fragment extends Fragment {
    Activity listener;
    Button send, chooseRecipients1, chooseButton, chooseGroups;
    TextView chosenRecipients1;
    EditText customMessage;
    ImageView menuOption;
    int count = 0;


    String[] contacts;
    String[] groups;
    boolean[] checkedContacts;
    boolean[] checkedGroups;
    ArrayList<Integer> selectedContacts = new ArrayList<>();
    ArrayList<Integer> selectedGroups = new ArrayList<>();
    ArrayList<String> messageArray;
    List<String> contactsList = new ArrayList<>();
    List<String> groupsList = new ArrayList<>();
    Contacts contactsClass;
    Groups groupsClass;

    ValueEventListener driverListener, groupListener;
    private DatabaseReference UsersRef, DriverRef, GroupRef, groupMessageKeyRef1, sendToGroupsRef;
    private FirebaseAuth fAuth;
    FirebaseUser currentUser;
    String userID, retrieveUserType, receiverUserId, messageToSend, messageKey, currentUsername, currentDate, currentTime;
    String managementID;
    boolean isNormalUser = false;

    ArrayAdapter adapter;

    public ManagementDash2Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ManagementDashboard) {
            this.listener = (ManagementDashboard) context;
        }
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
        return inflater.inflate(R.layout.fragment_management_dash2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chooseRecipients1 = (Button) view.findViewById(R.id.chooseRecipients1);
        chooseGroups = (Button) view.findViewById(R.id.choose_groups);
        chosenRecipients1 = (TextView) view.findViewById(R.id.chosenRecipients1);
        send = (Button) view.findViewById(R.id.sendButton);
        customMessage = view.findViewById(R.id.editTextTextPersonName2);
        messageArray = new ArrayList<>();
        messageArray.add("Please add your quick messages");
        TinyDB tinyDB = new TinyDB(getContext());
        fAuth = FirebaseAuth.getInstance();

        currentUser = fAuth.getCurrentUser();
        assert currentUser != null;
        userID = currentUser.getUid();

        UsersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        DriverRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers");
        GroupRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups");
        sendToGroupsRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups");
        checkUserAccessLevel();

//        anyDriversRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers");


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh-mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());

        menuOption = (ImageButton) view.findViewById(R.id.menuOption);
        menuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMessagesDialog();
            }
        });

////////////////////////first dropdown/////////////////////////////////////////////////
        if (tinyDB.getListString("MessagesList").isEmpty()) ;
        {
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, messageArray);
        }
        if (!tinyDB.getListString("MessagesList").isEmpty()) {
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, tinyDB.getListString("MessagesList"));
        }

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner = view.findViewById(R.id.messagesSpinnerMDash2);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                messageToSend = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        checkUserAccessLevel();
///////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////           CONTACTS DROPDOWN                               ///////////////////////////////////////
/////////////////////////                                       ////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////
        chooseRecipients1.setOnClickListener(new View.OnClickListener() {
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
                        chosenRecipients1.setText("Sending To: " + item);

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
                            chosenRecipients1.setText("");
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
                        chosenRecipients1.setText("Sending To: " + item);

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
                            chosenRecipients1.setText("");
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
                String setMessage;
                if(messageToSend.equals("NA") && TextUtils.isEmpty(customMessage.getText().toString())){
                    Toast.makeText(listener.getApplicationContext(), "No message selected or entered", Toast.LENGTH_SHORT).show();
                }

                else if(!messageToSend.equals("NA") && !TextUtils.isEmpty(customMessage.getText().toString())){
                    Toast.makeText(listener.getApplicationContext(), "Can only send one of the above messages - remove one", Toast.LENGTH_LONG).show();
                }
                else if(messageToSend.equals("NA") && !TextUtils.isEmpty(customMessage.getText().toString())){
                    setMessage = customMessage.getText().toString();
                    SendMessageFromFrag(setMessage);
                    customMessage.setText("");
                }
                else if(!messageToSend.equals("NA") && TextUtils.isEmpty(customMessage.getText().toString())){
                    setMessage = messageToSend;
                    SendMessageFromFrag(setMessage);
                }
            }
        });
    }

    private void SendMessageFromFrag(String setMessage)
    {
        SendMessage sendMessage = new SendMessage();
        if (selectedContacts.size() != 0) {
            for (int i = 0; i < selectedContacts.size(); i++) {
                Log.d("DashFrag", "Testing send Message selected contacts retrieval: " + contacts[selectedContacts.get(i)]);
                UsersRef.orderByChild("username").equalTo(contacts[selectedContacts.get(i)]).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String receiverUserId1 = ds.getKey();
                                Log.d("SendMessageTest", "Retrieving users ID: " + receiverUserId1);
                                sendMessage.SendingMessage(setMessage, userID, receiverUserId1);
                                chosenRecipients1.setText("");
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
                chosenRecipients1.setText("");
            }
//                Toast.makeText(getContext(), "Sending to - "+contacts[selectedContacts(i)]);
        }

        if (selectedGroups.size() != 0) {
            SaveGroupMessageToDatabase sendGroupMessage = new SaveGroupMessageToDatabase();
            for(int i = 0; i < selectedGroups.size(); i++) {
                if(isNormalUser){
                    if(managementID != null) {
                        sendGroupMessage.SendingGroupMessage(groups[selectedGroups.get(i)], managementID, setMessage);
                    }
                    }else {
                    sendGroupMessage.SendingGroupMessage(groups[selectedGroups.get(i)], userID, setMessage);
                    chosenRecipients1.setText("");
                    customMessage.setText("");
                }
            }
            for (int i = 0; i < checkedGroups.length; i++) {
                checkedGroups[i] = false;
                selectedGroups.clear();
                chosenRecipients1.setText("");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        contactsList.clear();
        groupsList.clear();
//        adapter.setNotifyOnChange(true);
    }


    private void openMessagesDialog() {
        CustomMessagesDialog customMessagesDialog = new CustomMessagesDialog();
        Bundle messageDialogBundle = new Bundle();
        TinyDB tinyDB = new TinyDB(getContext());
        List<String> fillCustomDialog = new ArrayList<>();
        messageDialogBundle.putString("UserType", retrieveUserType);
        for(int i = 0; i < tinyDB.getListString("MessagesList").size(); i++){
            messageDialogBundle.putString("message"+i+"", tinyDB.getListString("MessagesList").get(i));
        }
        customMessagesDialog.setArguments(messageDialogBundle);
        customMessagesDialog.show(getChildFragmentManager(), "Messages Dialog");
    }

    private void checkUserAccessLevel() {
        Log.d("Contacts", "Checking access level - ID passed: "+userID);
        UsersRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("userType"))) {
                    retrieveUserType = Objects.requireNonNull(snapshot.child("userType").getValue()).toString();
                    currentUsername = snapshot.child("username").getValue().toString();
                    if (retrieveUserType.equals("Management")) {
                        Log.d("ManDash2", "User is Management user");
//                        DriverRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers").child(userID);
//                        GroupRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups").child(userID);
                        GetDriversAndGroups();

                    }
                    if (retrieveUserType.equals("Driver")) {
                        Log.d("ManDash2", "User is normal user");
                        if(snapshot.hasChild("myManagersID")) {
                            managementID = Objects.requireNonNull(snapshot.child("myManagersID").getValue()).toString();
                        }
                        currentUsername = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
//                        DriverRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers").child(managementID);
//                        GroupRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups").child(managementID);
                        isNormalUser = true;
                        GetDriversAndGroups();
                        chooseGroups.setVisibility(View.INVISIBLE);

                    }
                }else{
                    Log.d("Contacts", "No usertype found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetDriversAndGroups() {
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
        if (isNormalUser) {
            if(managementID != null) {
                DriverRef.child(managementID).addValueEventListener(driverListener);
            }
//            DriverRef.addValueEventListener(driverListener);
        } else {
            DriverRef.child(userID).addValueEventListener(driverListener);
        }
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
        if (isNormalUser) {
//            GroupRef.child("GroupInfo").addValueEventListener(groupListener);
            if(managementID != null) {
                GroupRef.child(managementID).child("GroupInfo").addValueEventListener(groupListener);
            }
            } else {
            GroupRef.child(userID).child("GroupInfo").addValueEventListener(groupListener);
        }
    }
}