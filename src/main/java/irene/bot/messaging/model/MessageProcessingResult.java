package irene.bot.messaging.model;

public class MessageProcessingResult {

    public boolean status;
    public String msg;

    public MessageProcessingResult(boolean status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
