package com.example.trippacking;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListsManager {
    private Context ctx;
    private JSONObject data;
    public List<String> listNames;
    private Map<String, PackingList> allLists;
    public String workingWith;

    public ListsManager(Context ctx) {
        this.ctx = ctx;
        listNames = new ArrayList<>();
        allLists = new HashMap<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ctx.openFileInput("lists.txt")));
            String jsonData = bufferedReader.readLine();
            bufferedReader.close();
            if (jsonData == null) {
                data = new JSONObject();
                data.put("lists", new JSONArray());
                return;
            }
            data = new JSONObject(jsonData);
            if (data.has("lists")) {
                JSONArray listArray = data.getJSONArray("lists");
                for (int i = 0; i < listArray.length(); i++) {
                    listNames.add(listArray.getString(i));
                }
            }
            for (int i = 0; i < listNames.size(); i++) {
                allLists.put(listNames.get(i), new PackingList(data.getJSONObject(listNames.get(i))));
            }
        }
        catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void open(String list) {
        workingWith = list;
        if (allLists.get(list) == null) {
            allLists.put(list, new PackingList((JSONObject) null));
            try {
                data.put("lists", data.getJSONArray("lists").put(list));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            listNames.add(list);
        }
    }

    public void addAndOpen(String name, String json) {
        if (allLists.get(name) != null) return;
        workingWith = name;
        listNames.add(name);
        try {
            allLists.put(name, new PackingList(new JSONObject(json)));
            data.put("lists", data.getJSONArray("lists").put(name));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        save();
    }

    public void add(String category, String name) {
        allLists.get(workingWith).add(category, name);
    }

    public void save() {
        if (workingWith == null || allLists.get(workingWith) == null) return;
        try {
            data.put(workingWith, allLists.get(workingWith).data);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(ctx.openFileOutput("lists.txt", MODE_PRIVATE)));
            bufferedWriter.write(data.toString());
            bufferedWriter.close();
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PackingList getCurrent() {
        return allLists.get(workingWith);
    }

    public int getTotal(String list) {
        if (allLists.get(list) == null) return 1;
        int sum = 0;
        for (String category : allLists.get(list).getCategories()) {
            sum += allLists.get(list).count(category);
        }
        return sum;
    }

    public int getProgress(String list) {
        if (allLists.get(list) == null) return 1;
        int sum = 0;
        for (String category : allLists.get(list).getCategories()) {
            sum += allLists.get(list).progress(category);
        }
        return sum;
    }

    public String workingWithId() {
        return workingWith + ":" + allLists.get(workingWith).id;
    }

    public void addPOJO(String name, PackingListPOJO pojo) {
        if (allLists.get(name) == null) listNames.add(name);
        allLists.put(name, new PackingList(pojo));
        try {
            data.put("lists", data.getJSONArray("lists").put(name));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(String name) {
        workingWith = null;
        allLists.remove(name);
        while (listNames.remove(name)) {
        };
        data.remove(name);
        JSONArray toReplace = new JSONArray();
        try {
            JSONArray lists = data.getJSONArray("lists");
            for (int i = 0; i < lists.length(); i++) {
                if (!lists.getString(i).equals(name)) toReplace.put(lists.getString(i));
            }
            data.put("lists", toReplace);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public class PackingList {
        public JSONObject data;
        public Map<String, PackingCategory> allCategories;
        public int id;

        public PackingList(PackingListPOJO pojo) {
            allCategories = new HashMap<>();
            data = new JSONObject();
            id = (int) (Math.random() * 1000000);
            try {
                data.put("id", id);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            for (String key : pojo.allCategories.keySet()) {
                if (key == "id") continue;
                allCategories.put(key, new PackingCategory(pojo.allCategories.get(key)));
                try {
                    data.put(key, allCategories.get(key).data);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public PackingList(JSONObject categories) {
            id = (int) (Math.random() * 1000000);
            allCategories = new HashMap<String, PackingCategory>();
            if (categories == null) {
                data = new JSONObject();
                try {
                    data.put("id", id);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            data = categories;
            try {
                if (data.has("id")) id = data.getInt("id");
                else data.put("id", id);
                JSONArray names = categories.names();
                if (names == null) return;
                for (int i = 0; i < names.length(); i++) {
                    if (Objects.equals(names.getString(i), "id")) continue;
                    allCategories.put(names.getString(i), new PackingCategory(categories.getJSONObject(names.getString(i))));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        public void add(String category, String name) {
            if (allCategories.get(category) == null) allCategories.put(category, new PackingCategory((JSONObject) null));
            updateChecked(category, name, false);
        }

        public void add(String category) {
            if (allCategories.get(category) == null) allCategories.put(category, new PackingCategory((JSONObject) null));
            save(category);
        }

        public void updateChecked(String category, String name, boolean checked) {
            allCategories.get(category).updateChecked(name, checked);
            save(category);
        }

        public void remove(String category, String name) {
            allCategories.get(category).remove(name);
            save(category);
        }

        public void remove(String category) {
            allCategories.remove(category);
            data.remove(category);
        }

        public void save(String category) {
            if (allCategories.get(category) == null) return;
            try {
                data.put(category, allCategories.get(category).data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        public List<String> getCategories() {
            List<String> categories = new ArrayList<>(allCategories.keySet());
            Collections.sort(categories);
            return categories;
        }

        public int count(String category) {
            return allCategories.get(category).length();
        }

        public int progress(String category) {
            return allCategories.get(category).progress();
        }

        public PackingCategory getCategory(String category) {
            return allCategories.get(category);
        }

        public Pair<String, Boolean> getCategoryAt(String category, int position) {
            List<String> items = new ArrayList<>(allCategories.get(category).allItems.keySet());
            Collections.sort(items);
            return new Pair<>(items.get(position), allCategories.get(category).get(items.get(position)));
        }

        public int getCategorySize(String category) {
            return allCategories.get(category).length();
        }

        public int count() {
            return allCategories.size();
        }

        public class PackingCategory {
            public JSONObject data;
            public Map<String, Boolean> allItems;

            public PackingCategory(PackingCategoryPOJO pojo) {
                data = new JSONObject();
                allItems = new HashMap<>();
                for (String key : pojo.allItems) {
                    try {
                        data.put(key, false);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    allItems.put(key, false);
                }
            }

            public PackingCategory(JSONObject items) {
                allItems = new HashMap<>();
                if (items == null) {
                    data = new JSONObject();
                    return;
                }
                data = items;
                try {
                    JSONArray names = items.names();
                    if (names == null) return;
                    for (int i = 0; i < names.length(); i++) {
                        allItems.put(names.getString(i), items.getBoolean(names.getString(i)));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            public boolean get(String name) {
                return allItems.get(name);
            }

            public boolean has(String name) {
                return allItems.get(name) != null;
            }

            public int length() {
                return allItems.size();
            }

            public void remove(String name) {
                allItems.remove(name);
                data.remove(name);
            }

            public void updateChecked(String name, boolean checked) {
                allItems.put(name, checked);
                try {
                    data.put(name, checked);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            public int progress() {
                int sum = 0;
                for (String key : allItems.keySet()) {
                    if (allItems.get(key)) sum++;
                }
                return sum;
            }
        }
    }
}
