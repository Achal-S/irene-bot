
package irene.bot.expert.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Primary implements Question{

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("question")
    @Expose
    private String question;

    @SerializedName("confirmation")
    @Expose
    private boolean confirmation;

    @SerializedName("secondary")
    @Expose
    private List<Secondary> secondary = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isConfirmation() {
        return confirmation;
    }

    public boolean isPrimary(){
        return true;
    }


    public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Secondary> getSecondary() {
        return secondary;
    }

    public void setSecondary(List<Secondary> secondary) {
        this.secondary = secondary;
    }

}
