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

import com.example.drivermanagement.Contacts;
import com.example.drivermanagement.DriverDashboard;
import com.example.drivermanagement.LoginActivity;
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

THIS IS WHERE THE USER CAN SELECT ANOTHER USER AND SEE THEIR PROFILE, GET THEIR NUMBER, EMAIL ADDRESS AND LOCATION IS THE USER IS A MANAGER USER

 */

public class ContactsFragment extends Fragment {

    private View contactsFragmentView;
    private RecyclerView findDriverList;
    private DatabaseReference UsersRef, DriverRef, anyDriversRef;
    private FirebaseAuth fAuth;
    FirebaseUser currentUser;
    String userID, normalUserUsername;
    String managementID;
    boolean isNormalUser = false;


    public ContactsFragment() {
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
        contactsFragmentView = inflater.inflate(R.layout.fragment_contacts, container, false);
        fAuth = FirebaseAuth.getInstance();

        currentUser = fAuth.getCurrentUser();
        assert currentUser != null;
        userID = currentUser.getUid();

        Log.d("Contacts", "Current user ID: "+userID);
        UsersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        anyDriversRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers");

        findDriverList = contactsFragmentView.findViewById(R.id.find_drivers_list);
        findDriverList.setLayoutManager(new LinearLayoutManager(getContext()));


        return contactsFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Contacts", "OnStart called");
        Log.d("Contacts", "Calling check user access level");
        checkUserAccessLevel();


        anyDriversRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if theres no drivers in the DB
                if(!snapshot.exists()){
                    //AND if the user is normal driver display this message
                    if(isNormalUser) {
                        Toast.makeText(getContext(), "Your manager has not added other contacts yet or you are not yet in your managements system", Toast.LENGTH_LONG).show();
                        //ELSE if its management user display this message
                    }else{
                        Toast.makeText(getContext(), "You have not yet added any Drivers to the system, please go to add drivers in menu", Toast.LENGTH_LONG).show();
                    }
                }
                else{
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
                                            //Get user id when user selects user
                                            String visit_user_id = getRef(position).getKey();
                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("visit_user_id", visit_user_id);
                                            profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(profileIntent);
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public ContactsFragment.FindDriversViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                                    ContactsFragment.FindDriversViewHolder viewHolder = new ContactsFragment.FindDriversViewHolder(view);
                                    return viewHolder;
                                }
                            };
                    findDriverList.setAdapter(adapter);

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

        TextView userName;
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
                        Log.d("Contacts", "User is Management user");
                        DriverRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers").child(userID);

                    }
                    if (retrieveUserType.equals("Driver")) {
                        Log.d("Contacts", "User is normal user");
                        if(snapshot.hasChild("myManagersID")) {
                            managementID = Objects.requireNonNull(snapshot.child("myManagersID").getValue()).toString();
                        }
                        normalUserUsername = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
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