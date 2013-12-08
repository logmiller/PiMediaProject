package stream.pimedia.upnp.server;

import org.teleal.cling.support.xmicrosoft.AbstractMediaReceiverRegistrarService;
import stream.pimedia.upnp.UpnpClient;

/**
 * Created by Logan on 12/7/13.
 */
public class PiMediaReceiverRegisterService extends
        AbstractMediaReceiverRegistrarService {

    private final UpnpClient upnpClient;

    public PiMediaReceiverRegisterService(UpnpClient upnpClient) {
        this.upnpClient = upnpClient;

    }
}