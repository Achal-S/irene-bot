
package irene.bot.embedded.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Slots {

    @SerializedName("slot-name")
    @Expose
    private String slotName;

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

}
