package fuck.json.JsonStuff;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class JsonConfig {

    private @NotNull Map<String, Object> map;
    private final File file;

    public JsonConfig(@NotNull File file) {
        map = new HashMap<>();
        this.file = file;
        load();
    }

    // Set Stuff
    public void setMap(final @NotNull Map<String, Object> newMap) {
        map = newMap;
    }

    public void set(final @NotNull String path, final @Nullable Object object) {
        if (object == null) {
            map.remove(path);
            return;
        }

        map.put(path, object);
    }

    // Get Methods
    @Contract("_, !null, _ -> !null")
    private <T> T get(final @NotNull String path, final @Nullable T def, final @NotNull Class<T> clazz) {
        final Object object = map.getOrDefault(path, null);

        if (clazz != object.getClass()) {
            return def;
        }

        return clazz.cast(object);
    }


    @Contract("_, !null -> !null")
    public @Nullable Object get(final @NotNull String path, final @Nullable Object def) {
        return map.getOrDefault(path, def);
    }

    public @Nullable Object get(final @NotNull String path) {
        return get(path, null);
    }

    public @Nullable String getString(final @NotNull String path, final @Nullable String def) {
        return get(path, def, String.class);
    }

    public @Nullable String getString(final @NotNull String path) {
        return getString(path, null);
    }


    // File Stuff
    public void reload() {
        map.clear();
        load();
    }

    private void load() {
        try {
            setMap(JsonUtilities.toMap(Files.readString(file.toPath(), StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(final boolean truncate) {
        final String jsonString = JsonUtilities.toJsonString(this.getMap());

        //noinspection ResultOfMethodCallIgnored
        file.delete();

        try {
            if (truncate) {
                Files.write(file.toPath(), jsonString.getBytes());
                return;
            }

            Files.write(
                    file.toPath(),
                    jsonString.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE
            );
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        save(false);
    }

    public @NotNull Map<String, Object> getMap() {
        return map;
    }

}
