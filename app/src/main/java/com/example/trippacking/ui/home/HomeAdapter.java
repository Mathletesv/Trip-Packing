package com.example.trippacking.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trippacking.R;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private Context ctx;
    private HomeFragment home;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView inner_text;
        public final ProgressBar inner_progress;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            inner_text = view.findViewById(R.id.id_inner_list_name);
            inner_progress = view.findViewById(R.id.id_inner_progress);
        }

        public TextView getTextView() {
            return inner_text;
        }
    }

    public HomeAdapter(HomeFragment main) {
        ctx = main.getContext();
        home = main;
    }

    private List<String> getList() {
        return home.parent.listsManager.listNames;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.home_recycler, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String list = getList().get(position);
        viewHolder.getTextView().setText(list);
        viewHolder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.parent.listsManager.open(list);
                home.parent.navigate(R.id.nav_list);
            }
        });
        int progress = (int) (Math.random() * 6);
        viewHolder.inner_progress.setMax(home.parent.listsManager.getTotal(list));
        viewHolder.inner_progress.setProgress(home.parent.listsManager.getProgress(list), true);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return getList().size();
    }
}
