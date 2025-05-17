package fuck.json;

import java.util.List;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        // Loads file. If it doesn't exist, a new one will be created
        TheJsonCreator jsonCreator = new TheJsonCreator("configs/config.json", "configFiles/main.json");

        // exists: xxx
        String botToken = jsonCreator.getString("botToken","UNKNOWN");
        System.out.println("botToken = " + botToken);

        // doesnt exist: ??? = fallback
        String apiKey = jsonCreator.getString("apiKey", "???");
        System.out.println("apiKey = " + apiKey);

        // Set
        int counts = jsonCreator.getInteger("timesStarted") + 1;
        System.out.println("Times stared = " + counts);
        jsonCreator.set("timesStarted", counts);

        // Entities are possible too
        PersonEntity person = jsonCreator.get("person", PersonEntity.class);
        System.out.println(person.toString());
        System.out.println();

        // List
        List<String> grades = jsonCreator.getList("grades", String.class);
        System.out.println("Grades = " + grades);
        System.out.println();

        // Entity Class List
        List<ClassEntity> bestClasses = jsonCreator.getList("bestClasses", ClassEntity.class);
        for (ClassEntity bestClass : bestClasses) System.out.println(bestClass);
        System.out.println();

        // Get Keys
        List<String> all = jsonCreator.getKeys("classes.json", false);
        for (String s : all) System.out.println("false " + s);
        all = jsonCreator.getKeys("classes.json", true);
        for (String s : all) System.out.println("true " + s);

        // Creates the path if it does not exist
        if (jsonCreator.getString("uuid") == null) jsonCreator.set("uuid", UUID.randomUUID().toString());

        // Don´t forget to save :)
        jsonCreator.save();

        // This throws an exception because the file doesn't exist
        try {
            TheJsonCreator classes = new TheJsonCreator("configFiles/clas.json");
        } catch (RuntimeException ignored) {}

        // List only files are also supported
        TheJsonCreator classes = new TheJsonCreator("configFiles/classes.json");
        List<ClassEntity> list = classes.getList("", ClassEntity.class);
        System.out.println(list);
        list.get(0).setRoom("Admin");
        classes.set("", list);
        classes.save();
    }
}