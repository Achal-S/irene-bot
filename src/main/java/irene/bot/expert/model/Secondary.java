
package irene.bot.expert.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Secondary implements Question{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("question")
    @Expose
    private String question;

    @SerializedName("confirmation")
    @Expose
    private boolean confirmation;

    @SerializedName("outcomes")
    @Expose
    private List<String> outcomes = null;

    public String getId() {
        return id;
    }

    public boolean isPrimary(){
        return false;
    }


    public void setId(String id) {
        this.id = id;
    }

    public boolean isConfirmation() {
        return confirmation;
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

    public List<String> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<String> outcomes) {
        this.outcomes = outcomes;
    }

}
