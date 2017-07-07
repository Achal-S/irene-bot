package irene.bot.lex.model;

public enum FullfillmentState {
    // Fulfilled or Failed
    FULFILLED("Fulfilled"),
    FAILED("Failed");

    private String value;

    FullfillmentState(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }


    public static FullfillmentState fromString(String text) throws IllegalArgumentException {
        for (FullfillmentState b : FullfillmentState.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unable to recognize argument " + text);
    }
}
