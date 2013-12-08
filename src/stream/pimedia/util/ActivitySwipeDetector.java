package stream.pimedia.util;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivitySwipeDetector implements OnTouchListener {

    static final String logTag = "ActivitySwipeDetector";
    private SwipeReceiver swipeReceiver;
    static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;

    public ActivitySwipeDetector(SwipeReceiver swipeReceiver) {
        this.swipeReceiver = swipeReceiver;
    }

    private void onRightToLeftSwipe() {
        Log.i(logTag, "RightToLeftSwipe!");
        swipeReceiver.onRightToLeftSwipe();
    }

    private void onLeftToRightSwipe() {
        Log.i(logTag, "LeftToRightSwipe!");
        swipeReceiver.onLeftToRightSwipe();
    }

    private void onTopToBottomSwipe() {
        Log.i(logTag, "onTopToBottomSwipe!");
        swipeReceiver.onTopToBottomSwipe();
    }

    private void onBottomToTopSwipe() {
        Log.i(logTag, "onBottomToTopSwipe!");
        swipeReceiver.onBottomToTopSwipe();
    }

    private void endOnTouchProcessing(View v, MotionEvent event) {
        Log.i(logTag, "endOnTouchProcessing!");
        swipeReceiver.endOnTouchProcessing(v, event);

    }

    private void beginOnTouchProcessing(View v, MotionEvent event) {
        Log.i(logTag, "beginOnTouchProcessing!");
        swipeReceiver.beginOnTouchProcessing(v, event);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        beginOnTouchProcessing(v, event);
        try {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;

                    // swipe horizontal?
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        // left or right
                        if (deltaX < 0) {
                            this.onLeftToRightSwipe();
                            return true;
                        }
                        if (deltaX > 0) {
                            this.onRightToLeftSwipe();
                            return true;
                        }
                    } else {
                        Log.i(logTag, "Swipe was only " + Math.abs(deltaX)
                                + " long, need at least " + MIN_DISTANCE);
                        return false; // We don't consume the event
                    }

                    // swipe vertical?
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        // top or down
                        if (deltaY < 0) {
                            this.onTopToBottomSwipe();
                            return true;
                        }
                        if (deltaY > 0) {
                            this.onBottomToTopSwipe();
                            return true;
                        }
                    } else {
                        Log.i(logTag, "Swipe was only " + Math.abs(deltaX)
                                + " long, need at least " + MIN_DISTANCE);
                        return false; // We don't consume the event
                    }

                    return true;
                }
            }

            return false;

        } finally {
            endOnTouchProcessing(v, event);
        }
    }
}