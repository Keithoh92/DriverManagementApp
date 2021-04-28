package com.example.drivermanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*

    RECENT MESSAGES ADAPTOR FOR RECENT MESSAGES FRAGMENT ON DRIVERS DASHBOARD

 */
public class RecyclerRecentMessagesList extends RecyclerView.Adapter<RecyclerRecentMessagesList.ViewHolder>{

    private List<String> listOfRecentMessages;

    private ItemClickListener mClickListener;

    public RecyclerRecentMessagesList(List<String> listOfRecentMessages){
        this.listOfRecentMessages = listOfRecentMessages;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView recent_messages_textview;

        public ViewHolder(View v) {
            super(v);

            recent_messages_textview = v.findViewById(R.id.textview_recent_messages);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
            Toast.makeText(v.getContext(), listOfRecentMessages.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerRecentMessagesList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recent_messages_list_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder; //create instance of above inner class (ViewHolder)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerRecentMessagesList.ViewHolder holder, int position) {
        holder.recent_messages_textview.setText(listOfRecentMessages.get(position));
        //holder.recipient.setText(mData.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listOfRecentMessages.size();
    }


    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
