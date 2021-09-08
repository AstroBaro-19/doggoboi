package message;

import java.util.Map;

/**
 * @authors - Alessandro Baroni, Simone Brunelli, Riccardo Mari
 * @project - Smart City Car Sharing
 */

public class EventMessage extends GenericMessageStructure{

    public EventMessage() {
    }

    public EventMessage(String type, Map<String, Object> metadata) {
        super(type, metadata);
    }

    public EventMessage(String type, long timestamp, Map<String, Object> metadata) {
        super(type, timestamp, metadata);
    }

}
