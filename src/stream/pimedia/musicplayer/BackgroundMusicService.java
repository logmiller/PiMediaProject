package stream.pimedia.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BackgroundMusicService extends Service {

    public static final String URIS = "URIS_PARAM"; // String Intent parameter
    private MediaPlayer player;
    private BackgroundMusicBroadcastReceiver backgroundMusicBroadcastReceiver;


    public BackgroundMusicService() {
        super();

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.getClass().getName(), "On Create");

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        Log.d(this.getClass().getName(), "On Destroy");
        if (player != null) {
            player.stop();
            player.release();
        }
        unregisterReceiver(backgroundMusicBroadcastReceiver);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(this.getClass().getName(), "On Bind");
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onStart(android.content.Intent, int)
     */
    @Override
    public void onStart(Intent intent, int startid) {
        Log.d(this.getClass().getName(), "On Start");
        backgroundMusicBroadcastReceiver = new BackgroundMusicBroadcastReceiver(
                this);
        backgroundMusicBroadcastReceiver.registerReceiver();
        if (player == null) {
            player = new MediaPlayer();
        } else {
            player.stop();
        }
        try {
            if (intent.getData() != null) {
                player.setDataSource(this, intent.getData());
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName(),
                    "Exception while changing datasource uri", e);

        }
        if (player != null) {
            player.setVolume(100, 100);
            Log.i(this.getClass().getName(), "is Playing:" + player.isPlaying());
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    public void play() {
        if (player != null && !player.isPlaying()) {
            player.start();
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }

    public void setMusicUri(Uri uri) {
        Log.d(this.getClass().getName(),
                "changing datasource uri to:" + uri.toString());
        if (player != null) {
            player.release();
        }
        player = new MediaPlayer();
        try {
            if (player.isPlaying()) {
                stop();
            }
            player.setDataSource(this, uri);
            player.prepare();
        } catch (Exception e) {
            Log.e(this.getClass().getName(),
                    "Exception while changing datasource uri", e);
        }
    }
}
