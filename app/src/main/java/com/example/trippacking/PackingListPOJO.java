package com.example.trippacking;

import java.util.HashMap;
import java.util.Map;

public class PackingListPOJO {
    public Map<String, PackingCategoryPOJO> allCategories;

    public PackingListPOJO(ListsManager.PackingList list) {
        allCategories = new HashMap<>();
        for (String category : list.allCategories.keySet()) {
            if (category == "id") continue;
            allCategories.put(category, new PackingCategoryPOJO(list.allCategories.get(category)));
        }
    }

    public PackingListPOJO(Map<String, Object> data) {
        data = (Map<String, Object>) data.get("allCategories");
        allCategories = new HashMap<>();
        for (String category : data.keySet()) {
            allCategories.put(category, new PackingCategoryPOJO(data.get(category)));
        }
    }
}