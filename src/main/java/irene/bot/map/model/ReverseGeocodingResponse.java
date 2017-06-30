package irene.bot.map.model;

public class ReverseGeocodingResponse {

    private String address;
    private boolean success;
    private String message;

    public ReverseGeocodingResponse(String address, boolean success, String message) {
        this.address = address;
        this.success = success;
        this.message = message;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
