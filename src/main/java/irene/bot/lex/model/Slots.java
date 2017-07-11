
package irene.bot.lex.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Slots {

    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("primaryQuestion")
    @Expose
    private Integer primaryQuestion;

    @SerializedName("secondaryQuestion")
    @Expose
    private Integer secondaryQuestion;

    @SerializedName("backOffMessage")
    @Expose
    private String backOffMessage;

    @SerializedName("desiredState")
    @Expose
    private Boolean desiredState;

    public Boolean getDesiredState() {
        return desiredState;
    }

    public void setDesiredState(Boolean desiredState) {
        this.desiredState = desiredState;
    }

    public String getBackOffMessage() {
        return backOffMessage;
    }

    public void setBackOffMessage(String backOffMessage) {
        this.backOffMessage = backOffMessage;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getPrimaryQuestion() {
        return primaryQuestion;
    }

    public void setPrimaryQuestion(Integer primaryQuestion) {
        this.primaryQuestion = primaryQuestion;
    }

    public Integer getSecondaryQuestion() {
        return secondaryQuestion;
    }

    public void setSecondaryQuestion(Integer secondaryQuestion) {
        this.secondaryQuestion = secondaryQuestion;
    }

    @Override
    public String toString() {
        return this.mobile;
    }
}
