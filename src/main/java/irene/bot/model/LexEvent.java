
package irene.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LexEvent {

    @SerializedName("currentIntent")
    @Expose
    private CurrentIntent currentIntent;
    @SerializedName("bot")
    @Expose
    private Bot bot;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("inputTranscript")
    @Expose
    private String inputTranscript;
    @SerializedName("invocationSource")
    @Expose
    private String invocationSource;
    @SerializedName("outputDialogMode")
    @Expose
    private String outputDialogMode;
    @SerializedName("messageVersion")
    @Expose
    private String messageVersion;
    @SerializedName("sessionAttributes")
    @Expose
    private SessionAttributes sessionAttributes;

    public CurrentIntent getCurrentIntent() {
        return currentIntent;
    }

    public void setCurrentIntent(CurrentIntent currentIntent) {
        this.currentIntent = currentIntent;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInputTranscript() {
        return inputTranscript;
    }

    public void setInputTranscript(String inputTranscript) {
        this.inputTranscript = inputTranscript;
    }

    public String getInvocationSource() {
        return invocationSource;
    }

    public void setInvocationSource(String invocationSource) {
        this.invocationSource = invocationSource;
    }

    public String getOutputDialogMode() {
        return outputDialogMode;
    }

    public void setOutputDialogMode(String outputDialogMode) {
        this.outputDialogMode = outputDialogMode;
    }

    public String getMessageVersion() {
        return messageVersion;
    }

    public void setMessageVersion(String messageVersion) {
        this.messageVersion = messageVersion;
    }

    public SessionAttributes getSessionAttributes() {
        return sessionAttributes;
    }

    public void setSessionAttributes(SessionAttributes sessionAttributes) {
        this.sessionAttributes = sessionAttributes;
    }

}
