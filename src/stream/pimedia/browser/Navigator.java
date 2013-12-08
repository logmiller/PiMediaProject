package stream.pimedia.browser;

import java.util.LinkedList;
import android.util.Log;
/**
 * Created by Logan on 12/7/13.
 */
public class Navigator {
    public final static String DEVICE_OVERVIEW_OBJECT_ID = "-1";
    public final static String PROVIDER_DEVICE_SELECT_LIST_OBJECT_ID = "-2";
    public final static String RECEIVER_DEVICE_SELECT_LIST_OBJECT_ID = "-3";
    public final static String ITEM_ROOT_OBJECT_ID = "0";
    public final static Position DEVICE_LIST_POSITION = new Position(DEVICE_OVERVIEW_OBJECT_ID, null);
    public Navigator(){
        navigationPath = new LinkedList<Position>();
        Log.d(getClass().getName(), "pushNavigation: " + DEVICE_LIST_POSITION.getObjectId());
        navigationPath.add(DEVICE_LIST_POSITION);
    }
    private LinkedList<Position> navigationPath;
    /**
     * Provides information about the current position.
     * @return current position or DEVICE_LIST_POSITION if on device level
     */
    public Position getCurrentPosition(){
        if (navigationPath.isEmpty()){
            return DEVICE_LIST_POSITION;
        }
        return navigationPath.peekLast();
    }
    public void pushPosition(Position pos){
        Log.d(getClass().getName(), "pushNavigation: " + pos.getObjectId());
        navigationPath.add(pos);
    }
    /**
     * Provides information about the current position and removes it from the navigation path.
     * @return current position or DEVICE_LIST_POSITION if on device level
     */
    public Position popPosition(){
        Position result = null;
        if (navigationPath.isEmpty()){
            result = DEVICE_LIST_POSITION;
        }else{
            result = navigationPath.removeLast();
        }
        Log.d(getClass().getName(), "popNavigation: " + result.getObjectId());
        return result;
    }
}