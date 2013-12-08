package stream.pimedia.browser;

import org.teleal.cling.model.meta.Device;

/**
 * Created by Logan on 12/7/13.
 */
public class Position{

    private String objectId;
    private Device device;

    public Position(String objectId, Device device){

        this.device = device;
        this.objectId = objectId;
    }


    public String getObjectId() {
        return objectId;
    }

    public Device getDevice() {
        return device;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Position ["
                + (objectId != null ? "objectId=" + objectId + ", " : "")
                + (device != null ? "device=" + device : "") + "]";
    }

}