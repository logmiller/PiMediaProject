package stream.pimedia.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import stream.pimedia.R;
/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageViewerSettingsActivity extends PreferenceActivity{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.image_viewer_preference);

    }
}
