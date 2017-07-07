
package irene.bot.lex.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionAttributes {

    @SerializedName("channel")
    @Expose
    private String channel;

    @SerializedName("conversationId")
    @Expose
    private String conversationId;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("serviceUrl")
    @Expose
    private String serviceUrl;

    @SerializedName("textNotification")
    @Expose
    private String textNotification;

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getTextNotification() {
        return textNotification;
    }

    public void setTextNotification(String textNotification) {
        this.textNotification = textNotification;
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
}
