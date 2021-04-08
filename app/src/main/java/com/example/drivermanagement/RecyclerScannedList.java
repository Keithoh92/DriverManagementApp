package com.example.drivermanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerScannedList extends RecyclerView.Adapter<RecyclerScannedList.ViewHolder>{

    private List<String> scannedList;

    private ItemClickListener mClickListener;

    public RecyclerScannedList(List<String> scannedList){
        this.scannedList = scannedList;
//        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView scanned_textview;

        public ViewHolder(View v) {
            super(v);

            scanned_textview = v.findViewById(R.id.textview_scanned);
            v.setOnClickListener(this);
//            getItemCount();
        }

        @Override
        public void onClick(View v) {
            if(mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
//            String fds = scannedList.get(getAdapterPosition());
            Toast.makeText(v.getContext(), scannedList.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerScannedList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.scanned_list_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder; //create instance of above inner class (ViewHolder)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerScannedList.ViewHolder holder, int position) {
        holder.scanned_textview.setText(scannedList.get(position));
        //holder.recipient.setText(mData.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return scannedList.size();
    }


    // convenience method for getting data at click position
//    String getItem(int id) {
//        return mData.get(id);
//    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
