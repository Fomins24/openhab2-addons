/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.internal.discovery;

import java.util.*;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.*;
import org.openhab.binding.broadlink.BroadlinkBindingConstants;
import org.openhab.binding.broadlink.handler.BroadlinkControllerHandler;
import org.openhab.binding.broadlink.handler.ControllerStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Broadlink discovery implementation.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public class BroadlinkDeviceDiscoveryService extends AbstractDiscoveryService
        implements ControllerStatusListener {

    private static final int DISCOVERY_TIMEOUT_SEC = 10;
    private final BroadlinkControllerHandler controller;
    private final Logger logger = LoggerFactory.getLogger(BroadlinkDeviceDiscoveryService.class);
    public static final Set SUPPORTED_THING_TYPES_UIDS;

    static {
        SUPPORTED_THING_TYPES_UIDS = Collections.singleton(BroadlinkBindingConstants.THING_TYPE_S1C);
    }

    public BroadlinkDeviceDiscoveryService(BroadlinkControllerHandler controller) {
        super(SUPPORTED_THING_TYPES_UIDS, 10, true);
        logger.info("BroadlinkDeviceDiscoveryService constructed");
        this.controller = controller;
        this.controller.addControllerStatusListener(this);
    }

    protected void startScan() {
        logger.info("BroadlinkDeviceDiscoveryService scan started");
        discoverDevices();
    }

    protected void startBackgroundDiscovery() {
        discoverDevices();
    }

    public void controllerStatusChanged(ThingStatus status) {
        if (status.equals(ThingStatus.ONLINE)) {
            discoverDevices();
        }

    }

    protected void deactivate() {
        super.deactivate();
        controller.removeControllerStatusListener(this);
    }

    private void discoverDevices() {
        String serial = null;
        String name = null;
        String type = null;
        ThingUID controllerUID = controller.getThing().getUID();
        ThingUID thingUID = new ThingUID(type, controllerUID, serial);
        Map properties = new HashMap(2);
        properties.put("serial", serial);
        properties.put("name", name);
        org.eclipse.smarthome.config.discovery.DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties).withBridge(controllerUID).withLabel(name).build();
        thingDiscovered(discoveryResult);
    }
}
