package stream.pimedia.browser;

import java.util.Collection;
import java.util.LinkedList;
import org.teleal.cling.model.meta.Device;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import stream.pimedia.R;
/**
 * Created by Logan on 12/7/13.
 */
public class BrowseReceiverDeviceAdapter extends BaseAdapter {
    LinkedList<Device> devices;
    private LayoutInflater inflator;
    private LinkedList<Device> selectedDevices;
    public BrowseReceiverDeviceAdapter(Context ctx, Collection<Device> devices, Collection<Device> selectedDevices) {
        super();
        this.devices = new LinkedList<Device>(devices);
        this.selectedDevices = new LinkedList<Device>(selectedDevices);
        inflator = LayoutInflater.from(ctx);
    }
    @Override
    public int getCount() {
        return devices.size();
    }
    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }
    @Override
    public long getItemId(int position) {
// TODO Auto-generated method stub
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.browse_item_checkable, parent, false);
            Log.d(getClass().getName(), "New view created");
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView
                    .findViewById(R.id.browseItemIcon);
            holder.name = (TextView) convertView
                    .findViewById(R.id.browseItemName);
            holder.checkBox= (CheckBox) convertView
                    .findViewById(R.id.browseItemCheckbox);
            convertView.setTag(holder);
        } else {
            Log.d(getClass().getName(), "view already there");
            holder = (ViewHolder) convertView.getTag();
        }
        holder.icon.setImageResource(R.drawable.device);
        Device currentDevice = (Device) getItem(position);
        holder.name.setText(currentDevice.getDisplayString());
        holder.checkBox.setChecked(selectedDevices.contains(currentDevice));
        Log.d(getClass().getName(), "checkBox isChecked (" + currentDevice.getDisplayString()+"):" + holder.checkBox.isChecked());
        return convertView;
    }
    public void setDevices(Collection<Device<?,?,?>> devices) {
        this.devices = new LinkedList<Device>();
        this.devices.addAll(devices);
    }
    public void setSelectedDevices(Collection<Device<?,?,?>> devices) {
        this.selectedDevices = new LinkedList<Device>();
        this.selectedDevices.addAll(devices);
    }
    public void addSelectedDevice(Device<?,?,?> device) {
        this.selectedDevices.add(device);
    }
    public void removeSelectedDevice(Device<?,?,?> device) {
        this.selectedDevices.remove(device);
    }
    static class ViewHolder{
        ImageView icon;
        TextView name;
        CheckBox checkBox;
    }
}