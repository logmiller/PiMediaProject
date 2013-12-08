package org.teleal.cling.android;

import android.content.Context;
import android.net.wifi.WifiManager;
import org.teleal.cling.DefaultUpnpServiceConfiguration;
import org.teleal.cling.binding.xml.DeviceDescriptorBinder;
import org.teleal.cling.binding.xml.ServiceDescriptorBinder;
import org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl;
import org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl;
import org.teleal.cling.transport.impl.apache.StreamClientConfigurationImpl;
import org.teleal.cling.transport.impl.apache.StreamClientImpl;
import org.teleal.cling.transport.impl.apache.StreamServerConfigurationImpl;
import org.teleal.cling.transport.impl.apache.StreamServerImpl;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.cling.transport.spi.StreamClient;
import org.teleal.cling.transport.spi.StreamServer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Configuration settings for deployment on Android.
 * @author Logan Miller
 */
public class AndroidUpnpServiceConfiguration extends DefaultUpnpServiceConfiguration {

    final private static Logger log = Logger.getLogger(AndroidUpnpServiceConfiguration.class.getName());

    final protected Object manager;

    public AndroidUpnpServiceConfiguration(Object manager) {
    	this(manager, 0); // Ephemeral port
    }

    public AndroidUpnpServiceConfiguration(Object manager, int streamListenPort) {
        super(streamListenPort, false);

        this.manager = manager;

        // This should be the default on Android 2.1 but it's not set by default
        System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
    }

    @Override
    protected NetworkAddressFactory createNetworkAddressFactory(int streamListenPort) {
    	 return new AndroidNetworkAddressFactory(manager);
    }

    @Override
    public StreamServer createStreamServer(NetworkAddressFactory networkAddressFactory) {
        return new StreamServerImpl(
                new StreamServerConfigurationImpl(
                        networkAddressFactory.getStreamListenPort()
                )
        );
    }

    @Override
    public StreamClient createStreamClient() {
        return new StreamClientImpl(new StreamClientConfigurationImpl() {
        	public int getConnectionTimeoutSeconds() {
                return 2;
            }
        	public int getDataReadTimeoutSeconds() {
                return 3;
            }
        	public boolean getStaleCheckingEnabled() {
        		// comment from AndroidHttpClient.java:
        		//
                // Turn off stale checking.  Our connections break all the time anyway,
                // and it's not worth it to pay the penalty of checking every time.
        		return false;
        	}
        	public int getRequestRetryCount() {
        		// since "connections break all the time anyway", limit number of retries to
        		// minimize time spent in HttpClient.execute()
        		return 1;
        	}
        });
    }

    @Override
    protected DeviceDescriptorBinder createDeviceDescriptorBinderUDA10() {
        return new UDA10DeviceDescriptorBinderSAXImpl();
    }

    @Override
    protected ServiceDescriptorBinder createServiceDescriptorBinderUDA10() {
        return new UDA10ServiceDescriptorBinderSAXImpl();
    }

    @Override
    public int getRegistryMaintenanceIntervalMillis() {
        return 3000; // Preserve battery on Android, only run every 3 seconds
    }

    @Override
    protected Executor createDefaultExecutor() {

        // Smaller pool and larger queue on Android, devices do not have much resources...
        ThreadPoolExecutor defaultExecutor = new ThreadPoolExecutor(8, 16, 5, TimeUnit.SECONDS, new ArrayBlockingQueue(512)) {
            @Override
            protected void beforeExecute(Thread thread, Runnable runnable) {
                super.beforeExecute(thread, runnable);
                thread.setName("Thread " + thread.getId() + " (Active: " + getActiveCount() + ")");
            }
        };

        defaultExecutor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.DiscardPolicy() {
                    @Override
                    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

                        // Log and discard
                        log.warning(
                                "Thread pool saturated, discarding execution " +
                                "of '"+runnable.getClass()+"', consider raising the " +
                                "maximum pool or queue size"
                        );
                        super.rejectedExecution(runnable, threadPoolExecutor);
                    }
                }
        );

        return defaultExecutor;
    }

}
