package fuck.json;

import fuck.json.JsonStuff.JsonConfig;
import fuck.json.JsonStuff.JsonCreator;

public class Main {

    /**
     * Funktioniert soweit :D
     * Ich will den Code aber noch sotieren und erweitern.
     * Code kommentieren
     * Read Me hinzufügen.
     * Eigenes Map System schreiben
     */

    public static void main(String[] args) {
        System.out.println("--==#==--");

        // JsonCreator jsonCreator = new JsonCreator("lel/config.json", "configs/config.json");
        JsonCreator jsonCreator = new JsonCreator("configs/config.json");
        JsonConfig config = jsonCreator.getConfig();
        System.out.println(config.getString("botToken"));
        config.set("botToken", "ohha");
        System.out.println(config.getString("hehe.moin"));
        config.save();

        System.out.println(config.getString("botToken", "not found"));
        // WORKS!!!!! So far :DDD

    }
}