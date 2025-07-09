package fuck.json;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TheJsonCreator {

    /**
     * <h1>Jsoner [v1.2.1]</h1>
     * by TFJ - MIT license <br><a href="https://github.com/TFJ2021/jsoner">GitHub Link</a>
     */

    private File file;
    private Gson gson;
    private JsonElement root;

    /**
     * Creates new JsonCreator with a brand-new file
     *
     * @param resourcePath Relative path of the file to be copied from the resource folder
     * @param targetPath Relative path to the file that should be created
     */
    public TheJsonCreator(String resourcePath, String targetPath) {
        File file = new File(targetPath);
        File directory = file.getParentFile();
        if (!file.exists()) {
            // Creates Directory
            if (directory != null && !directory.exists()) directory.mkdirs();

            // Creates File
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Inserts content into the new file.
            try {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
                if (inputStream == null) throw new FileNotFoundException();
                Files.copy(inputStream, Path.of(targetPath), StandardCopyOption.REPLACE_EXISTING);
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // The main startup
        startUp(file);
    }

    /**
     * Creates new JsonCreator
     *
     * @param targetPath Relative path to the file that should be loaded
     * @throws RuntimeException When the File couldn't be found
     */
    public TheJsonCreator(String targetPath) {
        File file = new File(targetPath);
        if (!file.exists()) throw new RuntimeException(new FileNotFoundException());

        // The main startup
        startUp(file);
    }

    /**
     * Uses a string in Json format for the creator
     *
     * @param json The json as String
     * @param file (Nullable) To which file the content should be saved with save()
     */
    public TheJsonCreator(String json, File file) {
        // The main startup
        startUp(json, file);
    }

    /**
     * Starts the main loading part
     *
     * @param file The file that should be loaded
     */
    private void startUp(File file) {
        settings(file);
        reload();
    }

    private void startUp(String json, File file) {
        settings(file);
        reload(json);
    }

    /**
     * Sets file and new gson
     *
     * @param file The main file
     */
    private void settings(File file) {
        this.file = file;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * (Re-)loads the File and sets the root
     */
    public void reload() {
        // Checks whether a file has been deposited
        if (file == null) throw new RuntimeException(new FileNotFoundException("No file has been deposited"));

        try {
            FileReader fr = new FileReader(file);
            JsonReader jr = new JsonReader(fr);
            JsonElement parsed = JsonParser.parseReader(jr);
            if (parsed == null) root = new JsonObject(); // root is null
            else if (parsed.isJsonObject()) root = parsed.getAsJsonObject();
            else if (parsed.isJsonArray()) root = parsed.getAsJsonArray();
            else root = new JsonObject(); // empty object if the file is empty or no object
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses the string as JSON
     */
    private void reload(String content) {
        StringReader sr = new StringReader(content);
        JsonReader jr = new JsonReader(sr);
        JsonElement parsed = JsonParser.parseReader(jr);
        if (parsed == null) root = new JsonObject(); // root is null
        else if (parsed.isJsonObject()) root = parsed.getAsJsonObject();
        else if (parsed.isJsonArray()) root = parsed.getAsJsonArray();
        else root = new JsonObject(); // empty object if the file is empty or no object
    }

    /**
     * Saves the current state back to the JSON file.
     */
    public void save() {
        // Checks whether a file has been deposited
        if (file == null) throw new RuntimeException(new FileNotFoundException("No file has been deposited"));

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(toJson());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets a new Value
     *
     * @param path Path in the format "object.subobject.field". Cannot be null
     * @param value New value (any type, serialized via Gson)
     * @param <T> Value type
     * @throws IllegalArgumentException when the path is null
     */
    public <T> void set(String path, T value) {
        if (path == null) throw new RuntimeException(new IllegalArgumentException());
        JsonElement jsonValue = gson.toJsonTree(value);

        // Case 1: Set root
        if (path.isEmpty()) {
            this.root = jsonValue;
            return;
        }

        // Case 2: Root must be objected for nested keys
        if (!(root != null && root.isJsonObject())) throw new IllegalStateException("Root is not a JSON object – cannot be set with path");

        String[] parts = path.split("\\.");
        JsonObject current = root.getAsJsonObject();
        for (int i = 0; i < parts.length - 1; i++) {
            String key = parts[i];
            JsonElement child = current.get(key);

            if (child != null && child.isJsonObject()) current = child.getAsJsonObject();
            else {
                JsonObject newObj = new JsonObject();
                current.add(key, newObj);
                current = newObj;
            }
        }

        // Last Key: Set
        String lastKey = parts[parts.length - 1];
        current.add(lastKey, jsonValue);
    }


    /**
     * Reads a value using a "dot" path, casts it to clazz, or returns the fallback.
     *
     * @param path Path in the format "object.subobject.field"
     * @param clazz Target Typ
     * @param fallback Fallback-Value
     * @return Value or fallback
     */
    public <T> T get(String path, Class<T> clazz, T fallback) {
        JsonElement node = traverse(path);
        if (node == null || node.isJsonNull()) return fallback; // Not Found

        try {
            return gson.fromJson(node, clazz);
        } catch (JsonSyntaxException e) {
            return fallback;
        }
    }

    /// Some more get methods
    public <T> T get(String path, Class<T> clazz) {
        return get(path, clazz, null);
    }

    // Strings
    public String getString(String path, String fallback) {
        return get(path, String.class, fallback);
    }
    public String getString(String path) {
        return getString(path, null);
    }

    // Boolean
    public boolean getBoolean(String path, boolean fallback) {
        return get(path, Boolean.class, fallback);
    }
    public boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    // Byte
    public int getByte(String path, byte fallback) {
        return get(path, Byte.class, fallback);
    }
    public int getByte(String path) {
        return getByte(path, (byte) 0);
    }

    // Integer
    public int getInteger(String path, int fallback) {
        return get(path, Integer.class, fallback);
    }
    public int getInteger(String path) {
        return getInteger(path, 0);
    }

    // Long
    public long getLong(String path, long fallback) {
        return get(path, Long.class, fallback);
    }
    public long getLong(String path) {
        return getLong(path, 0L);
    }

    // Float
    public float getFloat(String path, float fallback) {
        return get(path, Float.class, fallback);
    }
    public float getFloat(String path) {
        return getFloat(path, 0f);
    }

    // Double
    public double getDouble(String path, double fallback) {
        return get(path, Double.class, fallback);
    }
    public double getDouble(String path) {
        return getDouble(path, 0d);
    }

    /**
     * Gets a list
     *
     * @param path Path in the format "object.subobject.field"
     * @param clazz List Typ
     * @param fallback Fallback-Value
     * @return List or fallback
     */
    public <T> List<T> getList(String path, Class<T> clazz, List<T> fallback) {
        JsonElement node = traverse(path);
        if (node == null || node.isJsonNull()) return fallback;

        try {
            JsonArray arr = node.getAsJsonArray();
            List<T> result = new ArrayList<>();
            for (JsonElement elem : arr) result.add(gson.fromJson(elem, clazz));
            return result;
        } catch (Exception e) {
            return fallback;
        }
    }

    public <T> List<T> getList(String path, Class<T> clazz) {
        return getList(path, clazz, null);
    }

    /**
     * Traverses the JSON structure according to dot notation.
     */
    private JsonElement traverse(String path) {
        if (path == null) throw new RuntimeException(new IllegalArgumentException());
        if (path.isEmpty()) return root;

        String[] parts = path.split("\\.");
        JsonElement current = root;

        for (String part : parts) {
            if (current == null || !current.isJsonObject()) return null;
            current = current.getAsJsonObject().get(part);
        }
        return current;
    }

    /**
     * Returns all keys below a given path.
     *
     * @param path Path in the format "object.subobject.field"
     * @param deep true = recursive (all subkeys), false = direct keys only
     * @return The requested keys as List< String>
     */
    public List<String> getKeys(String path, boolean deep) {
        if (path == null) throw new RuntimeException(new IllegalArgumentException());
        List<String> keys = new ArrayList<>();
        JsonElement node = traverse(path);
        if (node == null || !node.isJsonObject()) return keys; // Not found
        collectKeys(node.getAsJsonObject(), "", deep, keys);
        return keys;
    }

    // Helper methode
    private void collectKeys(JsonObject obj, String prefix, boolean deep, List<String> keys) {
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String fullKey = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            keys.add(fullKey);
            if (deep && entry.getValue().isJsonObject()) collectKeys(entry.getValue().getAsJsonObject(), fullKey, true, keys);
        }
    }

    // Returns the class as JSON
    public String toJson() {
        return gson.toJson(root);
    }

    // Just look at toJson()
    @Override
    public String toString() {
        return toJson();
    }
}
