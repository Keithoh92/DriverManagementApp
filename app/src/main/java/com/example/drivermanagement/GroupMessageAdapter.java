package com.example.drivermanagement;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder>
{
    private List<GroupMessages> groupMessagesList;
    private FirebaseAuth fAuth;
    private DatabaseReference UsersRef;

    public GroupMessageAdapter(List<GroupMessages> groupMessagesList)
    {
        this.groupMessagesList = groupMessagesList;
    }
    public class GroupMessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText, receiverFrom;
        public GroupMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverFrom = itemView.findViewById(R.id.group_message_from);
        }
    }

    @NonNull
    @Override
    public GroupMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_group_messages_layout, parent, false);

        fAuth = FirebaseAuth.getInstance();
        return new GroupMessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull GroupMessageViewHolder holder, int position)
    {
        String messageSenderID = fAuth.getCurrentUser().getUid();
        GroupMessages groupmessages = groupMessagesList.get(position);

        String fromUserID = groupmessages.getFrom();
        String fromMessageType = groupmessages.getType();
        String fromMessageUsername = groupmessages.getUsername();

//        UsersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(fromUserID);
//        UsersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.hasChild("image") && !snapshot.child("image").equals(""))
//                {
//                    String receiverImage = snapshot.child("image").getValue().toString();
//                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(holder.receiverProfileImage);//library to load image from firebase storage into circleimageview
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        if(fromMessageType.equals("text"))
        {
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverFrom.setVisibility(View.INVISIBLE);
            holder.senderMessageText.setVisibility(View.INVISIBLE);


            if(fromUserID.equals(messageSenderID))
            {
                holder.senderMessageText.setVisibility(View.VISIBLE);

                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setText(groupmessages.getMessage());
            }
            else{
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverFrom.setVisibility(View.VISIBLE);


                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(groupmessages.getMessage());

//                holder.receiverFrom.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverFrom.setTextColor(Color.BLACK);
                holder.receiverFrom.setText(groupmessages.getUsername());


            }
        }
    }


    @Override
    public int getItemCount() {
        return groupMessagesList.size();
    }


}
