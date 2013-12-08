package stream.pimedia.upnp.server;

import java.net.URI;

import org.teleal.cling.support.avtransport.impl.state.AbstractState;
import org.teleal.cling.support.avtransport.impl.state.NoMediaPresent;
import org.teleal.cling.support.avtransport.lastchange.AVTransportVariable;
import org.teleal.cling.support.model.AVTransport;
import org.teleal.cling.support.model.MediaInfo;
import org.teleal.cling.support.model.PositionInfo;

import android.util.Log;
import stream.pimedia.upnp.UpnpClient;

/**
 * @author Logan Miller
 * 
 */
public class AvTransportMediaRendererNoMediaPresent extends
		NoMediaPresent<AVTransport> {

	private UpnpClient upnpClient;

	/**
	 * Constructor.
	 * 
	 * @param transport
	 *            the state holder
	 * @param upnpClient
	 *            the upnpClient to use
	 */
	public AvTransportMediaRendererNoMediaPresent(AVTransport transport,
			UpnpClient upnpClient) {
		super(transport);
		this.upnpClient = upnpClient;
	}

	/*
	 * (non-Javadoc)
	 * @see org.teleal.cling.support.avtransport.impl.state.NoMediaPresent#setTransportURI(java.net.URI, java.lang.String)
	 */
	@Override
	public Class<? extends AbstractState> setTransportURI(URI uri,
			String metaData) {
		Log.d(this.getClass().getName(), "set Transport: " + uri + " metaData: " + metaData);
		getTransport().setMediaInfo(new MediaInfo(uri.toString(), metaData));		
		// If you can, you should find and set the duration of the track here!
		getTransport().setPositionInfo(
				new PositionInfo(1, metaData, uri.toString()));

		// It's up to you what "last changes" you want to announce to event
		// listeners
		getTransport().getLastChange().setEventedValue(
				getTransport().getInstanceId(),
				new AVTransportVariable.AVTransportURI(uri),
				new AVTransportVariable.CurrentTrackURI(uri));

		return AvTransportMediaRendererStopped.class;
	}
}
