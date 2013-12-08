package stream.pimedia.browser;

import android.view.View;
import org.teleal.cling.model.meta.Device;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import stream.pimedia.R;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class BrowseDeviceClickListener implements OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> listView, View arg1, int position, long id) {
        ListView a = (ListView) listView.findViewById(R.id.itemList);
        BrowseDeviceAdapter adapter = (BrowseDeviceAdapter) listView.getAdapter();

        BrowseActivity.uClient.setProviderDevice((Device)adapter.getItem(position));

        BrowseActivity.getNavigator().pushPosition(new Position(Navigator.ITEM_ROOT_OBJECT_ID, BrowseActivity.uClient.getProviderDevice()));

        BrowseItemAdapter bItemAdapter = new BrowseItemAdapter(
                listView.getContext(), Navigator.ITEM_ROOT_OBJECT_ID);
        a.setAdapter(bItemAdapter);

        BrowseItemClickListener bItemClickListener = new BrowseItemClickListener();
        a.setOnItemClickListener(bItemClickListener);
    }

}
