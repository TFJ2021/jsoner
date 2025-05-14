package fuck.json;

public class Main {

    public static void main(String[] args) {
        TheJsonCreator jsonCreator = new TheJsonCreator();

        // TODO | Add Examples
        /*
            JsonEntity cfg = new JsonEntity(new File("config.json"));

            // existierend: liefert "001"
            String stickerId = cfg.get("stickers.lol", String.class, "default");
            System.out.println("stickerId = " + stickerId);

            PersonEntity sticker = cfg.get("da", PersonEntity.class, new PersonEntity());
            System.out.println("stickerId = " + sticker.getId());

            sticker.setDisplay("Uiii");
            cfg.set("botToken", ":D");

            // nicht existierend: liefert Fallback
            String foo = cfg.get("foo.bar", String.class, "nicht gefunden");
            System.out.println("foo.bar = " + foo);

            // ändern und speichern:
            cfg.getRoot()
                    .getAsJsonObject("stickers")
                    .addProperty("lol", "999");
            cfg.save();
            System.out.println("Saved new sticker.lol = " + cfg.get("stickers.lol", String.class, "x"));

         */
    }
}