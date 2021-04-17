//package com.example.drivermanagement;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class FoundDriversFragment extends Fragment implements MessagesActivity.ActivityToFragment {
//
//    Activity listener;
//    CircleImageView circleImageView;
//    TextView username;
//    Button cancel, add;
//
//    FragmentManager fm;
//    Fragment foundDriversFragment, addDriversFragment;
//
//    private DatabaseReference usersRef;
//    private String receiverUsersID;
//
//    private FragmentToActivity2 mCallback;
//
//    public FoundDriversFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        Log.d("foundDriversFrag", "onStart Called");
//
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        Log.d("foundDriversFrag", "onAttach Called");
//        if(context instanceof MessagesActivity){
//            this.listener = (MessagesActivity) context;
//        }
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d("foundDriversFrag", "onCreate Called");
//
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        Log.d("foundDriversFrag", "onCreateView Called");
//
//        View view = inflater.inflate(R.layout.fragment_found_drivers, container, false);
////        return inflater.inflate(R.layout.fragment_found_drivers, container, false);
//        circleImageView = view.findViewById(R.id.users_profile_image1);
//        username = view.findViewById(R.id.user_profile_name1);
//        cancel = view.findViewById(R.id.cancel_found_driver);
//        add = view.findViewById(R.id.add_driver_found);
////        foundDriversFragment = new Fragment();
////        addDriversFragment = new Fragment();
//
//
//        fm = getChildFragmentManager();
//        foundDriversFragment = fm.findFragmentById(R.id.add_drivers_fragment);
//
//
//        usersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
//        Bundle receivedID = this.getArguments();
//        if(receivedID != null){
//            receiverUsersID = getArguments().getString("foundDriver");
//            Log.d("FoundDriverFrag", "User id found: "+receiverUsersID+", Calling retrieve user info method");
//            RetrieveUsersInfo();
//        }
//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                sendID(receiverUsersID, username.getText().toString());
//                Log.d("FoundDriversFragment", "Sending userID retieved: "+receiverUsersID+" and username: "+username.getText().toString()+" to Messages activity");
//                fm.beginTransaction()
//                        .hide(addDriversFragment)
//                        .hide(foundDriversFragment)
//                        .remove(addDriversFragment)
//                        .remove(foundDriversFragment)
//                        .commit();
//                Toast.makeText(getContext(), "Adding "+username+" to your Drivers", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fm.beginTransaction()
//                        .hide(foundDriversFragment)
//                        .hide(addDriversFragment)
//                        .remove(foundDriversFragment)
//                        .remove(addDriversFragment)
//                        .commit();
//                Log.d("FoundDriversFrag", "User cancelled adding driver returned");
//            }
//        });
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }
//
//    private void RetrieveUsersInfo()
//    {
//        Log.d("FoundDriversFragment", "Retrieving users info");
//        usersRef.child(receiverUsersID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//                String username1 = snapshot.child("username").getValue().toString();
//                username.setText(username1);
//                Log.d("FoundDriversFragment", "Retrieved users username and setting username textview: " + username1);
//
//                if((!snapshot.child("image").getValue().toString().equals("")))
//                {
//                    Log.d("TAG", "Profile image reference exists for this user" +snapshot.child("image").getValue().toString());
//                    String imageUri = snapshot.child("image").getValue().toString();
//                    Picasso.get().load(imageUri).placeholder(R.drawable.profile_image).into(circleImageView);//library to load image from firebase storage into circleimageview
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    @Override
//    public void sendToFrag(String id) {
//        if(id != null) {
//            receiverUsersID = id;
//            RetrieveUsersInfo();
//        }
//    }
//
//    public interface FragmentToActivity2{
//        public void communicate(String userID, String userName);
//    }
//    private void sendID(String userID, String userName){mCallback.communicate(userID, userName);}
//
//    @Override
//    public void onDetach() {
//        mCallback = null;
//        super.onDetach();
//        this.listener = null;
//        Log.d("foundDriversFrag", "onDetach Called");
//
//
//    }
//}