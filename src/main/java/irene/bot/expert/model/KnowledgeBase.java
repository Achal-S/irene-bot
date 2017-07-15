
package irene.bot.expert.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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




    public Question getNextQuestion(int primaryIndex, int secondaryIndex, boolean skipToPrimary) throws Exception {
        if (primaryIndex < 0) {
            return this.getPrimary().get(0);
        } else {
            Primary primaryQuestion = this.primary.get(primaryIndex);
            if (!skipToPrimary){
                if(primaryQuestion.getSecondary().size() - 1 > secondaryIndex){
                    return primaryQuestion.getSecondary().get(++secondaryIndex);
                }else{
                    throw new Exception("End of questions");
                }
            } else {
                if (this.getPrimary().size() - 1 > primaryIndex) {
                    return this.getPrimary().get(++primaryIndex);
                } else {
                    throw new Exception("End of questions");
                }
            }
        }

    }
}
