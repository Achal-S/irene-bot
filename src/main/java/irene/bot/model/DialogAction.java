
package irene.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DialogAction {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("fulfillmentState")
    @Expose
    private String fulfillmentState;
    @SerializedName("message")
    @Expose
    private Message message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFulfillmentState() {
        return fulfillmentState;
    }

    public void setFulfillmentState(String fulfillmentState) {
        this.fulfillmentState = fulfillmentState;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

}
