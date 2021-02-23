package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindDriversActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView findDriverList;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_drivers);

        UsersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");

        findDriverList = findViewById(R.id.find_drivers_list);
        findDriverList.setLayoutManager(new LinearLayoutManager(this));

        toolbar = findViewById(R.id.toolbar_find_drivers);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Drivers");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(UsersRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, FindDriversViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindDriversViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindDriversViewHolder holder, final int position, @NonNull Contacts model) {
                        if(!model.getImage().equals("")) {
//                        holder.userName.setText(model.getUsername());
//                        Log.d("TAG", "Getting username" +model.getUsername());
                            holder.userName.setText(model.getUsername());
                            Log.d("TAG", "Getting username" +model.getUsername());

                            Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);
                            Log.d("TAG", "Getting Profile pic" + model.getImage());

                        }else{
                            holder.userName.setText(model.getUsername());
//                            holder.profileImage.setImageURI(R.drawable.profile_image);
                        }
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Get user id when user selects user
                                String visit_user_id = getRef(position).getKey();
                                Intent profileIntent = new Intent(FindDriversActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindDriversViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        FindDriversViewHolder viewHolder = new FindDriversViewHolder(view);
                        return viewHolder;
                    }
                };
        findDriverList.setAdapter(adapter);

        adapter.startListening();
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
}