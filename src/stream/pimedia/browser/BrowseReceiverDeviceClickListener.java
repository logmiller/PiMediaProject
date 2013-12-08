package stream.pimedia.browser;

import org.teleal.cling.model.meta.Device;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import stream.pimedia.R;

/**
 * Created by Logan on 12/7/13.
 */
public class BrowseReceiverDeviceClickListener implements OnItemClickListener {
    private BrowseActivity activity;
    public BrowseReceiverDeviceClickListener(BrowseActivity activity) {
        this.activity = activity;
    }
    @Override
    public void onItemClick(AdapterView<?> listView, View itemView,
                            int position, long id) {
        ListView a = (ListView) listView.findViewById(R.id.itemList);
        BrowseReceiverDeviceAdapter adapter = (BrowseReceiverDeviceAdapter) listView
                .getAdapter();
        SparseBooleanArray checked = a.getCheckedItemPositions();
        Log.d(getClass().getName(), "position: " + position);
        CheckBox checkBox = (CheckBox) itemView
                .findViewById(R.id.browseItemCheckbox);
        Device device = (Device) adapter.getItem(position);
        if (checkBox.isChecked()) {
            Log.d(getClass().getName(), "isChecked:" + device.getDisplayString());
            adapter.removeSelectedDevice(device);
            BrowseActivity.uClient.removeReceiverDevice(device);
            checkBox.setChecked(false);
        } else {
            Log.d(getClass().getName(), "isNotChecked:" + device.getDisplayString());
            adapter.addSelectedDevice(device);
            BrowseActivity.uClient.addReceiverDevice(device);
            checkBox.setChecked(true);
        }
    }
}