package stream.pimedia.upnp;

import org.teleal.cling.model.meta.Device;


/**
 * Listener on events from an instance of UpnpClient. 
 * @author Logan Miller 
 *
 */
public interface UpnpClientListener  {

	void deviceAdded(Device<?, ?, ?> device);
	void deviceRemoved(Device<?, ?, ?> device);
	void deviceUpdated(Device<?, ?, ?> device);	
	                	
}

