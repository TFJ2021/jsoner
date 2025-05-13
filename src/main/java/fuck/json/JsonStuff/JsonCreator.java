package fuck.json.JsonStuff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@SuppressWarnings("ALL")
public class JsonCreator {

    private File file;
    private JsonConfig config;

    // Create new File
    public JsonCreator(String resourcePath, String targetPath) {
        File file = new File(targetPath);
        File directory = file.getParentFile();
        if (!file.exists()) {
            // Creates Directory
            if (!directory.exists()) directory.mkdirs();

            // Created File
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Fill in File
            try {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
                if (inputStream == null) throw new FileNotFoundException();
                Files.copy(inputStream, Path.of(targetPath), StandardCopyOption.REPLACE_EXISTING);
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // The main startup
            startUp(file);
        }
    }

    // get File
    public JsonCreator(String targetPath) {
        File file = new File(targetPath);
        if (!file.exists()) {
            try {
                throw new FileNotFoundException();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // The main startup
        startUp(file);
    }

    // The main startup
    private void startUp(File file) {
        config = new JsonConfig(file);
    }

    // Get Config
    public JsonConfig getConfig() {
        return config;
    }
}
