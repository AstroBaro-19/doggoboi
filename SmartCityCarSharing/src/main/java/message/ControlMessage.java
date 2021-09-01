package message;

import java.util.Map;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class ControlMessage extends GenericMessageStructure{

    public ControlMessage() {
    }

    public ControlMessage(String type, Map<String, Object> metadata) {
        super(type, metadata);
    }

    public ControlMessage(String type, long timestamp, Map<String, Object> metadata) {
        super(type, timestamp, metadata);
    }
}
