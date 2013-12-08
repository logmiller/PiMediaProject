package stream.pimedia.upnp.server;

import org.teleal.cling.support.avtransport.impl.AVTransportStateMachine;
import org.teleal.common.statemachine.States;
/**
 * @author Logan Miller 
 *
 */

@States({
	AvTransportMediaRendererNoMediaPresent.class,
	AvTransportMediaRendererStopped.class,
	AvTransportMediaRendererPlaying.class,
	AvTransportMediaRendererPaused.class
})
public interface AvTransportStateMachine extends AVTransportStateMachine {

}
