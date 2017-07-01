package irene.bot.messaging.model;

public class MessageProcessingInput {
    private String method;
    private String body;

    public MessageProcessingInput() {
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
