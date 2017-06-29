package irene.bot.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Temperature {

    private double temperature;
    private boolean success;
    private String message;

    public Temperature(double temperature, boolean success, String message) {
        this.temperature = temperature;
        this.success = success;
        this.message = message;
    }

    public Temperature() {
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
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
        return String.format("Temperature is [%f Celsius]", temperature);
    }
}
