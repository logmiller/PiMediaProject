package stream.pimedia.player;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;
import stream.pimedia.R;
import stream.pimedia.upnp.UpnpClient;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocalThirdPartyMusicPlayer extends AbstractPlayer {
    PendingIntent pendingIntent;
    private int musicAppPid=0;

    public LocalThirdPartyMusicPlayer(UpnpClient upnpClient, String name) {
        this(upnpClient);
        setName(name);
    }


    public LocalThirdPartyMusicPlayer(UpnpClient upnpClient) {
        super(upnpClient);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.yaacc.player.AbstractPlayer#stopItem(de.yaacc.player.PlayableItem)
     */
    @Override
    protected void stopItem(PlayableItem playableItem) {
        if(musicAppPid != 0){
            Process.killProcess(musicAppPid);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.yaacc.player.AbstractPlayer#loadItem(de.yaacc.player.PlayableItem)
     */
    @Override
    protected Object loadItem(PlayableItem playableItem) {
        Uri uri = playableItem.getUri();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setDataAndType(uri, playableItem.getMimeType());
        return intent;
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
        if (loadedItem instanceof Intent) {

            Intent intent = (Intent) loadedItem;
            try {
                getContext().startActivity(intent);
                discoverMusicActivityPid();


            } catch (ActivityNotFoundException anfe) {
                Resources res = getContext().getResources();
                String text = String.format(
                        res.getString(R.string.error_no_activity_found),
                        intent.getType());
                Toast toast = Toast.makeText(getContext(), text,
                        Toast.LENGTH_LONG);
                toast.show();
            }
        } else {
            Log.d(getClass().getName(),
                    "Hey thats stange loaded item isn't an intent");
        }
    }

    private void discoverMusicActivityPid() {

        ActivityManager activityManager = (ActivityManager) getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        List<RunningAppProcessInfo> apps = activityManager.getRunningAppProcesses();
        String packageName = services.get(0).topActivity.getPackageName(); //fist Task is the last started task
        for (int i = 0; i < apps.size(); i++) {
            if(apps.get(i).processName .equals(packageName)){
                musicAppPid = apps.get(i).pid;
                Log.d(getClass().getName(), "Found music activity process: " + apps.get(i).processName + " PID: " + musicAppPid);
            }

        }
    }

    /* (non-Javadoc)
     * @see de.yaacc.player.AbstractPlayer#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(musicAppPid != 0){
            Process.killProcess(musicAppPid);
        }
    }
}