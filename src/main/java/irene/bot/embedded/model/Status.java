package irene.bot.embedded.model;

public class Status {

    boolean enabled;
    private String message;
    private String callBack;
    private String channel;
    private String conversationId;
    private String id;
    private String name;
    private String serviceUrl;
    private String textNotification;


    public Status(boolean enabled, String message, String callBack) {
        this.enabled = enabled;
        this.message = message;
        this.callBack = callBack;
    }

    public Status() {
    }

    public String getTextNotification() {
        return textNotification;
    }

    public void setTextNotification(String textNotification) {
        this.textNotification = textNotification;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCallBack() {
        return callBack;
    }

    public void setCallBack(String callBack) {
        this.callBack = callBack;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString(){
        return String.format("Safety mode enabled is %s, with callback %s, serviceUrl %s, and message \"%s\"", this.enabled, this.callBack, this.serviceUrl, this.message);
    }
}
