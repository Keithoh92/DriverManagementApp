package com.example.drivermanagement.DriversFragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.drivermanagement.ChatActivity;
import com.example.drivermanagement.Contacts;
import com.example.drivermanagement.CustomMessagesDialog;
import com.example.drivermanagement.CustomMessagesDialog2;
import com.example.drivermanagement.DriversDashboardActivity;
import com.example.drivermanagement.Messages;
import com.example.drivermanagement.OCRExtractionActivity;
import com.example.drivermanagement.R;
import com.example.drivermanagement.RecyclerRecentMessagesList;
import com.example.drivermanagement.RecyclerScannedList;
import com.example.drivermanagement.SaveGroupMessageToDatabase;
import com.example.drivermanagement.SendMessage;
import com.example.drivermanagement.TinyDB;
import com.example.drivermanagement.fragments.ManagementDashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RecievedMessagesFragment extends Fragment {

    Activity listener;
    TextView heading;
    ImageButton optionsButton;
    RecyclerView recentMessagesRecyclerView;
    RecyclerRecentMessagesList recyclerRecentMessagesAdaptor;
    Spinner quickreply;
    Button recipients, send;

    String[] contacts;
    boolean[] checkedContacts;
    ArrayList<Integer> selectedContacts = new ArrayList<>();
    ArrayList<String> messageArray;
    List<String> contactsList = new ArrayList<>();
    List<String> drivesIDs = new ArrayList<>();
    List<String> listOfRecentMessages;
    Contacts contactsClass;

    ValueEventListener contactsListener;

    private DatabaseReference UsersRef, DriverRef, LastMessagesRef, GroupRef, groupMessageKeyRef1, sendToGroupsRef;
    private FirebaseAuth fAuth;
    FirebaseUser currentUser;
    String userID, receiverUserId, fromUsername, messageToSend, messageKey, currentUsername, currentDate, currentTime;
    String managementID;
    boolean isNormalUser = false;

    ArrayAdapter adapter;

    public RecievedMessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DriversDashboardActivity) {
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
        return inflater.inflate(R.layout.fragment_recieved_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        optionsButton = view.findViewById(R.id.menu_received_messages);
        send = view.findViewById(R.id.send_button_rec);
        heading = view.findViewById(R.id.received_messages_textview);
        recentMessagesRecyclerView = view.findViewById(R.id.received_messages_recyclerview);
        recipients = view.findViewById(R.id.recipients_rec_messages);
        listOfRecentMessages = new ArrayList<>();

        messageArray = new ArrayList<>();
        messageArray.add("Please add quick replies");
        TinyDB tinyDB = new TinyDB(getContext());

        fAuth = FirebaseAuth.getInstance();

        currentUser = fAuth.getCurrentUser();
        assert currentUser != null;
        userID = currentUser.getUid();

        //FIREBASE INITIALISATION
        UsersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DriverRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers");
        GroupRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups");
        sendToGroupsRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups");
        LastMessagesRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        //BUTTON TO EDIT CUSTOM REPLIES
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMessagesDialog2();
            }
        });


        recyclerRecentMessagesAdaptor = new RecyclerRecentMessagesList(listOfRecentMessages);
        recentMessagesRecyclerView.setAdapter(recyclerRecentMessagesAdaptor);

        //Recycler view line divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recentMessagesRecyclerView.addItemDecoration(dividerItemDecoration);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //              TINYDB IS AN EXTERNAL LIBRARY USED TO SAVE DATA STRUCTURES PERMANENTLY TO THE DEVICE    /////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //HERE WE ARE CHECKING TO SEE IF THE USER HAS ALREADY SET CUSTOM REPLIES AND IF SO ADD THEM TO DROPDOWN
        if (tinyDB.getListString("DriversMessagesList").isEmpty()) ;
        {
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, messageArray);
        }
        if (!tinyDB.getListString("DriversMessagesList").isEmpty()) {
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, tinyDB.getListString("DriversMessagesList"));
        }

        Log.d("ReceivedFrag", "onCreateView calling get messages");
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner = view.findViewById(R.id.received_messages_spinner);
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
    public void onResume() {
        super.onResume();
        Log.d("ReceivedMessagesFrag", "onResume called");
//        GetMessages();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("ReceivedMessagesFrag", "onstart called");
        getUserInfo();

        TinyDB tinyDB = new TinyDB(getContext());
        if (tinyDB.getListString("DriversMessagesList").isEmpty()) {
            Toast.makeText(getContext(), "Click menu button in corner to edit your quick messages in the dropdown", Toast.LENGTH_LONG).show();
        }
        if (tinyDB.getListString("DriversMessagesList").isEmpty()) ;
        {
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, messageArray);
        }
        if (!tinyDB.getListString("DriversMessagesList").isEmpty()) {
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, tinyDB.getListString("MessagesList"));
        }

        /////CHOSE MESSAGE RECIPIENTS LOGIC
        recipients.setOnClickListener(new View.OnClickListener() {
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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedContacts.size() != 0) {
                    for (int i = 0; i < selectedContacts.size(); i++) {
                        Log.d("DashFrag", "Testing send Message selected contacts retrieval: " + contacts[selectedContacts.get(i)]);
                        UsersRef.child("Users").orderByChild("username").equalTo(contacts[selectedContacts.get(i)]).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        String receiverUserId1 = ds.getKey();
                                        Log.d("SendMessageTest", "Retrieving users ID: " + receiverUserId1);

                                        //INITIALISE CLASS FOR SENDING MESSAGES IN FIREBASE CLOUD MESSAGING
                                        SendMessage sendMessage = new SendMessage();

                                        //FIRST MESSAGE IN DROPDOWN WILL ALWAYS BE NA SO WILL NOT SEND IF NA IS SELECTED
                                        if (!messageToSend.equals("NA")) {
                                            sendMessage.SendingMessage(messageToSend, userID, receiverUserId1);
                                            Toast.makeText(getContext(), "Sent Message", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(getContext(), "Please select a message", Toast.LENGTH_SHORT).show();
                                        }
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
                }
            }
        });
    }

    ////METHOD TO RETRIEVE ALL COMPANY DRIVERS TO THEN BE INSERTED INTO THE CHOOSE RECIPIENTS DIALOG
    private void GetDrivers() {
        contactsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if theres no drivers in the DB
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        contactsClass = ds.getValue(Contacts.class);
                        Log.d("DashTesting", "Drivers retrieved: " + contactsClass.getUsername());
                        contactsList.add(contactsClass.getUsername());
                        drivesIDs.add(contactsClass.getDriverid());
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
        if(managementID != null) {
            DriverRef.child(managementID).addListenerForSingleValueEvent(contactsListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        contactsList.clear();
    }

    //METHOD TO OPEN THE CUSTOM MESSAGES DIALOG AND INSERT CUSTOM MESSAGES
    private void openMessagesDialog2() {
        CustomMessagesDialog2 customMessagesDialog2 = new CustomMessagesDialog2();
        Bundle messageDialogBundle2 = new Bundle();
        TinyDB tinyDB = new TinyDB(getContext());
        List<String> fillCustomDialog = new ArrayList<>();

        for(int i = 0; i < tinyDB.getListString("DriversMessagesList").size(); i++){
            messageDialogBundle2.putString("message"+i+"", tinyDB.getListString("DriversMessagesList").get(i));
        }
        customMessagesDialog2.setArguments(messageDialogBundle2);
        customMessagesDialog2.show(getChildFragmentManager(), "Order Dialog");
    }

    ///GET THE USERS USERNAME AND THEIR MANAGERS ID SO THEY CAN ACCESS THE DRIVERS REGISTERED WITH THE COMPANY
    private void getUserInfo() {
        Log.d("Contacts", "Checking access level - ID passed: "+userID);
        UsersRef.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("userType"))) {
                    currentUsername = snapshot.child("username").getValue().toString();
                    if(snapshot.hasChild("myManagersID")) {
                        managementID = Objects.requireNonNull(snapshot.child("myManagersID").getValue()).toString();
                    }
                    GetDrivers();
                    GetMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //METHOD TO GET THE USERNAME OF THE MESSAGE SENDER TO DISPLAY BESIDE NAME IN MESSAGES FRAGMENT ON DRIVERS DASHBOARD
    private String getMessageFromUsername(String fromID) {
        Log.d("Contacts", "Getting username - ID passed: "+fromID);
        UsersRef.child("Users").child(fromID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("userType"))) {
                    fromUsername = snapshot.child("username").getValue().toString();
                    Log.d("Contacts", "username retrieved: "+fromUsername);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return fromUsername;
    }

    //METHOD TO GET THE MOST RECENT MESSAGE RECEIVED AND DYNAMICALLY LOAD INTO RECYCLERVIEW USING LISTS
    public void GetMessages(){
        Log.d("ReceivedMessagesFrag", "Get messages called");
        for(int i = 0; i < drivesIDs.size(); i++) {
            Log.d("ReceivedMessagesFrag", "Drivers ID: " + drivesIDs.get(i));
            if (isVisible()) {
                LastMessagesRef.child("Messages").child(userID).child(drivesIDs.get(i)).limitToLast(1).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {
                            Messages messages = snapshot.getValue(Messages.class);
                            if (messages.getFrom().equals(userID)) {
                                Log.d("ReceivedFrag", "Not showing message because its from the user");
                            } else {
                                Log.d("ReceivedMessagesFrag", "Last Message retrieved from user: " + messages.getMessage());
                                TinyDB tinyDB = new TinyDB(getContext());
                                ArrayList<String> tempListOFMessages = new ArrayList<>();
                                Log.d("ReceivedMessagesFrag", "Adding message to RecentMessagesList");
                                tempListOFMessages.clear();
                                String fromUser = getMessageFromUsername(messages.getFrom());
                                if (fromUser != null) {
                                    tempListOFMessages.add(fromUser + ": " + messages.getMessage());
                                } else {
                                    tempListOFMessages.add(messages.getMessage());
                                }
                                tinyDB.putListString("RecentMessagesList", tempListOFMessages);
                                for (int j = 0; j < tempListOFMessages.size(); j++) {
                                    Log.d("ReceivedMessagesFrag", "List of messages to add to recycler view: " + tempListOFMessages.get(j));
                                }
                                if (!tinyDB.getListString("RecentMessagesList").isEmpty()) {
                                    listOfRecentMessages.clear();
                                    listOfRecentMessages.addAll(tinyDB.getListString("RecentMessagesList"));
                                    recyclerRecentMessagesAdaptor.notifyDataSetChanged();
                                } else {
                                    listOfRecentMessages.clear();
                                    listOfRecentMessages.add("No Recent Messages");
                                    recyclerRecentMessagesAdaptor.notifyDataSetChanged();
                                }
                            }
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
                });
            }
        }
    }
}