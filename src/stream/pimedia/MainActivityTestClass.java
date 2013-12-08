package stream.pimedia;

import android.annotation.TargetApi;
import android.test.ActivityInstrumentationTestCase2;
import stream.pimedia.browser.BrowseActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/7/13
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */
@TargetApi(3)
public class MainActivityTestClass extends ActivityInstrumentationTestCase2<BrowseActivity> {
    public MainActivityTestClass() {
        super("stream.pimedia", BrowseActivity.class);
    }
}