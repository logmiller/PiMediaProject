package stream.pimedia.player;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.avtransport.callback.Pause;
import org.teleal.cling.support.avtransport.callback.Play;
import org.teleal.cling.support.avtransport.callback.SetAVTransportURI;
import org.teleal.cling.support.avtransport.callback.Stop;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import stream.pimedia.upnp.UpnpClient;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class AVTransportPlayer extends AbstractPlayer {
    public static final String PLAYER_ID = "PlayerId";
    private String deviceId="";
    private int id;

    public AVTransportPlayer(UpnpClient upnpClient, Device receiverDevice, String name) {
        this(upnpClient);
        deviceId = receiverDevice.getIdentity().getUdn().getIdentifierString();
        setName(name);
        id = UUID.randomUUID().hashCode();
    }
    private Device<?, ?, ?> getDevice(){
        return getUpnpClient().getDevice(deviceId);
    }

    public AVTransportPlayer(UpnpClient upnpClient) {
        super(upnpClient);
// TODO Auto-generated constructor stub
    }
    /* (non-Javadoc)
    * @see de.yaacc.player.AbstractPlayer#stopItem(de.yaacc.player.PlayableItem)
    */
    @Override
    protected void stopItem(PlayableItem playableItem) {
        if(getDevice() == null) {
            Log.d(getClass().getName(),
                    "No receiver device found: "
                            + deviceId);
            return;
        }
        Service<?, ?> service = getUpnpClient().getAVTransportService(getDevice());
        if (service == null) {
            Log.d(getClass().getName(),
                    "No AVTransport-Service found on Device: "
                            + getDevice().getDisplayString());
            return;
        }
        Log.d(getClass().getName(), "Action SetAVTransportURI ");
        final ActionState actionState = new ActionState();
// Now start Stopping
        Log.d(getClass().getName(), "Action Stop");
        actionState.actionFinished = false;
        Stop actionCallback = new Stop(service) {
            @Override
            public void failure(ActionInvocation actioninvocation,
                                UpnpResponse upnpresponse, String s) {
                Log.d(getClass().getName(), "Failure UpnpResponse: "
                        + upnpresponse);
                Log.d(getClass().getName(),
                        upnpresponse != null ? "UpnpResponse: "
                                + upnpresponse.getResponseDetails() : "");
                Log.d(getClass().getName(), "s: " + s);
                actionState.actionFinished = true;
            }
            @Override
            public void success(ActionInvocation actioninvocation) {
                super.success(actioninvocation);
                actionState.actionFinished = true;
            }
        };
        getUpnpClient().getControlPoint().execute(actionCallback);
    }
    /* (non-Javadoc)
    * @see de.yaacc.player.AbstractPlayer#loadItem(de.yaacc.player.PlayableItem)
    */
    @Override
    protected Object loadItem(PlayableItem playableItem) {
        return playableItem;
    }
    /* (non-Javadoc)
    * @see de.yaacc.player.AbstractPlayer#startItem(de.yaacc.player.PlayableItem, java.lang.Object)
    */
    @Override
    protected void startItem(PlayableItem playableItem, Object loadedItem) {
        if (playableItem == null || getDevice() == null)
            return;
        Log.d(getClass().getName(), "Uri: " + playableItem.getUri());
        Log.d(getClass().getName(), "Duration: " + playableItem.getDuration());
        Log.d(getClass().getName(),
                "MimeType: " + playableItem.getMimeType());
        Log.d(getClass().getName(), "Title: " + playableItem.getTitle());
        Service<?, ?> service = getUpnpClient().getAVTransportService(getDevice());
        if (service == null) {
            Log.d(getClass().getName(),
                    "No AVTransport-Service found on Device: "
                            + getDevice().getDisplayString());
            return;
        }
        Log.d(getClass().getName(), "Action SetAVTransportURI ");
        final ActionState actionState = new ActionState();
        actionState.actionFinished = false;
        SetAVTransportURI setAVTransportURI = new InternalSetAVTransportURI(
                service, playableItem.getUri().toString(), actionState);
        getUpnpClient().getControlPoint().execute(setAVTransportURI);
        waitForActionComplete(actionState);
// Now start Playing
        Log.d(getClass().getName(), "Action Play");
        actionState.actionFinished = false;
        Play actionCallback = new Play(service) {
            @Override
            public void failure(ActionInvocation actioninvocation,
                                UpnpResponse upnpresponse, String s) {
                Log.d(getClass().getName(), "Failure UpnpResponse: "
                        + upnpresponse);
                Log.d(getClass().getName(),
                        upnpresponse != null ? "UpnpResponse: "
                                + upnpresponse.getResponseDetails() : "");
                Log.d(getClass().getName(), "s: " + s);
                actionState.actionFinished = true;
            }
            @Override
            public void success(ActionInvocation actioninvocation) {
                super.success(actioninvocation);
                actionState.actionFinished = true;
            }
        };
        getUpnpClient().getControlPoint().execute(actionCallback);
    }
    /**
     * Watchdog for async calls to complete
     */
    private void waitForActionComplete(final ActionState actionState) {
        actionState.watchdogFlag = false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                actionState.watchdogFlag = true;
            }
        }, 30000L); // 30sec. Watchdog
        while (!(actionState.actionFinished || actionState.watchdogFlag)) {
// wait for local device is connected
        }
        if (actionState.watchdogFlag) {
            Log.d(getClass().getName(), "Watchdog timeout!");
        }
        if (actionState.actionFinished) {
            Log.d(getClass().getName(), "Action completed!");
        }
    }
    private static class InternalSetAVTransportURI extends SetAVTransportURI {
        ActionState actionState = null;
        private InternalSetAVTransportURI(Service service, String uri,
                                          ActionState actionState) {
            super(service, uri);
            this.actionState = actionState;
        }
        @Override
        public void failure(ActionInvocation actioninvocation,
                            UpnpResponse upnpresponse, String s) {
            Log.d(getClass().getName(), "Failure UpnpResponse: " + upnpresponse);
            if (upnpresponse != null) {
                Log.d(getClass().getName(),
                        "UpnpResponse: " + upnpresponse.getResponseDetails());
                Log.d(getClass().getName(),
                        "UpnpResponse: " + upnpresponse.getStatusMessage());
                Log.d(getClass().getName(),
                        "UpnpResponse: " + upnpresponse.getStatusCode());
            }
            Log.d(getClass().getName(), "s: " + s);
            actionState.actionFinished = true;
        }
        @Override
        public void success(ActionInvocation actioninvocation) {
            super.success(actioninvocation);
            actionState.actionFinished = true;
        }
    }
    private static class ActionState {
        public boolean actionFinished = false;
        public boolean watchdogFlag = false;
    }
    /*
    * (non-Javadoc)
    * @see de.yaacc.player.AbstractPlayer#getNotificationIntent()
    */
    @Override
    protected PendingIntent getNotificationIntent(){
        Intent notificationIntent = new Intent(getContext(),
                AVTransportPlayerActivity.class);
        Log.d(getClass().getName(), "Put id into intent: " + getId());
        notificationIntent.setData(Uri.parse("http://0.0.0.0/"+getId()+"")); //just for making the intents different http://stackoverflow.com/questions/10561419/scheduling-more-than-one-pendingintent-to-same-activity-using-alarmmanager
        notificationIntent.putExtra(PLAYER_ID, getId());
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0 ,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        return contentIntent;
    }
    /*
    * (non-Javadoc)
    * @see de.yaacc.player.AbstractPlayer#getNotificationId()
    */
    @Override
    protected int getNotificationId() {
        return id;
    }
    /* (non-Javadoc)
    * @see de.yaacc.player.AbstractPlayer#pause()
    */
    @Override
    public void pause() {
        super.pause();
        if(getDevice() == null) {
            Log.d(getClass().getName(),
                    "No receiver device found: "
                            + deviceId);
            return;
        }
        Service<?, ?> service = getUpnpClient().getAVTransportService(getDevice());
        if (service == null) {
            Log.d(getClass().getName(),
                    "No AVTransport-Service found on Device: "
                            +getDevice().getDisplayString());
            return;
        }
        Log.d(getClass().getName(), "Action Pause ");
        final ActionState actionState = new ActionState();
        actionState.actionFinished = false;
        Pause actionCallback = new Pause(service) {
            @Override
            public void failure(ActionInvocation actioninvocation,
                                UpnpResponse upnpresponse, String s) {
                Log.d(getClass().getName(), "Failure UpnpResponse: "
                        + upnpresponse);
                Log.d(getClass().getName(),
                        upnpresponse != null ? "UpnpResponse: "
                                + upnpresponse.getResponseDetails() : "");
                Log.d(getClass().getName(), "s: " + s);
                actionState.actionFinished = true;
            }
            @Override
            public void success(ActionInvocation actioninvocation) {
                super.success(actioninvocation);
                actionState.actionFinished = true;
            }
        };
        getUpnpClient().getControlPoint().execute(actionCallback);
    }
}