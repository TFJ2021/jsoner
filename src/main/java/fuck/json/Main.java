package fuck.json;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TheJsonCreator jsonCreator = new TheJsonCreator("configs/config.json", "configFiles/main.json");

        // exists: xxx
        String botToken = jsonCreator.getString("botToken","UNKNOWN");
        System.out.println("botToken = " + botToken);

        // doesnt exist: ??? = fallback
        String apiKey = jsonCreator.getString("apiKey", "???");
        System.out.println("apiKey = " + apiKey);

        // Set and Saving
        int counts = jsonCreator.getInteger("timesStarted") + 1;
        System.out.println("Times stared = " + counts);
        jsonCreator.set("timesStarted", counts);

        // Entities are possible too
        PersonEntity person = jsonCreator.get("person", PersonEntity.class);
        System.out.println(person.toString());
        System.out.println();

        // List
        List<String> grades = jsonCreator.getList("grades", new ArrayList<String>());
        System.out.println("Grades = " + grades);
        System.out.println();

        // Entity Class - Are currently not supported!
        /*
        List<ClassEntity> bestClasses = jsonCreator.getList("bestClasses", new ArrayList<ClassEntity>());
        for (ClassEntity bestClass : bestClasses) System.out.println(bestClass.getTeacher() + " - " + bestClass.getRoom());
        System.out.println();
         */

        // Get Keys
        List<String> all = jsonCreator.getKeys("classes", false);
        for (String s : all) System.out.println("false " + s);
        all = jsonCreator.getKeys("classes", true);
        for (String s : all) System.out.println("true " + s);

        // Don´t forget to save :)
        jsonCreator.save();
    }
}