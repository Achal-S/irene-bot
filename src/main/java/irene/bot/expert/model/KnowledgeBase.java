
package irene.bot.expert.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KnowledgeBase {

    @SerializedName("primary")
    @Expose
    private List<Primary> primary = null;

    public List<Primary> getPrimary() {
        return primary;
    }

    public void setPrimary(List<Primary> primary) {
        this.primary = primary;
    }

}
