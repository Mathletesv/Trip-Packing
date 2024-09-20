package com.example.trippacking.ui.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trippacking.R;

public class DatabaseAdapter extends RecyclerView.Adapter<com.example.trippacking.ui.database.DatabaseAdapter.ViewHolder> {
    private Context ctx;
    private DatabaseFragment database;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView inner_text;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            inner_text = view.findViewById(R.id.id_inner_text);;
        }

        public TextView getTextView() {
            return inner_text;
        }
    }

    public DatabaseAdapter(DatabaseFragment main) {
        ctx = main.getContext();
        database = main;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.database_recycler, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String text = database.list.get(position).getId();
        text = text.split(":")[0];
        viewHolder.getTextView().setText(text);
        viewHolder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getList(viewHolder.getAdapterPosition());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return database.list.size();
    }
}