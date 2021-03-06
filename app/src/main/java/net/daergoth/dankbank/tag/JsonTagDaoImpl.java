package net.daergoth.dankbank.tag;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonTagDaoImpl implements TagDao {
    private static final String FILENAME = "tags.json";

    private final File saveFile;

    private final Gson gson;

    private Map<Integer, Tag> cachedTags;

    public JsonTagDaoImpl(File saveDirectory, Gson gson) {
        this.saveFile = new File(saveDirectory, FILENAME);

        this.gson = gson;

        this.cachedTags = loadTags();
    }

    @Override
    public Collection<Tag> getAllTags() {
        return Collections.unmodifiableCollection(cachedTags.values());
    }

    @Override
    public void saveMeme(Tag t) {
        if (t == null) {
            throw new NullPointerException("Tag cannot be null!");
        }

        if (!cachedTags.containsValue(t)) {
            if (t.getId() == null) {
                t.setId(cachedTags.size());
            }

            cachedTags.put(t.getId(), t);

            saveChanges();
        }
    }

    @Override
    public Tag getTagById(int id) {
        return cachedTags.get(id);
    }

    @Override
    public Tag getTagByName(String name) {
        for (Tag t : cachedTags.values()) {
            if (t.getTagName().equals(name)) {
                return t;
            }
        }

        return null;
    }

    private void saveChanges() {
        final StoredTags storedTags = new StoredTags();

        storedTags.setTags(new ArrayList<Tag>(this.cachedTags.values()));

        try {
            final JsonWriter jsonWriter = new JsonWriter(new FileWriter(saveFile));

            gson.toJson(storedTags, StoredTags.class, jsonWriter);

            jsonWriter.close();

        } catch (IOException e) {
            Log.e(JsonTagDaoImpl.class.getName(), e.getMessage());
        }
    }

    private Map<Integer, Tag> loadTags() {
        if (!saveFile.exists()) {
            return new HashMap<>();
        }

        try {
            final JsonReader jsonReader = new JsonReader(new FileReader(saveFile));

            final StoredTags storedTags = gson.fromJson(jsonReader, StoredTags.class);

            return convertStoredTags(storedTags);
        } catch (FileNotFoundException e) {
            // this branch should not be reached at all

            return new HashMap<>();
        }
    }

    private Map<Integer, Tag> convertStoredTags(StoredTags storedTags) {
        final Map<Integer, Tag> result = new HashMap<>(storedTags.getTags().size());

        for (Tag tag : storedTags.tags) {
            result.put(tag.getId(), tag);
        }

        return result;
    }

    private static class StoredTags {
        private List<Tag> tags;

        public List<Tag> getTags() {
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = tags;
        }
    }
}
