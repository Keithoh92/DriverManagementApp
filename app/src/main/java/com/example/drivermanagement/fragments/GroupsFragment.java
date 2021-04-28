package com.example.drivermanagement.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drivermanagement.Contacts;
import com.example.drivermanagement.GroupChatActivity;
import com.example.drivermanagement.Groups;
import com.example.drivermanagement.ProfileActivity;
import com.example.drivermanagement.R;
import com.example.drivermanagement.notifications.RootModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

/*

EACH GROUP THE USER IS A MEMBER OF IS LOADED INTO THIS FRAGMENT
 */

public class GroupsFragment extends Fragment {

    ValueEventListener listener;
    private View groupFragmentView;
    private RecyclerView list_view;


    private DatabaseReference RootRef, UsersRef, anyGroupsRef;
    private FirebaseAuth fAuth;
    FirebaseUser currentUser;
    String userID, managementID;
    boolean isNormalUser = false;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);

        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();
        assert currentUser != null;
        userID = currentUser.getUid();

        UsersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        anyGroupsRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups");

        list_view = groupFragmentView.findViewById(R.id.find_groups_list);
        list_view.setLayoutManager(new LinearLayoutManager(getContext()));

        return groupFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Groups", "OnStart called");
        checkUserAccessLevel();
        anyGroupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if theres no drivers in the DB
                if(!snapshot.exists()){
                    //AND if the user is normal driver display this message
                    if(isNormalUser) {
                        Toast.makeText(getContext(), "Your manager has not created any groups yet or you are not yet in your managements system", Toast.LENGTH_LONG).show();
                        //ELSE if its management user display this message
                    }else{
                        Toast.makeText(getContext(), "You have not yet created any Groups yet, please go to create group in menu", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Log.d("Testing", "Found Groups");

                    FirebaseRecyclerOptions<Groups> options =
                            new FirebaseRecyclerOptions.Builder<Groups>()
                                    .setQuery(RootRef.child("GroupInfo"), Groups.class)
                                    .build();

                    FirebaseRecyclerAdapter<Groups, GroupsFragment.FindGroupsViewHolder> adapter =
                            new FirebaseRecyclerAdapter<Groups, GroupsFragment.FindGroupsViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull FindGroupsViewHolder holder, int position, @NonNull Groups model) {
                                    if(!model.getImage().equals("")) {
                                        holder.groupName.setText(model.getGroupname());
                                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.groupProfileImage);
                                    }else{
                                        holder.groupName.setText(model.getGroupname());
                                    }
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String visit_user_id = getRef(position).getKey();
                                            String groupsName = getItem(position).groupname;
                                            Intent groupChat = new Intent(getContext(), GroupChatActivity.class);
                                            groupChat.putExtra("GroupName", groupsName);
                                            groupChat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(groupChat);
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public GroupsFragment.FindGroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_display_layout, parent, false);
                                    GroupsFragment.FindGroupsViewHolder viewHolder = new GroupsFragment.FindGroupsViewHolder(view);
                                    return viewHolder;
                                }
                            };
                    list_view.setAdapter(adapter);

                    adapter.startListening();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public static class FindGroupsViewHolder extends RecyclerView.ViewHolder
    {

        TextView groupName;
        CircleImageView groupProfileImage;
        public FindGroupsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            groupProfileImage = itemView.findViewById(R.id.groups_profile_image);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    // METHOD TO CHECK THE USERS ACCESS LEVEL
    // WHEN RETRIEVING DRIVERS, THE DRIVERS ARE STORED UNDER THE MANAGERS SYSTEM ID, SO IF THE USER IS A DRIVER
    // THEIR MANAGERS ID WILL BE STORED UNDER THEIR USER INFO IN THE DATABASE WHEN THE MANAGER ADDS THEM TO THE SYSTEM,
    // WE THEN RETRIEVE THIS EVERYTIME WE NEED ACCESS TO THE OTHER DRIVERS IN THE SYSTEM
    private void checkUserAccessLevel() {
        Log.d("GroupsFragment", "Checking access level - ID passed: "+userID);
        UsersRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("userType"))) {
                    String retrieveUserType = Objects.requireNonNull(snapshot.child("userType").getValue()).toString();
                    if (retrieveUserType.equals("Management")) {
                        Log.d("GroupsFragment", "User is Management user");
                        RootRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups").child(userID);

                    }
                    if (retrieveUserType.equals("Driver")) {
                        Log.d("GroupsFragment", "User is normal user");

                        if(snapshot.hasChild("myManagersID")) {
                            managementID = Objects.requireNonNull(snapshot.child("myManagersID").getValue()).toString();
                            Log.d("GroupsFragment", "Retrieving managers Id for group access: " + managementID);
                            RootRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups").child(managementID);
                        }
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
