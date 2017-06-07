package irene.geocoding.model;

public class ReverseGeocodingRequest {

    private double latitude;
    private double longitude;

    public ReverseGeocodingRequest() {
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
}
