package fuck.json;

public class Main {

    public static void main(String[] args) {
        TheJsonCreator jsonCreator = new TheJsonCreator("configs/config.json", "configFiles/main.json");

        // exists: xxx
        String botToken = jsonCreator.getString("botToken","UNKNOWN");
        System.out.println("botToken = " + botToken);

        // doesnt exist: ??? = fallback
        String apiKey = jsonCreator.getString("apiKey", "???");
        System.out.println("apiKey = " + apiKey);

        // TODO | Finish examples
        /*
        // ändern und speichern:
        cfg.getRoot()
                .getAsJsonObject("stickers")
                .addProperty("lol", "999");
        cfg.save();
        System.out.println("Saved new sticker.lol = " + cfg.get("stickers.lol", String.class, "x"));

         */
    }
}