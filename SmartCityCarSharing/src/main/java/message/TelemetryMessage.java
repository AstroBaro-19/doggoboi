package message;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

// Jackson library
// @JsonProperty: is used to indicate external property name,
// name used in data format (JSON or one of other supported data formats)

public class TelemetryMessage extends GenericMessageStructure{

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("type")
    private String type;

    @JsonProperty("data")
    private Object dataValue;

    public TelemetryMessage() {
    }

    public TelemetryMessage(String type, Object dataValue) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.dataValue = dataValue;
    }

    public TelemetryMessage(long timestamp, String type, Object dataValue) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.dataValue = dataValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getDataValue() {
        return dataValue;
    }

    public void setDataValue(Object dataValue) {
        this.dataValue = dataValue;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TelemetryMessage{");
        sb.append("timestamp='").append(timestamp).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", dataValue=").append(dataValue);
        sb.append('}');
        return sb.toString();
    }
}
