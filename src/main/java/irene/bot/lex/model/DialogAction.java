
package irene.bot.lex.model;

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

    @SerializedName("intentName")
    @Expose
    private String intentName;

    @SerializedName("slots")
    @Expose
    private Slots slots;

    @SerializedName("slotToElicit")
    @Expose
    private String slotToElicit;

    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }

    public Slots getSlots() {
        return slots;
    }

    public void setSlots(Slots slots) {
        this.slots = slots;
    }

    public String getSlotToElicit() {
        return slotToElicit;
    }

    public void setSlotToElicit(String slotToElicit) {
        this.slotToElicit = slotToElicit;
    }

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

    @Override
    public String toString(){
        String typeString = "Type :"+ type+"\n";
        String fulfillmentStateString = "fulfillmentState :"+ fulfillmentState+"\n";
        String intentNameString = "intentName :"+ intentName+"\n";
        String slotToElicitString = "slotToElicit :"+ slotToElicit+"\n";

        String msgString = "message is "+message.toString()+"\n";
        String slotsString = "slots is "+slots.toString()+"\n";
        return String.format("DialogAction is: \n %s, %s, %s, %s %s, %s", typeString, fulfillmentStateString, intentNameString, slotsString, slotToElicitString, msgString, slotsString);
    }

}
