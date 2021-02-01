package com.example.drivermanagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static android.content.ContentValues.TAG;


public class GroupsFragment extends Fragment {

    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdaptor;
    private ArrayList<String> list_of_groups = new ArrayList<>();

    private FirebaseFirestore GroupRef;
    private DatabaseReference RootRef;
    private FirebaseAuth fAuth;

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
        GroupRef = FirebaseFirestore.getInstance();
        RootRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups");

        InitialiseFields();

        RetrieveAndDisplayGroups();

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String groupName = parent.getItemAtPosition(position).toString();

                Intent groupChatIntent = new Intent(getActivity(), GroupChatActivity.class);
                groupChatIntent.putExtra("groupName", groupName);
                startActivity(groupChatIntent);
            }
        });

        return groupFragmentView;
    }

    private void InitialiseFields() {
        list_view = (ListView) groupFragmentView.findViewById(R.id.list_view);
        arrayAdaptor = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_groups);
        list_view.setAdapter(arrayAdaptor);
    }

        private void RetrieveAndDisplayGroups() {
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Set<String> set = new HashSet<>();
                Iterator iterator = snapshot.getChildren().iterator();
                while(iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdaptor.notifyDataSetChanged();
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