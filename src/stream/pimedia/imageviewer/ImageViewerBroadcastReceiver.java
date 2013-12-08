package stream.pimedia.imageviewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImageViewerBroadcastReceiver extends BroadcastReceiver {

    public static String ACTION_PLAY = "stream.pimedia.imageviewer.ActionPlay";
    public static String ACTION_STOP = "stream.pimedia.imageviewer.ActionStop";
    public static String ACTION_PAUSE = "stream.pimedia.imageviewer.ActionPause";
    public static String ACTION_NEXT = "stream.pimedia.imageviewer.ActionNext";
    public static String ACTION_PREVIOUS = "stream.pimediaimageviewer.ActionPrevious";
    public static String ACTION_EXIT = "stream.pimedia.imageviewer.ActionExit";


    private ImageViewerActivity imageViewer;




    /*public ImageViewerBroadcastReceiver(ImageViewerActivity imageViewer) {
        Log.d(this.getClass().getName(), "Starting Broadcast Receiver..." );
        assert(imageViewer != null);
        this.imageViewer = imageViewer;

    }*/

    public void setImageViewer(ImageViewerActivity imageViewer) {
        this.imageViewer = imageViewer;
    }

    /* (non-Javadoc)
         * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
         */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getName(), "Received Action: " + intent.getAction());
        if(imageViewer == null) return;
        Log.d(this.getClass().getName(), "Execute Action on imageViewer: " + imageViewer);
        if(ACTION_PLAY.equals(intent.getAction())){
            imageViewer.play();
        }else if(ACTION_PAUSE.equals(intent.getAction())){
            imageViewer.pause();
        }else if(ACTION_STOP.equals(intent.getAction())){
            imageViewer.stop();
        }else if(ACTION_PREVIOUS.equals(intent.getAction())){
            imageViewer.previous();
        }else if(ACTION_NEXT.equals(intent.getAction())){
            imageViewer.next();
        }else if(ACTION_EXIT.equals(intent.getAction())){
            imageViewer.finish();
        }


    }

    public void registerReceiver() {
        Log.d(this.getClass().getName(), "Register Receiver" );
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_NEXT);
        intentFilter.addAction(ACTION_PREVIOUS);
        intentFilter.addAction(ACTION_STOP);
        imageViewer.registerReceiver(this, intentFilter);


    }

}
