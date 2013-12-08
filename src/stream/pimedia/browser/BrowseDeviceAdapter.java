package stream.pimedia.browser;

import java.util.Collection;
import java.util.LinkedList;

import org.teleal.cling.model.meta.Device;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import stream.pimedia.R;
import stream.pimedia.browser.BrowseItemAdapter.ViewHolder;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class BrowseDeviceAdapter extends BaseAdapter {

    LinkedList<Device> devices;
    private LayoutInflater inflator;


    public BrowseDeviceAdapter(Context ctx, LinkedList <Device> devices) {
        super();

        this.devices = devices;

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

        if(convertView == null){
            convertView = inflator.inflate(R.layout.browse_item,parent,false);

            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.browseItemIcon);
            holder.name = (TextView) convertView.findViewById(R.id.browseItemName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageResource(R.drawable.device);
        holder.name.setText(((Device)getItem(position)).getDisplayString());

        return convertView;
    }

    public void setDevices(Collection <Device> devices){
        this.devices = new LinkedList<Device>();
        this.devices.addAll(devices);
    }

}