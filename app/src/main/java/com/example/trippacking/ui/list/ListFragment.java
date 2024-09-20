package com.example.trippacking.ui.list;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trippacking.NavActivity;
import com.example.trippacking.PackingListPOJO;
import com.example.trippacking.R;
import com.example.trippacking.databinding.FragmentListBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    NavActivity parent;
    Context ctx;
    RecyclerView listsView;
    ListAdapter listAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        GalleryViewModel galleryViewModel =
//                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        parent = (NavActivity) getActivity();
        if (parent.listsManager.workingWith == null) parent.navigate(R.id.nav_home);

//        final TextView textView = binding.textList;
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        ctx = getContext();

        listsView = root.findViewById(R.id.id_recycler_items);
        listAdapter = new ListAdapter(this);
        listsView.setLayoutManager(new LinearLayoutManager(ctx));
        listsView.setAdapter(listAdapter);
        listAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("NEW_CHANGE", "CHANGE");
                parent.listsManager.save();
            }
        });

        root.findViewById(R.id.id_home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.navigate(R.id.nav_home);
            }
        });

        TextView listName = root.findViewById(R.id.id_list_name);
        listName.setText(parent.listsManager.workingWith);

        FloatingActionButton upload = root.findViewById(R.id.id_upload_btn);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                PackingListPOJO myList = new PackingListPOJO(parent.listsManager.getCurrent());
                db.collection("lists").document(parent.listsManager.workingWithId()).set(myList);
            }
        });

        FloatingActionButton delete = root.findViewById(R.id.id_delete_btn);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.navigate(R.id.nav_home);
                parent.listsManager.remove(parent.listsManager.workingWith);
                parent.listsManager.save();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        parent.listsManager.save();
    }

    public int getPos(String category) {
        return listAdapter.getList().getCategories().indexOf(category);
    }
}