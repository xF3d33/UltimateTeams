package dev.xf3d3.ultimateteams.hooks;

import org.geysermc.floodgate.api.FloodgateApi;

public class FloodgateAPIHook {

    private final FloodgateApi floodgateApi;

    public FloodgateAPIHook() {
        this.floodgateApi = FloodgateApi.getInstance();
    }

    public FloodgateApi getHook() {
        return this.floodgateApi;
    }

}