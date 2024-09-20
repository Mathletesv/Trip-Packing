package com.example.trippacking.ui.list;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trippacking.ListsManager;
import com.example.trippacking.R;

public class InnerAdapter extends RecyclerView.Adapter<com.example.trippacking.ui.list.InnerAdapter.ViewHolder> {
    private Context ctx;
    private ListFragment fragment;
    private String category;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox innerCheck;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            innerCheck = view.findViewById(R.id.id_inner_check);
        }

        public CheckBox getInnerCheck() {
            return innerCheck;
        }
    }

    public InnerAdapter(ListFragment main, String category) {
        ctx = main.getContext();
        fragment = main;
        this.category = category;
    }

    private ListsManager.PackingList getList() {
        return fragment.parent.listsManager.getCurrent();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.inner_recycler, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Pair<String, Boolean> element = getList().getCategoryAt(category, position);
        Log.d("LOG_VALS", "test" + position + "" + element.first);
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getInnerCheck().setText(element.first);
        viewHolder.getInnerCheck().setChecked(element.second);
        viewHolder.getInnerCheck().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Pair<String, Boolean> element = getList().getCategoryAt(category, viewHolder.getAdapterPosition());
                getList().getCategory(category).updateChecked(element.first, isChecked);
                View creator = fragment.listsView.getChildAt(fragment.getPos(category));
                if (creator == null) return;
                ProgressBar progressBar = creator.findViewById(R.id.id_inner_progress);
                progressBar.setProgress(getList().getCategory(category).progress());
                //fragment.parent.listsManager.save();
//                viewHolder.getInnerCheck().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        fragment.listAdapter.notifyItemChanged(fragment.getPos(category));
//                    }
//                });
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return getList().getCategorySize(category);
    }
}
