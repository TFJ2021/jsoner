package fuck.json;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class TheJsonCreator {

    /**
     * <h1>Jsoner</h1>
     * by TFJ - MIT license <br><a href="https://github.com/TFJ2021/jsoner">Github Link</a>
     */

    private File file;
    private Gson gson;
    private JsonObject root;

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
            if (!directory.exists()) directory.mkdirs();

            // Creates File
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Fills in the new file
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
        if (!file.exists()) {
            try {
                throw new FileNotFoundException();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        // The main startup
        startUp(file);
    }

    /**
     * Starts the main loading part
     *
     * @param file The file that should be loaded
     */
    private void startUp(File file) {
        this.file = file;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        reload();
    }

    /**
     * (Re-)loads the File and sets the root
     */
    public void reload() {
        try {
            FileReader fr = new FileReader(file);
            JsonReader jr = new JsonReader(fr);
            JsonElement parsed = JsonParser.parseReader(jr);
            if (parsed != null && parsed.isJsonObject()) root = parsed.getAsJsonObject();
            else root = new JsonObject(); // empty object if the file is empty or no object
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the current state back to the JSON file.
     */
    public void save() {
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            gson.toJson(root, bw);
        } catch (IOException | JsonIOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads a value using a "dot" path, casts it to clazz, or returns the fallback.
     * Example: get("hashedAPIKey", String.class, "xxx")
     *
     * @param path Path in the format "object.subobject.field"
     * @param clazz Target class
     * @param fallback Fallback-Value
     * @param <T> Typ
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
     * Traverses the JSON structure according to dot notation.
     */
    private JsonElement traverse(String path) {
        String[] parts = path.split("\\.");
        JsonElement current = root;
        for (String p : parts) {
            if (current == null || !current.isJsonObject()) {
                return null;
            }
            current = current.getAsJsonObject().get(p);
        }
        return current;
    }

    /**
     * Sets a new Value
     *
     * @param path Path in the format "object.subobject.field"
     * @param value New value (any type, serialized via Gson)
     * @param <T> Value type
     */
    public <T> void set(String path, T value) {
        String[] parts = path.split("\\.");
        JsonObject current = root;
        // Loops through to the second to last path element, creating missing objects
        for (String key : parts) {
            JsonElement child = current.get(key);
            if (!(child == null || !child.isJsonObject())) current = child.getAsJsonObject();
            else {
                JsonObject obj = new JsonObject();
                current.add(key, obj);
                current = obj;
            }
        }

        // Last element: sets the value
        String lastKey = parts[parts.length - 1];
        JsonElement jsonValue = gson.toJsonTree(value);
        current.add(lastKey, jsonValue);
    }
}
