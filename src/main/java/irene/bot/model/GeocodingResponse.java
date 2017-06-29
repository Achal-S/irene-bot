package irene.bot.model;

public class GeocodingResponse {

    private double latitude;
    private double longitude;
    private boolean success;
    private String message;

    public GeocodingResponse(double latitude, double longitude, boolean success, String message) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.success = success;
        this.message = message;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
