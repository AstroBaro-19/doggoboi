package resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//Creating generic abstract class
public abstract  class SmartObjectResource<T> {

    private static final Logger logger = LoggerFactory.getLogger(SmartObjectResource.class);

    private String id;

    private String type;


    public SmartObjectResource() {

    }

    public SmartObjectResource(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract T loadUpdatedValue();

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SmartObjectResource{");
        sb.append("id='").append(id).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
