package com.example.drivermanagement;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddDriversFragment extends Fragment {


   Activity listener;
   EditText findDriversSearchbar;
   Button findDriversButton, cancel;
   String foundDriver;
   Fragment addDriversFragment;
   FragmentManager fm;
   String inputSearch;

   private FirebaseAuth fAuth;
   private DatabaseReference driverRef;

   private AddDriversFragmentToActivity mCallback;

    public AddDriversFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof MessagesActivity){
            this.listener = (MessagesActivity) context;
        }
        try{
            mCallback = (AddDriversFragment.AddDriversFragmentToActivity) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "Must implement listener");
        }

    }

    @Override
    public void onStart() {
        super.onStart();

//        foundDriversFragment = fm.findFragmentById(R.id.add_drivers_fragment);
//        fm.beginTransaction()
//                .show(foundDriversFragment)
//                .commit();
        Log.d("AddDriversFrag", "onStart Add drivers called");
//        findDriversSearchbar.callOnClick();
        findDriversSearchbar = getView().findViewById(R.id.driver_search_bar);


        findDriversSearchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputSearch = s.toString();
            }
        });

        findDriversButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputSearch.equals("")) {
                    Toast.makeText(getContext(), "Please enter a username to search", Toast.LENGTH_SHORT).show();
                    Log.d("testing", "Nothing in search bar");
                } else {
                    String enteredUsername = inputSearch;
                    Log.d("testing", "Retrieved from search bar: "+enteredUsername);
                    Log.d("testing", "Querying database");
                    driverRef.orderByChild("username").equalTo(enteredUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Log.d("testing", "Found match in users DB for username: "+enteredUsername);
                                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                                    String foundDriver1 = childDataSnapshot.getKey();
                                    Log.d("testing", "Retrieving users ID");
                                    Log.d("AddDriversFragment", "Returned user id: " + foundDriver1);
                                    Log.d("testing", "Sending the user id to messages activity");
                                    Log.d("AddDriversFragment", "Before send check if ID is null? ID: " +foundDriver1);
                                    if(foundDriver1 != null){
                                        Log.d("AddDriversFragment", "Not null, Sending ID " +foundDriver1);
                                        mCallback.comm(foundDriver1);
                                    }
//                                    FoundDriversFragment foundDrive = new FoundDriversFragment();
//                                    foundDrive = (FoundDriversFragment) fm.findFragmentById(R.id.found_driver_result);
//                                    Bundle foundDriverBundle = new Bundle();
//                                    foundDriverBundle.putString("foundDriver", foundDriver1);
//                                    assert foundDrive != null;
//                                    foundDrive.setArguments(foundDriverBundle);
//                                    fm.beginTransaction()
//                                            .replace(R.id.found_driver_result, foundDrive)
//                                            .show(foundDrive)
//                                            .commit();
                                    findDriversButton.setVisibility(View.INVISIBLE);
                                    cancel.setVisibility(View.INVISIBLE);
//                                    fm.beginTransaction()
//                                            .show(fdf)
//                                            .commit();
                                }
                            } else {
                                Toast.makeText(getContext(), "No Driver with this username is using this application", Toast.LENGTH_SHORT).show();
                                Log.d("AddDriversFragment", "No driver found in Database with this udername");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction()
                        .hide(addDriversFragment)
                        .remove(addDriversFragment)
                        .commit();
                Log.d("testing", "User cancelled search for driver, removing found drivers fragment");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("AddDriversFrag", "onResume Add drivers called");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("AddDriversFrag", "onPause Add drivers called");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_drivers, container, false);

//        foundDriversFragment = fm.findFragmentById(R.id.found_driver_result);
//        addDriversFragment = fm.findFragmentById(R.id.add_drivers_fragment);
        fm = getChildFragmentManager();
        findDriversButton = view.findViewById(R.id.find_drivers_button);
        findDriversSearchbar = view.findViewById(R.id.driver_search_bar);
//        findDriversSearchbar.setBackgroundColor();

//        foundDriversFragment = fm.findFragmentById(R.id.found_driver_result);



        cancel = view.findViewById(R.id.cancel_button);
//        foundDriversFragment = new Fragment();
//        addDriversFragment = new Fragment();


        driverRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");

        return view;
    }
    public interface AddDriversFragmentToActivity{
        public void comm(String foundDriversID);
    }
//    private void sendUsersID(String foundDriversID){mCallback.comm(foundDriversID);}

    @Override
    public void onDetach() {
//        mCallback = null;
        super.onDetach();
    }
}
