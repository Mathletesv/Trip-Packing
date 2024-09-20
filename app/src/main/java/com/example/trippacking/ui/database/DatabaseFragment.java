package com.example.trippacking.ui.database;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trippacking.NavActivity;
import com.example.trippacking.PackingListPOJO;
import com.example.trippacking.R;
import com.example.trippacking.databinding.FragmentDatabaseBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DatabaseFragment extends Fragment {

    private FragmentDatabaseBinding binding;
    public List<DocumentSnapshot> list;
    public FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        DatabaseViewModel DatabaseViewModel =
//                new ViewModelProvider(this).get(DatabaseViewModel.class);

        binding = FragmentDatabaseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        list = new ArrayList<>();

        RecyclerView listsView = root.findViewById(R.id.id_recycler_items);
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        listsView.setLayoutManager(new LinearLayoutManager(getContext()));
        listsView.setAdapter(databaseAdapter);

        db = FirebaseFirestore.getInstance();
        db.collection("lists").limit(20).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                list = queryDocumentSnapshots.getDocuments();
                Log.d("TEST_DOC", list.get(0).getId());
                databaseAdapter.notifyDataSetChanged();
            }
        });

//        final TextView textView = binding.textDatabase;
//        DatabaseViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void getList(int index) {
        String text = list.get(index).getId().split(":")[0];
        PackingListPOJO pojo = new PackingListPOJO(list.get(index).getData());
        NavActivity parent = (NavActivity) getActivity();
        parent.listsManager.addPOJO(text, pojo);
        parent.listsManager.open(text);
        parent.listsManager.save();
        parent.navigate(R.id.nav_list);
    }
}