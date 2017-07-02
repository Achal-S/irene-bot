package irene.bot.embedded.sensing.model;

public class Position {

    private double latitude;
    private double longitude;
    private double speed;
    private double speedError;


    private boolean success;
    private String message;


    public Position(double latitude, double longitude, double speed, double sppedError, boolean success, String message) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.success = success;
        this.message = message;
        this.speed = speed;
        this.speedError = sppedError;

    }

    public Position() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeedError() {
        return speedError;
    }

    public void setSpeedError(double speedError) {
        this.speedError = speedError;
    }

    @Override
    public String toString(){
        return String.format("GPS Position is [LAT: %f, LNG: %f, Speed: %f]", this.latitude, this.longitude, this.speed);
    }
}
