package org.teleal.cling.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import org.teleal.cling.model.Constants;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.NetworkAddressFactory;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation appropriate for Android environment, avoids unavailable
 * methods.
 * 
 * @author Logan Miller
 */
public class AndroidNetworkAddressFactory implements NetworkAddressFactory {

	final private static Logger log = Logger
			.getLogger(NetworkAddressFactory.class.getName());

	protected NetworkInterface wifiInterface;
	protected List<InetAddress> bindAddresses = new ArrayList();

	/**
	 * Defaults to an ephemeral port.
	 */
	public AndroidNetworkAddressFactory(Object manager)
			throws InitializationException {
		wifiInterface = getWifiNetworkInterface(manager);

		if (wifiInterface == null)
			throw new InitializationException(
					"Could not discover WiFi network interface");
		log.info("Discovered WiFi network interface: "
				+ wifiInterface.getDisplayName());

		discoverBindAddresses();
	}

	protected void discoverBindAddresses() throws InitializationException {
		try {

			log.finer("Discovering addresses of interface: "
					+ wifiInterface.getDisplayName());
			for (InetAddress inetAddress : getInetAddresses(wifiInterface)) {
				if (inetAddress == null) {
					log.warning("Network has a null address: "
							+ wifiInterface.getDisplayName());
					continue;
				}

				if (isUsableAddress(inetAddress)) {
					log.fine("Discovered usable network interface address: "
							+ inetAddress.getHostAddress());
					bindAddresses.add(inetAddress);
				} else {
					log.finer("Ignoring non-usable network interface address: "
							+ inetAddress.getHostAddress());
				}
			}

		} catch (Exception ex) {
			throw new InitializationException(
					"Could not not analyze local network interfaces: " + ex, ex);
		}
	}

	protected boolean isUsableAddress(InetAddress address) {
		if (!(address instanceof Inet4Address)) {
			log.finer("Skipping unsupported non-IPv4 address: " + address);
			return false;
		}
		return true;
	}

	protected List<InetAddress> getInetAddresses(
			NetworkInterface networkInterface) {
		return Collections.list(networkInterface.getInetAddresses());
	}

	public InetAddress getMulticastGroup() {
		try {
			return InetAddress.getByName(Constants.IPV4_UPNP_MULTICAST_GROUP);
		} catch (UnknownHostException ex) {
			throw new RuntimeException(ex);
		}
	}

	public int getMulticastPort() {
		return Constants.UPNP_MULTICAST_PORT;
	}

	public int getStreamListenPort() {
		return 0; // Ephemeral
	}

	public NetworkInterface[] getNetworkInterfaces() {
		return new NetworkInterface[] { wifiInterface };
	}

	public InetAddress[] getBindAddresses() {
		return bindAddresses.toArray(new InetAddress[bindAddresses.size()]);
	}

	public byte[] getHardwareAddress(InetAddress inetAddress) {
		return null; // TODO: Get this from WifiInfo from WifiManager
	}

	public InetAddress getBroadcastAddress(InetAddress inetAddress) {
		return null; // TODO: No low-level network interface methods available
						// on Android API
	}

	public InetAddress getLocalAddress(NetworkInterface networkInterface,
			boolean isIPv6, InetAddress remoteAddress) {
		// TODO: This is totally random because we can't access low level
		// InterfaceAddress on Android!
		for (InetAddress localAddress : getInetAddresses(networkInterface)) {
			if (isIPv6 && localAddress instanceof Inet6Address)
				return localAddress;
			if (!isIPv6 && localAddress instanceof Inet4Address)
				return localAddress;
		}
		throw new IllegalStateException(
				"Can't find any IPv4 or IPv6 address on interface: "
						+ networkInterface.getDisplayName());
	}

	// Code from:
	// http://www.gubatron.com/blog/2010/09/19/android-programming-how-to-obtain-the-wifis-corresponding-networkinterface/

	public static NetworkInterface getWifiNetworkInterface(Object manager) {
		if (ModelUtil.ANDROID_EMULATOR) {
			return getEmulatorWifiNetworkInterface(manager);
		}
		return getActualNetworkInterface(manager);
	}

	public static NetworkInterface getEmulatorWifiNetworkInterface(
			Object manager) {
		// Return the first network interface that is not loopback
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface iface : interfaces) {
				List<InetAddress> addresses = Collections.list(iface
						.getInetAddresses());
				for (InetAddress address : addresses) {
					if (!address.isLoopbackAddress())
						return iface;
				}
			}
		} catch (Exception ex) {
			throw new InitializationException(
					"Could not find emulator's network interface: " + ex, ex);
		}
		return null;
	}

	public static NetworkInterface getActualNetworkInterface(Object manager) {
		if (manager instanceof WifiManager) {
			return getRealWifiNetworkInterface((WifiManager) manager);
		} else {
			try {
				List<NetworkInterface> interfaces = Collections
						.list(NetworkInterface.getNetworkInterfaces());
				for (NetworkInterface iface : interfaces) {
					if (iface.getDisplayName().equals("eth0")) {
						List<InetAddress> addresses = Collections.list(iface
								.getInetAddresses());
						for (InetAddress address : addresses) {
							if (!address.isLoopbackAddress()) {
								return iface;
							}
						}
					}
				}
			} catch (Exception ex) {
				throw new InitializationException(
						"Could not find emulator's network interface: " + ex,
						ex);
			}
		}
		// Return the first network interface that is not loopback

		return null;
	}

	public static NetworkInterface getRealWifiNetworkInterface(
			WifiManager manager) {

		Enumeration<NetworkInterface> interfaces = null;
		try {
			// the WiFi network interface will be one of these.
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			log.info("No network interfaces available");
			return null;
		}

		// We'll use the WiFiManager's ConnectionInfo IP address and compare it
		// with
		// the ips of the enumerated NetworkInterfaces to find the WiFi
		// NetworkInterface.

		// Wifi manager gets a ConnectionInfo object that has the ipAdress as an
		// int
		// It's endianness could be different as the one on java.net.InetAddress
		// maybe this varies from device to device, the android API has no
		// documentation on this method.
		int wifiIP = manager.getConnectionInfo().getIpAddress();

		// so I keep the same IP number with the reverse endianness
		int reverseWifiIP = Integer.reverseBytes(wifiIP);

		while (interfaces.hasMoreElements()) {

			NetworkInterface iface = interfaces.nextElement();

			// since each interface could have many InetAddresses...
			Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();
			while (inetAddresses.hasMoreElements()) {
				InetAddress nextElement = inetAddresses.nextElement();
				int byteArrayToInt = byteArrayToInt(nextElement.getAddress(), 0);

				// grab that IP in byte[] form and convert it to int, then
				// compare it
				// to the IP given by the WifiManager's ConnectionInfo. We
				// compare
				// in both endianness to make sure we get it.
				if (byteArrayToInt == wifiIP || byteArrayToInt == reverseWifiIP) {
					return iface;
				}
			}
		}

		return null;
	}

	static int byteArrayToInt(byte[] arr, int offset) {
		if (arr == null || arr.length - offset < 4)
			return -1;

		int r0 = (arr[offset] & 0xFF) << 24;
		int r1 = (arr[offset + 1] & 0xFF) << 16;
		int r2 = (arr[offset + 2] & 0xFF) << 8;
		int r3 = arr[offset + 3] & 0xFF;
		return r0 + r1 + r2 + r3;
	}

}
