package stream.pimedia.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import stream.pimedia.R;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class AboutActivity extends Activity {
    public static void showAbout(Activity activity) {
        activity.startActivity(new Intent(activity,AboutActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);

    }
}
