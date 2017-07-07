package irene.bot.lex.model;

public enum ConfirmationStatus {
    //    None, Confirmed, or Denied
    NONE("None"),
    CONFIRMED("Confirmed"),
    DENIED("Denied");

    private String value;

    ConfirmationStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }


    public static ConfirmationStatus fromString(String text) throws IllegalArgumentException {
        for (ConfirmationStatus b : ConfirmationStatus.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unable to recognize argument " + text);
    }
}
