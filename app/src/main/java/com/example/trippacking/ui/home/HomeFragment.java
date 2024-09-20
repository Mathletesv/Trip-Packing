package com.example.trippacking.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trippacking.NavActivity;
import com.example.trippacking.R;
import com.example.trippacking.databinding.FragmentHomeBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;

public class HomeFragment extends Fragment {

    Button createEmpty;
    Button createAi;
    Button createImport;

    RecyclerView listsView;
    HomeAdapter packingLists;

    private FragmentHomeBinding binding;
    Context ctx;
    NavActivity parent;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //HomeViewModel homeViewModel =
        //new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textView;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        ctx = getContext();
        parent = (NavActivity) getActivity();

        createEmpty = root.findViewById(R.id.id_create_empty);
        createAi = root.findViewById(R.id.id_create_ai);

        listsView = root.findViewById(R.id.id_lists_view);
        packingLists = new HomeAdapter(HomeFragment.this);
        listsView.setLayoutManager(new LinearLayoutManager(ctx));
        listsView.setAdapter(packingLists);
        packingLists.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                parent.listsManager.save();
            }
        });

        createEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edit_text = new EditText(ctx);
                AlertDialog.Builder create_list_dialog = new AlertDialog.Builder(ctx);
                create_list_dialog.setView(edit_text).setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = edit_text.getText().toString();
                        if (text.isEmpty()) text = "New List";
                        parent.listsManager.open(text);
                        packingLists.notifyItemInserted(parent.listsManager.listNames.size() - 1);
                        parent.navigate(R.id.nav_list);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });

        createAi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder create_ai_dialog = new AlertDialog.Builder(ctx);
                View ai_view = LayoutInflater.from(ctx).inflate(R.layout.ai_dialog, null);
                create_ai_dialog.setView(ai_view).setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText name = ai_view.findViewById(R.id.id_ai_list_name);
                        EditText people = ai_view.findViewById(R.id.id_ai_list_people);
                        EditText description = ai_view.findViewById(R.id.id_ai_list_description);
                        generateAIList(name.getText().toString(), Integer.parseInt(people.getText().toString()), description.getText().toString());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
        return root;
    }

    void generateAIList(String name, int people, String description) {
        String apiKey = "AIzaSyBGE-TeifdmjkPN3oEZPc0lwEq2Q0flJls";

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", apiKey);

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText("Give me a packing list named " + name + " for " + people + " people for the following description.")
                .addText(description)
                .addText("Provide the packing list as a raw json object that can be converted from a string in the following format")
                .addText("{\"category1\":{\"item0\":false},\"category2\":{\"item1\":false,\"item2\":false,\"item3\":false}}")
                .addText("Please do not include ```json at the beginning or ``` at the end.")
                .build();

        Executor executor = ctx.getMainExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                parent.listsManager.addAndOpen(name, resultText);
                packingLists.notifyItemInserted(parent.listsManager.listNames.size() - 1);
                parent.navigate(R.id.nav_list);
                Log.d("AI_TEXT", resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("FAIL", "yes");
                t.printStackTrace();
            }
        }, executor);
    }

    @Override
    public void onDestroyView() {
        parent.listsManager.save();
        super.onDestroyView();
        binding = null;
    }
}