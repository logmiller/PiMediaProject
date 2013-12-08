package stream.pimedia.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BackgroundMusicBroadcastReceiver extends BroadcastReceiver {

    public static String ACTION_PLAY = "de.yaacc.musicplayer.ActionPlay";
    public static String ACTION_STOP = "de.yaacc.musicplayer.ActionStop";
    public static String ACTION_PAUSE = "de.yaacc.musicplayer.ActionPause";
    public static String ACTION_SET_DATA = "de.yaacc.musicplayer.ActionSetData";
    public static String ACTION_SET_DATA_URI_PARAM = "de.yaacc.musicplayer.ActionSetDataUriParam";



    private BackgroundMusicService backgroundMusicService;

    public BackgroundMusicBroadcastReceiver(BackgroundMusicService backgroundMusicService) {
        Log.d(this.getClass().getName(), "Starting Broadcast Receiver..." );
        assert(backgroundMusicService != null);
        this.backgroundMusicService = backgroundMusicService;

    }

    /* (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getName(), "Received Action: " + intent.getAction());
        if(backgroundMusicService == null) return;
        Log.d(this.getClass().getName(), "Execute Action on backgroundMusicService: " + backgroundMusicService);
        if(ACTION_PLAY.equals(intent.getAction())){
            backgroundMusicService.play();
        }else if(ACTION_PAUSE.equals(intent.getAction())){
            backgroundMusicService.pause();
        }else if(ACTION_STOP.equals(intent.getAction())){
            backgroundMusicService.stop();
        }else if(ACTION_SET_DATA.equals(intent.getAction())){
            backgroundMusicService.setMusicUri((Uri)intent.getParcelableExtra(ACTION_SET_DATA_URI_PARAM));
        }


    }

    public void registerReceiver() {
        Log.d(this.getClass().getName(), "Register Receiver" );
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_STOP);

        intentFilter.addAction(ACTION_SET_DATA);
        backgroundMusicService.registerReceiver(this, intentFilter);
    }
}
