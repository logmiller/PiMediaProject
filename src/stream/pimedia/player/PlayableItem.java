package stream.pimedia.player;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayableItem {

    private String mimeType;
    private String title;
    private Uri uri;
    private long duration;


    public PlayableItem() {
        // TODO Auto-generated constructor stub
    }


    public String getMimeType() {
        return mimeType;
    }


    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public Uri getUri() {
        return uri;
    }


    public void setUri(Uri uri) {
        this.uri = uri;
    }


    public long getDuration() {
        return duration;
    }


    public void setDuration(long duration) {
        this.duration = duration;
    }

}
