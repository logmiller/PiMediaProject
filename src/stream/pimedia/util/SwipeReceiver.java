package stream.pimedia.util;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SwipeReceiver {
    void onRightToLeftSwipe();
    void onLeftToRightSwipe();
    void onTopToBottomSwipe();
    void onBottomToTopSwipe();
    void beginOnTouchProcessing(View v, MotionEvent event);
    void endOnTouchProcessing(View v, MotionEvent event);
}