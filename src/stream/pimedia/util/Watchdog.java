package stream.pimedia.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class Watchdog {

    private boolean watchdogFlag = false;
    private long timeout = 0;
    private Timer watchdogTimer;

    private Watchdog(long timeout) {
        this.timeout = timeout;
    }

    public static  Watchdog createWatchdog(long timeout) {
        return new Watchdog(timeout);
    }

    public void start() {
        watchdogFlag = false;
        watchdogTimer = new Timer();
        watchdogTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                watchdogFlag = true;
            }
        }, timeout);
    }

    public boolean hasTimeout(){
        return watchdogFlag;
    }
}