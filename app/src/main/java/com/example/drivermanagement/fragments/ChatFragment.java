package com.example.drivermanagement.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drivermanagement.ChatActivity;
import com.example.drivermanagement.Contacts;
import com.example.drivermanagement.ProfileActivity;
import com.example.drivermanagement.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
/*

THIS IS THE CHAT FRAGMENT OF THE MESSAGES FEATURE WHERE THE USER CAN SELECT A DRIVER THEY WANT TO CHAT WITH

 */
public class ChatFragment extends Fragment {

    private View chatsView;
    private RecyclerView chatsList;

    private DatabaseReference UsersRef, DriverRef, anyDriversRef;
    private FirebaseAuth fAuth;
    FirebaseUser currentUser;
    String userID, normalUserUsername;
    String managementID;
    boolean isNormalUser = false;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatsView = inflater.inflate(R.layout.fragment_chat, container, false);

        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();
        assert currentUser != null;
        userID = currentUser.getUid();

        Log.d("Contacts", "Current user ID: "+userID);
        UsersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        anyDriversRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers");

        chatsList = (RecyclerView) chatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return chatsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("ChatFrag", "OnStart called");
        Log.d("ChatFrag", "Calling check user access level");
        checkUserAccessLevel();

        //FIRST WE CHECK TO SEE IF THERE ARE ANY DRIVERS ADDED TO THE COMPANY SYSTEM YET
        //OR IF THE USER IS A DRIVER AND THEY HAVE NOT YET BEEN ADDED TO THE COMPANY SYSTEM
        anyDriversRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    if(isNormalUser) {
                        Toast.makeText(getContext(), "Your manager has not added other contacts yet or you are not yet in your managements system", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getContext(), "You have not yet added any Drivers to the system, please go to add drivers in menu", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    // THIS FIREBASE RECYCLER ADAPTOR IS USED TO DYNAMICALLY LOAD EACH DRIVER IN THE SYSTEM INTO
                    // THE LIST OF DRIVERS ON THE CHAT FRAGMENT
                    Log.d("Testing", "Found contacts");
                    FirebaseRecyclerOptions<Contacts> options =
                            new FirebaseRecyclerOptions.Builder<Contacts>()
                                    .setQuery(DriverRef, Contacts.class)
                                    .build();

                    FirebaseRecyclerAdapter<Contacts, ContactsFragment.FindDriversViewHolder> adapter =
                            new FirebaseRecyclerAdapter<Contacts, ContactsFragment.FindDriversViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull ContactsFragment.FindDriversViewHolder holder, final int position, @NonNull Contacts model) {
                                    if(!model.getImage().equals("")) {
                                        // HERE WE CHECK IF THE USER RETURNED FROM THE DRIVERS DATABASE IS THE CURRENT USER
                                        // IF IT IS WE DO NOT DISPLAY IT IN THE LIST
                                        if(isNormalUser && normalUserUsername.equals(model.getUsername())){
                                            Log.d("Testing", "Current user, not adding to view");
                                            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)holder.itemView.getLayoutParams();
                                            param.height = 0;
                                            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                                            holder.itemView.setVisibility(View.VISIBLE);
                                            //Get the managers details here
                                        }
                                        if(!isNormalUser && model.getDriverid().equals(userID)) {
                                            Log.d("Testing", "Current management user, not adding to view");
                                            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)holder.itemView.getLayoutParams();
                                            param.height = 0;
                                            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                                            holder.itemView.setVisibility(View.VISIBLE);
                                        }
                                        else{
                                            holder.userName.setText(model.getUsername());
                                            Log.d("TAG", "Getting username" + model.getUsername());

                                            //IF THE DRIVER HAS A PROFILE PIC ADD IT TO THE VIEW BESIDE USERNAME
                                            Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                            Log.d("TAG", "Getting Profile pic" + model.getImage());

                                        }
                                    }else{
                                        if(isNormalUser && normalUserUsername.equals(model.getUsername())) {
                                            Log.d("Testing", "Current user, not adding to view");
                                            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)holder.itemView.getLayoutParams();
                                            param.height = 0;
                                            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                                            holder.itemView.setVisibility(View.VISIBLE);

                                        }
                                        if(!isNormalUser && model.getDriverid().equals(userID)){
                                            Log.d("Testing", "Current management user, not adding to view");
                                            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)holder.itemView.getLayoutParams();
                                            param.height = 0;
                                            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                                            holder.itemView.setVisibility(View.VISIBLE);
                                        }
                                        else{
                                            holder.userName.setText(model.getUsername());
                                        }
                                    }
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Get user id when user selects user and go to chat activity
                                            String visit_user_id = getRef(position).getKey();
                                            String userNameR = getItem(position).username;
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id", visit_user_id);
                                            chatIntent.putExtra("visit_user_name", userNameR);
                                            startActivity(chatIntent);
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public ContactsFragment.FindDriversViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_display_layout, parent, false);
                                    ContactsFragment.FindDriversViewHolder viewHolder = new ContactsFragment.FindDriversViewHolder(view);
                                    return viewHolder;
                                }
                            };
                    chatsList.setAdapter(adapter);

                    adapter.startListening();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static class FindDriversViewHolder extends RecyclerView.ViewHolder
    {

        TextView userName, lastChats;
        CircleImageView profileImage;
        public FindDriversViewHolder(@NonNull View itemView)
        {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }

    //METHOD TO CHECK THE USERS ACCESS LEVEL
    // WHEN RETRIEVING DRIVERS, THE DRIVERS ARE STORED UNDER THE MANAGERS SYSTEM ID, SO IF THE USER IS A DRIVER
    // THEIR MANAGERS ID WILL BE STORED UNDER THEIR USER INFO IN THE DATABASE WHEN THE MANAGER ADDS THEM TO THE SYSTEM,
    // WE THEN RETRIEVE THIS EVERYTIME WE NEED ACCESS TO THE OTHER DRIVERS IN THE SYSTEM
    private void checkUserAccessLevel() {
        Log.d("Contacts", "Checking access level - ID passed: "+userID);
        UsersRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("userType"))) {
                    String retrieveUserType = Objects.requireNonNull(snapshot.child("userType").getValue()).toString();
                    if (retrieveUserType.equals("Management")) {
                        Log.d("ChatFrag", "User is Management user");
                        DriverRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers").child(userID);

                    }
                    if (retrieveUserType.equals("Driver")) {
                        Log.d("ChatFrag", "User is normal user");
                        if(snapshot.hasChild("myManagersID")) {
                            managementID = Objects.requireNonNull(snapshot.child("myManagersID").getValue()).toString();
                        }                        normalUserUsername = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                        DriverRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers").child(managementID);
                        isNormalUser = true;
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
}