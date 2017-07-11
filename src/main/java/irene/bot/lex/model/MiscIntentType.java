package irene.bot.lex.model;

public enum MiscIntentType {
    //    None, Confirmed, or Denied
    HELP("Help"),
    CANCEL("Cancel"),
    STOP("Stop"),
    GREETING("Greeting");

    private String value;

    MiscIntentType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }


    public static MiscIntentType fromString(String text) throws IllegalArgumentException {
        for (MiscIntentType b : MiscIntentType.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unable to recognize argument " + text);
    }
}
