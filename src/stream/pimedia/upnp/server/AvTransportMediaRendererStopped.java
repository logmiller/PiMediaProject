package stream.pimedia.upnp.server;
import java.net.URI;
import java.util.List;
import org.teleal.cling.support.avtransport.impl.state.AbstractState;
import org.teleal.cling.support.avtransport.impl.state.Stopped;
import org.teleal.cling.support.avtransport.lastchange.AVTransportVariable;
import org.teleal.cling.support.model.AVTransport;
import org.teleal.cling.support.model.MediaInfo;
import org.teleal.cling.support.model.PositionInfo;
import org.teleal.cling.support.model.SeekMode;
import android.util.Log;
import stream.pimedia.player.Player;
import stream.pimedia.upnp.UpnpClient;
/**
 * State stopped
 * @author Logan Miller
 *
 */
public class AvTransportMediaRendererStopped extends Stopped<AVTransport> {
    private UpnpClient upnpClient;
    /**
     * Constructor.
     *
     * @param transport
     * the state holder
     * @param upnpClient
     * the upnpclient to use
     */
    public AvTransportMediaRendererStopped(AVTransport transport,
                                           UpnpClient upnpClient) {
        super(transport);
        this.upnpClient = upnpClient;
    }
    /*
    * (non-Javadoc)
    *
    * @see org.teleal.cling.support.avtransport.impl.state.Stopped#onEntry()
    */
    @Override
    public void onEntry() {
        Log.d(this.getClass().getName(), "On Entry");
        super.onEntry();
        List<Player> players = upnpClient.getCurrentPlayers(getTransport());
        for (Player player : players) {
            if(player != null ){
                player.stop();
            }
        }
    }
    /*
    * (non-Javadoc)
    *
    * @see
    * org.teleal.cling.support.avtransport.impl.state.Stopped#setTransportURI
    * (java.net.URI, java.lang.String)
    */
    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri,
                                                          String metaData) {
        Log.d(this.getClass().getName(), "setTransportURI");
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
// This operation can be triggered in any state, you should think
// about how you'd want your player to react. If we are in Stopped
// state nothing much will happen, except that you have to set
// the media and position info, just like in MyRendererNoMediaPresent.
// However, if this would be the MyRendererPlaying state, would you
// prefer stopping first?
        return AvTransportMediaRendererStopped.class;
    }
    /*
    * (non-Javadoc)
    *
    * @see org.teleal.cling.support.avtransport.impl.state.Stopped#stop()
    */
    @Override
    public Class<? extends AbstractState> stop() {
        Log.d(this.getClass().getName(), "stop");
// / Same here, if you are stopped already and someone calls STOP,
// well...
        return AvTransportMediaRendererStopped.class;
    }
    /*
    * (non-Javadoc)
    *
    * @see
    * org.teleal.cling.support.avtransport.impl.state.Stopped#play(java.lang
    * .String)
    */
    @Override
    public Class<? extends AbstractState> play(String speed) {
        Log.d(this.getClass().getName(), "play");
// It's easier to let this classes' onEntry() method do the work
        return AvTransportMediaRendererPlaying.class;
    }
    /*
    * (non-Javadoc)
    *
    * @see org.teleal.cling.support.avtransport.impl.state.Stopped#next()
    */
    @Override
    public Class<? extends AbstractState> next() {
        Log.d(this.getClass().getName(), "next");
        return AvTransportMediaRendererStopped.class;
    }
    /*
    * (non-Javadoc)
    *
    * @see org.teleal.cling.support.avtransport.impl.state.Stopped#previous()
    */
    @Override
    public Class<? extends AbstractState> previous() {
        Log.d(this.getClass().getName(), "previous");
        return AvTransportMediaRendererStopped.class;
    }
    /*
    * (non-Javadoc)
    *
    * @see
    * org.teleal.cling.support.avtransport.impl.state.Stopped#seek(org.teleal
    * .cling.support.model.SeekMode, java.lang.String)
    */
    @Override
    public Class<? extends AbstractState> seek(SeekMode unit, String target) {
        Log.d(this.getClass().getName(), "seek");
// Implement seeking with the stream in stopped state!
        return AvTransportMediaRendererStopped.class;
    }
} 