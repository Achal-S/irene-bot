
package irene.bot.lex.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Slots {

    @SerializedName("mobile")
    @Expose
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return this.mobile;
    }
}
