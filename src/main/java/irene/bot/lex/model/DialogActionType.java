package irene.bot.lex.model;

public enum DialogActionType {
    //ElicitIntent, ElicitSlot, ConfirmIntent, Delegate, or Close
    ELICITINTENT("ElicitIntent"),
    ELICITSLOT("ElicitSlot"),
    CONFIRMINTENT("ConfirmIntent"),
    DELEGATE("Delegate"),
    CLOSE("Close");

    private String value;

    DialogActionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }


    public static DialogActionType fromString(String text) throws IllegalArgumentException {
        for (DialogActionType b : DialogActionType.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unable to recognize argument " + text);
    }
}
