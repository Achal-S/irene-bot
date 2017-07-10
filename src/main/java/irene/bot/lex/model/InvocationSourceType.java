package irene.bot.lex.model;

public enum InvocationSourceType {
    //DialogCodeHook, FulfillmentCodeHook
    DIALOGCODEHOOK("DialogCodeHook"),
    FULLFILLMENTCODEHOOK("FulfillmentCodeHook");

    private String value;

    InvocationSourceType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }


    public static InvocationSourceType fromString(String text) throws IllegalArgumentException {
        for (InvocationSourceType b : InvocationSourceType.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unable to recognize argument " + text);
    }
}
