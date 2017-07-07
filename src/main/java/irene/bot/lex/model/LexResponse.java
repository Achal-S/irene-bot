
package irene.bot.lex.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LexResponse {

    @SerializedName("dialogAction")
    @Expose
    private DialogAction dialogAction;

    public DialogAction getDialogAction() {
        return dialogAction;
    }

    public void setDialogAction(DialogAction dialogAction) {
        this.dialogAction = dialogAction;
    }

    @Override
    public String toString(){
        return "Lex Response with DialogAction is: "+dialogAction.toString();
    }

}
