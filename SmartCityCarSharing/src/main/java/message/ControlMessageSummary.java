package message;


import java.util.Map;

public class ControlMessageSummary {

    private String type;

    private Map<String, Object> metadata;

    public ControlMessageSummary() {
    }

    public ControlMessageSummary(String type, Map<String, Object> metadata) {
        this.type = type;
        this.metadata = metadata;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ControlMessageSummary{");
        sb.append("type='").append(type).append('\'');
        sb.append(", metadata=").append(metadata);
        sb.append('}');
        return sb.toString();
    }
}
