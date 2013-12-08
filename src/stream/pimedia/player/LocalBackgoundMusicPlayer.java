package stream.pimedia.player;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import stream.pimedia.musicplayer.BackgroundMusicBroadcastReceiver;
import stream.pimedia.musicplayer.BackgroundMusicService;
import stream.pimedia.upnp.UpnpClient;
import stream.pimedia.util.NotificationId;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocalBackgoundMusicPlayer extends AbstractPlayer {

    private boolean background = true;
    private BackgroundMusicService musicService;
    private Timer commandExecutionTimer;

    public LocalBackgoundMusicPlayer(UpnpClient upnpClient, String name) {
        this(upnpClient);
        setName(name);
    }

    public LocalBackgoundMusicPlayer(UpnpClient upnpClient) {
        super(upnpClient);
        Context context = upnpClient.getContext();
        Log.d(getClass().getName(), "Starting background music service... ");
        Intent svc = new Intent(context, BackgroundMusicService.class);
        context.startService(svc);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.yaacc.player.AbstractPlayer#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent svc = new Intent(getContext(), BackgroundMusicService.class);
        getContext().stopService(svc);
    }

    /* (non-Javadoc)
     * @see de.yaacc.player.AbstractPlayer#pause()
     */
    @Override
    public void pause() {
        super.pause();
        commandExecutionTimer = new Timer();
        commandExecutionTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(BackgroundMusicBroadcastReceiver.ACTION_PAUSE);
                getContext().sendBroadcast(intent);

            }
        }, 1000L);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.yaacc.player.AbstractPlayer#stopItem(de.yaacc.player.PlayableItem)
     */
    @Override
    protected void stopItem(PlayableItem playableItem) {

        // Communicating with the activity is only possible after the activity
        // is started
        // if we send an broadcast event to early the activity won't be up
        // in order there is no known way to query the activity state
        // we are sending the command delayed
        commandExecutionTimer = new Timer();
        commandExecutionTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(BackgroundMusicBroadcastReceiver.ACTION_STOP);
                getContext().sendBroadcast(intent);

            }
        }, 1000L);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.yaacc.player.AbstractPlayer#loadItem(de.yaacc.player.PlayableItem)
     */
    @Override
    protected Object loadItem(PlayableItem playableItem) {
        final Uri uri = playableItem.getUri();
        // Communicating with the activity is only possible after the activity
        // is started
        // if we send an broadcast event to early the activity won't be up
        // in order there is no known way to query the activity state
        // we are sending the command delayed
        commandExecutionTimer = new Timer();
        commandExecutionTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(BackgroundMusicBroadcastReceiver.ACTION_SET_DATA);
                intent.putExtra(BackgroundMusicBroadcastReceiver.ACTION_SET_DATA_URI_PARAM, uri);
                getContext().sendBroadcast(intent);
            }
        }, 500L);
        return uri;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.yaacc.player.AbstractPlayer#startItem(de.yaacc.player.PlayableItem,
     * java.lang.Object)
     */
    @Override
    protected void startItem(PlayableItem playableItem, Object loadedItem) {

        // Communicating with the activity is only possible after the activity
        // is started
        // if we send an broadcast event to early the activity won't be up
        // in order there is no known way to query the activity state
        // we are sending the command delayed
        commandExecutionTimer = new Timer();
        commandExecutionTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(BackgroundMusicBroadcastReceiver.ACTION_PLAY);
                getContext().sendBroadcast(intent);
            }
        }, 600L);
    }
    /*
     * (non-Javadoc)
     * @see de.yaacc.player.AbstractPlayer#getNotificationIntent()
     */
    @Override
    protected PendingIntent getNotificationIntent(){
        Intent notificationIntent = new Intent(getContext(),
                MusicPlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0,
                notificationIntent, 0);
        return contentIntent;
    }

    /*
     * (non-Javadoc)
     * @see de.yaacc.player.AbstractPlayer#getNotificationId()
     */
    @Override
    protected int getNotificationId() {

        return NotificationId.LOCAL_BACKGROUND_MUSIC_PLAYER.getId();
    }
}
