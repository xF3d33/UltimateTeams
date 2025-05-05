package dev.xf3d3.ultimateteams.hooks;

import org.geysermc.floodgate.api.FloodgateApi;

public class FloodgateHook {

    private final FloodgateApi floodgateApi;

    public FloodgateHook() {
        this.floodgateApi = FloodgateApi.getInstance();
    }

    public FloodgateApi getHook() {
        return this.floodgateApi;
    }

}