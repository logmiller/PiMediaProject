package stream.pimedia.upnp.server;

import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;
import org.teleal.cling.support.renderingcontrol.AbstractAudioRenderingControl;
import org.teleal.cling.support.renderingcontrol.RenderingControlException;

import android.util.Log;

import stream.pimedia.upnp.UpnpClient;

/**
 * @author Logan Miller
 */
public class PiMediaAudioRenderingControlService extends
		AbstractAudioRenderingControl {

	
	private final UpnpClient upnpClient;

	public PiMediaAudioRenderingControlService(UpnpClient upnpClient) {
		this.upnpClient = upnpClient;
	}

	@Override
	public boolean getMute(UnsignedIntegerFourBytes instanceId, String channelName)
			throws RenderingControlException {
		Log.d(getClass().getName(), "getMute() - not yet implemented");
		return false;
	}

	@Override
	public UnsignedIntegerTwoBytes getVolume(UnsignedIntegerFourBytes instanceId,
			String channelName) throws RenderingControlException {
		Log.d(getClass().getName(), "getVolume() - not yet implemented");
		return null;
	}

	@Override
	public void setMute(UnsignedIntegerFourBytes instanceId, String channelName, boolean desiredMute)
			throws RenderingControlException {
		Log.d(getClass().getName(), "setMute() - not yet implemented");

	}

	@Override
	public void setVolume(UnsignedIntegerFourBytes instanceId, String channelName,
			UnsignedIntegerTwoBytes desiredVolume) throws RenderingControlException {
		Log.d(getClass().getName(), "setVolume() - not yet implemented");

	}

}
