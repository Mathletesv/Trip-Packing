package com.example.trippacking;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PackingCategoryPOJO {
    public List<String> allItems;

    public PackingCategoryPOJO(ListsManager.PackingList.PackingCategory category) {
        allItems = new ArrayList<>();
        allItems.addAll(category.allItems.keySet());
    }

    public PackingCategoryPOJO(Object o) {
        HashMap<String, Object> data = (HashMap<String, Object>) o;
        Log.d("TEST_POJO", data.keySet().size() + "");
        Log.d("TEST_POJO", data.keySet().iterator().next());
        allItems = (List<String>) data.get("allItems");
        Log.d("TEST_POJO", allItems.size() + "");
    }
}
