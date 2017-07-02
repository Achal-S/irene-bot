package irene.bot.embedded.sensing.model;

public class Humidity {

    private double humidity;
    private boolean success;
    private String message;

    public Humidity(double humidity, boolean success, String message) {
        this.humidity = humidity;
        this.success = success;
        this.message = message;
    }

    public Humidity() {
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
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

    @Override
    public String toString(){
        return String.format("Humidity is [%f ]", humidity);
    }
}
