package message;

import java.util.Map;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public abstract class GenericMessageStructure {

    private String type;

    private long timestamp;

    //Generic Map
    private Map<String, Object> metadata;

    public GenericMessageStructure() {
    }

    public GenericMessageStructure(String type, Map<String, Object> metadata) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.metadata = metadata;
    }

    public GenericMessageStructure(String type, long timestamp, Map<String, Object> metadata) {
        this.type = type;
        this.timestamp = timestamp;
        this.metadata = metadata;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GenericMessageStructure{");
        sb.append("type='").append(type).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", metadata=").append(metadata);
        sb.append('}');
        return sb.toString();
    }
}
