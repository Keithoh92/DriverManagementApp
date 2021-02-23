package com.example.drivermanagement;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapterDestinations extends RecyclerView.Adapter<RecyclerAdapterDestinations.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";
    List<String> destinationsList;

    public RecyclerAdapterDestinations(List<String> destinationsList){
        this.destinationsList = destinationsList;
    }
    @NonNull
    @Override
    public RecyclerAdapterDestinations.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterDestinations.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
    }
}
