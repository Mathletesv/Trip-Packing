package com.example.trippacking.ui.list;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trippacking.ListsManager;
import com.example.trippacking.R;

public class ListAdapter extends RecyclerView.Adapter<com.example.trippacking.ui.list.ListAdapter.ViewHolder> {
    private Context ctx;
    private ListFragment fragment;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView innerText;
        public final ProgressBar innerProgress;
        public final RecyclerView innerList;
        public final Button innerBtn;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            innerText = view.findViewById(R.id.id_inner_list_name);
            innerProgress = view.findViewById(R.id.id_inner_progress);
            innerList = view.findViewById(R.id.id_inner_recycler);
            innerBtn = view.findViewById(R.id.id_inner_btn);
        }

        public TextView getTextView() {
            return innerText;
        }
    }

    public ListAdapter(ListFragment main) {
        ctx = main.getContext();
        fragment = main;
    }

    public ListsManager.PackingList getList() {
        return fragment.parent.listsManager.getCurrent();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_recycler, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d("HIT_SPOT", position + " in main");
        viewHolder.innerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                Log.d("TEST_CLICK", viewHolder.getAdapterPosition() + " " + (getItemCount() - 1));
                boolean last = (viewHolder.getAdapterPosition() == (getItemCount() - 1));
                final EditText editText = new EditText(ctx);
                editText.setHint((last ? "Category Name" : "Item Name"));
                AlertDialog.Builder alert_builder = new AlertDialog.Builder(ctx);
                alert_builder.setView(editText);
                alert_builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        if (text.isEmpty()) return;
                        if (last) {
                            getList().add(text);
                            viewHolder.getTextView().post(new Runnable() {
                                @Override
                                public void run() {
                                    fragment.listAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        else {
                            Log.d("TEST_POS", pos + "");
                            getList().add(getList().getCategories().get(pos), text);
                            viewHolder.innerList.getAdapter().notifyDataSetChanged();
                            viewHolder.getTextView().post(new Runnable() {
                                @Override
                                public void run() {
                                    fragment.listAdapter.notifyItemChanged(pos);
                                }
                            });
                        }

                    }
                });
                alert_builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editText.setText("");
                    }
                });
                alert_builder.show();
            }
        });
        if (position == (getItemCount() - 1)) {
            viewHolder.innerList.setVisibility(View.GONE);
            viewHolder.innerText.setText("Add Category");
            viewHolder.innerProgress.setMax(1);
            viewHolder.innerProgress.setProgress(1);
            return;
        }


        String category = getList().getCategories().get(viewHolder.getAdapterPosition());
        LinearLayoutManager layoutManager = new LinearLayoutManager(viewHolder.innerList.getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setInitialPrefetchItemCount(getList().getCategorySize(category));

        InnerAdapter innerAdapter = new InnerAdapter(fragment, category);

        viewHolder.innerList.setLayoutManager(new LinearLayoutManager(viewHolder.innerList.getContext()));
        viewHolder.innerList.setAdapter(innerAdapter);
        viewHolder.innerList.setRecycledViewPool(viewPool);
        Log.d("TEST_INNER", viewHolder.innerList.getAdapter().getItemCount() + "yes");
        Log.d("TEST_PROGRESS", getList().getCategory(category).progress() + " " + getList().getCategory(category).length());
        viewHolder.innerProgress.setMax(getList().getCategory(category).length());
        viewHolder.innerProgress.setProgress(getList().getCategory(category).progress());
        viewHolder.getTextView().setText(getList().getCategories().get(viewHolder.getAdapterPosition()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return getList().count() + 1;
    }
}
