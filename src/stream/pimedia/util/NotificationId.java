package stream.pimedia.util;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
public enum NotificationId {
    LOCAL_BACKGROUND_MUSIC_PLAYER(1),
    AVTRANSPORT_PLAYER(2),
    LOCAL_IMAGE_PLAYER(3),
    MULTI_CONTENT_PLAYER(4),
    UPNP_SERVER(5);


    private int id=0;


    private NotificationId(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }

}