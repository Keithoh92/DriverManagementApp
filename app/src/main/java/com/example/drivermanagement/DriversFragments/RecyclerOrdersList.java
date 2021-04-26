package com.example.drivermanagement.DriversFragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.drivermanagement.R;

import java.util.List;

public class RecyclerOrdersList extends RecyclerView.Adapter<RecyclerOrdersList.ViewHolder>{

    private List<String> listOfOrders;

    private ItemClickListener mClickListener;

    public RecyclerOrdersList(List<String> listOfOrders){
        this.listOfOrders = listOfOrders;
//        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView myOrdersTextView;

        public ViewHolder(View v) {
            super(v);

            myOrdersTextView = v.findViewById(R.id.textview_my_orders);
            v.setOnClickListener(this);
//            getItemCount();
        }

        @Override
        public void onClick(View v) {
            if(mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
//            String fds = scannedList.get(getAdapterPosition());
            Toast.makeText(v.getContext(), listOfOrders.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerOrdersList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.orders_list_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder; //create instance of above inner class (ViewHolder)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerOrdersList.ViewHolder holder, int position) {
        holder.myOrdersTextView.setText(listOfOrders.get(position));
        //holder.recipient.setText(mData.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listOfOrders.size();
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
