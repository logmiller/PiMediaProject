package stream.pimedia.browser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.support.model.DIDLObject;
import stream.pimedia.R;
import stream.pimedia.player.Player;
import stream.pimedia.player.PlayerFactory;
import stream.pimedia.settings.SettingsActivity;
import stream.pimedia.upnp.UpnpClient;
import stream.pimedia.upnp.UpnpClientListener;
import stream.pimedia.upnp.server.PiMediaUpnpServerService;
import stream.pimedia.util.AboutActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class BrowseActivity extends Activity implements OnClickListener,
        UpnpClientListener {
    public static UpnpClient uClient = null;
    private BrowseItemAdapter bItemAdapter;
    BrowseItemClickListener bItemClickListener = null;
    BrowseDeviceClickListener bDeviceClickListener = null;
    BrowseReceiverDeviceClickListener bReceiverDeviceClickListener = null;
    private DIDLObject selectedDIDLObject;
    private SharedPreferences preferences = null;
    private Intent serverService = null;
    protected ListView contentList;
    private static Navigator navigator = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = new Navigator();
        setContentView(R.layout.browse);
// local server startup
        uClient = new UpnpClient();
        uClient.initialize(getApplicationContext());
// load preferences
        preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
// initialize click listener
        bItemClickListener = new BrowseItemClickListener();
// initialize click listener
        bDeviceClickListener = new BrowseDeviceClickListener();
// initialize click listener
        bReceiverDeviceClickListener = new BrowseReceiverDeviceClickListener(this);
// Define where to show the folder contents for media
        contentList = (ListView) findViewById(R.id.itemList);
        registerForContextMenu(contentList);
// remove the buttons if local playback is enabled and background
// playback is not enabled
// FIXME: Include background playback
        if (uClient.isLocalPlaybackEnabled()) {
            activateControls(false);
        }
// initialize buttons
        ImageButton btnPrev = (ImageButton) findViewById(R.id.controlPrev);
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
// FIXME: uClient.playbackPrev();
//FIXME: Until context menu isn't working using the prev-button for playAll
//a little easter egg
                if(BrowseItemClickListener.currentObject != null){
                    List<Player> players = uClient.initializePlayers(BrowseItemClickListener.currentObject);
                    for (Player player : players) {
                        if(player != null){
                            player.play();
                        }
                    }
                }
            }
        });
        ImageButton btnDev = (ImageButton) findViewById(R.id.controlDevices);
        btnDev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                navigator.pushPosition(new Position(Navigator.PROVIDER_DEVICE_SELECT_LIST_OBJECT_ID, uClient.getProviderDevice()));
                populateDeviceList();
            }
        });
        ImageButton btnRecDev = (ImageButton) findViewById(R.id.controlReceiverDevices);
        btnRecDev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                navigator.pushPosition(new Position(Navigator.RECEIVER_DEVICE_SELECT_LIST_OBJECT_ID, uClient.getProviderDevice()));
                populateReceiverDeviceList();
            }
        });
        ImageButton btnStop = (ImageButton) findViewById(R.id.controlStop);
        btnStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//populateDeviceList();
                if(PlayerFactory.getCurrentPlayers().size() > 0){
                    Player player = PlayerFactory.getCurrentPlayers().get(0);
                    player.stop();
                }
// FIXME: uClient.playbackStop();
// FIXME: ImageButton btnStop = (ImageButton) findViewById(R.id.controlStop);
// FIXME: btnStop.setVisibility(View.INVISIBLE);
            }
        });
        ImageButton btnNext = (ImageButton) findViewById(R.id.controlNext);
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
// FIXME: uClient.playbackNext();
//FIXME: Until there is no refresh button using the next button
                uClient.searchDevices();
            }
        });
// add ourself as listener
        uClient.addUpnpClientListener(this);
        if (uClient.getProviderDevice() !=null) {
            showMainFolder();
        } else {
            populateDeviceList();
        }
    }
    /**
     * load app preferences
     * @return app preferences
     */
    private SharedPreferences getPrefereces(){
        if (preferences == null){
            preferences = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
        }
        return preferences;
    }
    @Override
    public void onResume() {
// Intent svc = new Intent(getApplicationContext(), PiMediaUpnpServerService.class);
        if (preferences.getBoolean(
                getString(R.string.settings_local_server_chkbx), false)) {
// Start upnpserver service for avtransport
            getApplicationContext().startService(getYaaccUpnpServerService());
            Log.d(this.getClass().getName(), "Starting local service");
        } else {
            getApplicationContext().stopService(getYaaccUpnpServerService());
            Log.d(this.getClass().getName(), "Stopping local service");
        }
        super.onResume();
    }
    /**
     * Singleton to avoid multiple instances when switch
     * @return
     */
    private Intent getYaaccUpnpServerService(){
        if (serverService == null){
            serverService = new Intent(getApplicationContext(),
                    PiMediaUpnpServerService.class);
        }
        return serverService;
    }
    /**
     * Tries to populate the browsing area if a providing device is configured
     */
    private void showMainFolder() {
        Device providerDevice = getProviderDevice();
        if (providerDevice != null) {
            populateItemList(providerDevice);
        } else {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.browse_no_content_found), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
/**
 * Navigation in option menu
 */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.yaacc_about:
                AboutActivity.showAbout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onClick(View v) {
        Device providerDevice = getProviderDevice();
        populateItemList(providerDevice);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return bItemClickListener.onContextItemSelected(selectedDIDLObject,
                item, getApplicationContext());
    }
    @Override
/**
 * Stepps 'up' in the folder hierarchy or closes App if on device level.
 */
    public void onBackPressed() {
        Log.d(BrowseActivity.class.getName(), "onBackPressed() CurrentPosition: " + navigator.getCurrentPosition());
        String currentObjectId = navigator.getCurrentPosition().getObjectId();
        if (Navigator.ITEM_ROOT_OBJECT_ID.equals(currentObjectId)) {
            navigator.pushPosition(Navigator.DEVICE_LIST_POSITION);
            populateDeviceList();
        } else if (Navigator.DEVICE_OVERVIEW_OBJECT_ID.equals(currentObjectId)){
            uClient.shutdown();
            super.finish();
        } else {
            final ListView itemList = (ListView) findViewById(R.id.itemList);
            Position pos = navigator.popPosition(); // First pop is our
// currentPosition
            bItemAdapter = new BrowseItemAdapter(this,
                    navigator.getCurrentPosition());
            itemList.setAdapter(bItemAdapter);
            BrowseItemClickListener bItemClickListener = new BrowseItemClickListener();
            itemList.setOnItemClickListener(bItemClickListener);
        }
    }
    @Override
/**
 * Creates context menu for certain actions on a specific item.
 */
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (v instanceof ListView) {
            ListView listView = (ListView) v;
            Object item = listView.getAdapter().getItem(info.position);
            if (item instanceof DIDLObject) {
                selectedDIDLObject = (DIDLObject) item;
            }
        }
        menu.setHeaderTitle(v.getContext().getString(
                R.string.browse_context_title));
        ArrayList<String> menuItems = new ArrayList<String>();
// TODO: I think there might be some item dependent actions in the
// future, so this is designed as a dynamic list
        if (!currentlyShowingDevices()){
            menuItems.add(v.getContext().getString(R.string.browse_context_play));
        }
        menuItems.add(v.getContext().getString(
                R.string.browse_context_add_to_playplist));
        menuItems.add(v.getContext()
                .getString(R.string.browse_context_download));
// TODO: Check via bytecode whether listsize is calculated every loop or
// just once, if do calculation before calling the loop
        for (int i = 0; i < menuItems.toArray(new String[menuItems.size()]).length; i++) {
            menu.add(Menu.NONE, i, i, menuItems.get(i));
        }
    }
    /**
     * Shows/Hides the controls
     *
     * @param activated
     * true if the controls should be shown
     */
    public void activateControls(boolean activated) {
        RelativeLayout controls = (RelativeLayout) findViewById(R.id.controls);
        if (activated) {
            controls.setVisibility(View.GONE);
        } else {
            controls.setVisibility(View.VISIBLE);
        }
    }
    /**
     * Selects the place in the UI where the items are shown and renders the
     * content directory
     *
     * @param providerDevice
     * device to access
     */
    private void populateItemList(Device providerDevice) {
        this.runOnUiThread(new Runnable() {
            public void run() {
// Load adapter if selected device is configured and found
                Position pos = new Position(Navigator.ITEM_ROOT_OBJECT_ID, BrowseActivity.uClient.getProviderDevice());
                navigator.pushPosition(pos);
                bItemAdapter = new BrowseItemAdapter(getApplicationContext(),
                        pos);
                contentList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                contentList.setAdapter(bItemAdapter);
                contentList.setOnItemClickListener(bItemClickListener);
            }
        });
    }
    /**
     * Shows all available devices in the main device list.
     */
    private void populateDeviceList(){
        this.runOnUiThread(new Runnable() {
            public void run() {
// Define where to show the folder contents
                ListView deviceList = (ListView) findViewById(R.id.itemList);
                deviceList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                BrowseDeviceAdapter bDeviceAdapter = new BrowseDeviceAdapter(getApplicationContext(), new LinkedList<Device>(uClient.getDevicesProvidingContentDirectoryService()));
                deviceList.setAdapter(bDeviceAdapter);
                deviceList.setOnItemClickListener(bDeviceClickListener);
            }
        });
    }
    /**
     * Shows all available devices in the receiver device list.
     */
    private void populateReceiverDeviceList(){
        this.runOnUiThread(new Runnable() {
            public void run() {
// Define where to show the folder contents
                ListView deviceList = (ListView) findViewById(R.id.itemList);
                deviceList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                LinkedList<Device> receiverDevices = new LinkedList<Device>(uClient.getDevicesProvidingAvTransportService());
                BrowseReceiverDeviceAdapter bDeviceAdapter = new BrowseReceiverDeviceAdapter(getApplicationContext(), receiverDevices, uClient.getReceiverDevices());
                deviceList.setAdapter(bDeviceAdapter);
                deviceList.setOnItemClickListener(bReceiverDeviceClickListener);
            }
        });
    }
    /**
     * Loads the device providing media files, as it is configured in the
     * settings
     *
     * @return configured device
     */
    private Device getProviderDevice() {
// Get Try to get selected device
        Device selectedDevice = null;
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        if (preferences.getString(
                getString(R.string.settings_selected_provider_title), null) != null) {
            selectedDevice = uClient
                    .getDevice(preferences
                            .getString(
                                    getString(R.string.settings_selected_provider_title),
                                    null));
        }
        return selectedDevice;
    }
    @Override
/**
 * Refreshes the shown devices when device is added.
 */
    public void deviceAdded(Device<?, ?, ?> device) {
        if (currentlyShowingDevices()) {
            preferences = getPrefereces();
            Device formerDevice = null;
            if (preferences.getString(
                    getString(R.string.settings_selected_provider_title), null) != null) {
                formerDevice = uClient
                        .getDevice(preferences
                                .getString(
                                        getString(R.string.settings_selected_provider_title),
                                        null));
            }
            if(formerDevice != null && device.equals(formerDevice)){
                showMainFolder();
            } else {
                populateDeviceList();
            }
        }
    }
    @Override
/**
 * Refreshes the shown devices when device is removed.
 */
    public void deviceRemoved(Device<?, ?, ?> device) {
        Log.d(this.getClass().toString(), "device removal called");
        if (currentlyShowingDevices()) {
            populateDeviceList();
        }
    }
    @Override
    public void deviceUpdated(Device<?, ?, ?> device) {
// TODO Auto-generated method stub
    }
    /**
     * Checks whether currently showing devices or folders inside a certain device
     * @return true if showing devices, false otherwise
     */
    public boolean currentlyShowingDevices(){
        if (Navigator.DEVICE_OVERVIEW_OBJECT_ID.equals(navigator.getCurrentPosition().getObjectId()))	{
            return true;
        }
        return false;
    }
    /**
     * Returns Object containing about the current navigation way
     * @return information about current navigation
     */
    public static Navigator getNavigator(){
        return navigator;
    }
}