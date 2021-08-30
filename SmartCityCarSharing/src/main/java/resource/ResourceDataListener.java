package resource;

/**
 * Creating generic Interface to handle generic Sensor Resources with several values: Double, String, Integer, ...
 * @param <T>
 */
public interface ResourceDataListener<T> {
    // (modified resource, modified value)
    public void onDataChange(ResourceDataListener<T> resource, T updatedValue);
}
