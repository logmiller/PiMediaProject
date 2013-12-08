package org.teleal.cling.android;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.registry.Registry;

/**
 * Interface of the Android UPnP application service component.
 *
 * @author Logan Miller
 */
public interface AndroidUpnpService {

    /**
     * @return The actual main instance and interface of the UPnP service.
     */
    public UpnpService get();

    /**
     * @return The configuration of the UPnP service.
     */
    public UpnpServiceConfiguration getConfiguration();

    /**
     * @return The registry of the UPnP service.
     */
    public Registry getRegistry();

    /**
     * @return The client API of the UPnP service.
     */
    public ControlPoint getControlPoint();

}