package model;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class GpsLocationDescriptor {

    public static final String FILE_LOCATION_PROVIDER = "location_provider_file";

    public static final String GPS_LOCATION_PROVIDER = "location_provider_gps";

    public static final String NETWORK_LOCATION_PROVIDER = "location_provider_network";

    private Double latitude;

    private Double longitude;

    private Double elevation;

    private String fileProvider;

    // Constructors
    public GpsLocationDescriptor() {
    }

    public GpsLocationDescriptor(Double latitude, Double longitude, Double elevation, String fileProvider) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.fileProvider = fileProvider;
    }

    // Getter & Setter
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    public String getFileProvider() {
        return fileProvider;
    }

    public void setFileProvider(String fileProvider) {
        this.fileProvider = fileProvider;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GpsLocationDescriptor{");
        sb.append("latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", elevation=").append(elevation);
        sb.append(", fileProvider='").append(fileProvider).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

