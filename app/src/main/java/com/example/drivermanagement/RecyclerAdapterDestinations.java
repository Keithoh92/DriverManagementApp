package com.example.drivermanagement;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapterDestinations extends RecyclerView.Adapter<RecyclerAdapterDestinations.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";
    List<String> destinationsList;

    private RecyclerViewAdapter.ItemClickListener mClickListener;


    public RecyclerAdapterDestinations(List<String> destinationsList) {
        this.destinationsList = destinationsList;
    }

    @NonNull
    @Override
    public RecyclerAdapterDestinations.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.destination_rv_row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterDestinations.ViewHolder holder, int position) {
        holder.dest_textview.setText(destinationsList.get(position));
    }

    @Override
    public int getItemCount() {
        return destinationsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView dest_textview;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.rv_imageview);
            dest_textview = itemView.findViewById(R.id.textview_dest);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), destinationsList.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }
    }
}