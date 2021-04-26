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


public class GroupsFragment extends Fragment {

    ValueEventListener listener;
    private View groupFragmentView;
    private RecyclerView list_view;
//    private ArrayAdapter<String> arrayAdaptor;
//    private ArrayList<String> list_of_groups = new ArrayList<>();

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


//        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String groupName = parent.getItemAtPosition(position).toString();
//
//                Intent groupChatIntent = new Intent(getActivity(), GroupChatActivity.class);
//                groupChatIntent.putExtra("groupName", groupName);
//                if(!isNormalUser){
//                    groupChatIntent.putExtra("usersID", userID);
//                }else{
//                    groupChatIntent.putExtra("managersID", managementID);
//                }
//                startActivity(groupChatIntent);
//            }
//        });

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
//                    RootRef.child(currentUserID).child("GroupInfo").child(groupName).setValue(createGroup).addOnCompleteListener(new OnCompleteListener<Void>() {

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
//        RootRef.removeEventListener(listener);

    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser){
//            listener = new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (!snapshot.exists()) {
//                        //AND if the user is normal driver display this message
//                        if (isNormalUser) {
//                            Toast.makeText(getContext(), "Your manager has not created any groups yet or you are not yet in your managements system", Toast.LENGTH_LONG).show();
//                            //ELSE if its management user display this message
//                        } else {
//                            Toast.makeText(getContext(), "You have not yet created any Groups yet, please go to create group in menu", Toast.LENGTH_LONG).show();
//                        }
//                    } else {
//                        Set<String> set = new HashSet<>();
//                        Iterator iterator = snapshot.getChildren().iterator();
//                        while (iterator.hasNext()) {
//                            set.add(((DataSnapshot) iterator.next()).getKey());
//                        }
//                        list_of_groups.clear();
//                        list_of_groups.addAll(set);
//                        arrayAdaptor.notifyDataSetChanged();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            };
//            RootRef.addValueEventListener(listener);
//
//        }
//    }


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
                        managementID = Objects.requireNonNull(snapshot.child("myManagersID").getValue()).toString();
                        Log.d("GroupsFragment", "Retrieving managers Id for group access: "+managementID);
                        RootRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups").child(managementID);
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
//    private void RetrieveAndDisplayGroups() {
//        FirebaseUser user = fAuth.getCurrentUser();
////        DocumentReference df = GroupRef.collection("Groups").document();
////        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
////            @Override
////            public void onSuccess(DocumentSnapshot documentSnapshot) {
////                String groupName = documentSnapshot.getString("Group Name");
////
////                list_of_groups.clear();
////                list_of_groups.add(groupName);
////                arrayAdaptor.notifyDataSetChanged();
////            }
////        });
////        DocumentReference df = collection("cities")
//        GroupRef.collection("Groups")
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            list_of_groups.clear();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("TAG", document.getId() + " => " + document.getData());
//                                String stripped = document.getData().toString().replace("{Group Name=", "");
//                                String stripped2 = stripped.replace("}", "");
//                                Set<String> set = new HashSet<>();
//                                set.add(stripped2);
////                                list_of_groups.clear();
//                                list_of_groups.addAll(set);
//                                arrayAdaptor.notifyDataSetChanged();
//                            }
//
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                        list_of_groups.clear();
//                        list_of_groups.addAll(set);
//                    }
//                });

//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });

//                Set<String> set = new HashSet<>();
//                Iterator iterator = (Iterator) GroupRef.collection("Groups").document("My Created Groups");


//                String groupName = documentSnapshot.getString("Group Name");
//                list_of_groups.clear();
//                list_of_groups.add(groupName);
//                arrayAdaptor.notifyDataSetChanged();


//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getActivity(), "You are not a memeber of any groups yet", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}