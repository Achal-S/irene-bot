package irene.bot.embedded.model;

public class Pressure {

    private double pressure;
    private boolean success;
    private String message;

    public Pressure(double pressure, boolean success, String message) {
        this.pressure = pressure;
        this.success = success;
        this.message = message;
    }

    public Pressure() {
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
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
        return String.format("Pressure is [%f mb]", pressure);
    }
}
