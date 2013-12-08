package stream.pimedia.upnp.server;

import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.avtransport.impl.AVTransportService;
import org.teleal.cling.support.avtransport.impl.AVTransportStateMachine;
import org.teleal.cling.support.model.AVTransport;
import org.teleal.common.statemachine.StateMachineBuilder;

import stream.pimedia.upnp.UpnpClient;


/**
 * @author Logan Miller
 * 
 */
public class PiMediaAVTransportService extends AVTransportService<AVTransport> {

	private final UpnpClient upnpClient;

	/**
	 * 
	 */
	public PiMediaAVTransportService(UpnpClient upnpClient) {
		super(AvTransportStateMachine.class,
				AvTransportMediaRendererNoMediaPresent.class);
		this.upnpClient = upnpClient;
	}

	/**
	 * Create a 
	 */
	protected AVTransportStateMachine createStateMachine(
			UnsignedIntegerFourBytes instanceId) {
		return (AVTransportStateMachine) StateMachineBuilder.build(
				AvTransportStateMachine.class,
				AvTransportMediaRendererNoMediaPresent.class, new Class[] {
						AVTransport.class, UpnpClient.class }, new Object[] {
						createTransport(instanceId, getLastChange()),
						upnpClient });
	}

}
