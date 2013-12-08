package org.teleal.cling.android;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.transport.Router;

/**
 * Provides a UPnP stack with Android configuration (WiFi network only) as an
 * application service component.
 * 
 * @author Logan Miller
 */
public class AndroidUpnpServiceImpl extends Service {

	protected UpnpService upnpService;
	protected Binder binder = new Binder();

	@Override
	public void onCreate() {
		super.onCreate();

		NetworkInterface ethernet = null;
		List<NetworkInterface> interfaces;
		Object manager_ = null;
		try {
			interfaces = Collections.list(NetworkInterface
					.getNetworkInterfaces());

			for (NetworkInterface iface : interfaces) {
				if (iface.getDisplayName().equals("eth0")) {
					ethernet = iface;
					break;
				}
			}		
		
		if(ethernet != null && ethernet.isUp()){
			manager_ = getSystemService("ethernet");
		} else {
			manager_ = getSystemService(Context.WIFI_SERVICE);
		}	
		
		} catch (SocketException e) {
			Log.d(getClass().getName(),
					"Exception while lookup Networkinterfaces", e);
			manager_ = getSystemService(Context.WIFI_SERVICE);
		}
		final Object manager = manager_;
		Log.d(getClass().getName(), "NetworkManager: " + manager);
		final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		upnpService = new UpnpServiceImpl(createConfiguration(manager)) {
			@Override
			protected Router createRouter(ProtocolFactory protocolFactory,
					Registry registry) {
				AndroidWifiSwitchableRouter router = AndroidUpnpServiceImpl.this
						.createRouter(getConfiguration(), protocolFactory,
								manager, connectivityManager);
				if (!ModelUtil.ANDROID_EMULATOR
						&& isListeningForConnectivityChanges()) {
					// Only register for network connectivity changes if we are
					// not running on emulator
					registerReceiver(router.getBroadcastReceiver(),
							new IntentFilter(
									"android.net.conn.CONNECTIVITY_CHANGE"));
				}
				return router;
			}
		};

	}

	protected AndroidUpnpServiceConfiguration createConfiguration(
			Object wifiManager) {
		return new AndroidUpnpServiceConfiguration(wifiManager);
	}

	protected AndroidWifiSwitchableRouter createRouter(
			UpnpServiceConfiguration configuration,
			ProtocolFactory protocolFactory, Object manager,
			ConnectivityManager connectivityManager) {
		return new AndroidWifiSwitchableRouter(configuration, protocolFactory,
				manager, connectivityManager);
	}

	@Override
	public void onDestroy() {
		if (!ModelUtil.ANDROID_EMULATOR && isListeningForConnectivityChanges())
			unregisterReceiver(((AndroidWifiSwitchableRouter) upnpService
					.getRouter()).getBroadcastReceiver());
		upnpService.shutdown();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	protected boolean isListeningForConnectivityChanges() {
		return true;
	}

	protected class Binder extends android.os.Binder implements
			AndroidUpnpService {

		public UpnpService get() {
			return upnpService;
		}

		public UpnpServiceConfiguration getConfiguration() {
			return upnpService.getConfiguration();
		}

		public Registry getRegistry() {
			return upnpService.getRegistry();
		}

		public ControlPoint getControlPoint() {
			return upnpService.getControlPoint();
		}
	}

}