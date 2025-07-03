package dev.xf3d3.ultimateteams.migrator.legacyModels;

import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class LegacyTeamWarp {
    @Expose
    private String name;
    @Expose
    private String WarpWorld;
    @Expose
    private Double WarpX;
    @Expose
    private Double WarpY;
    @Expose
    private Double WarpZ;
    @Expose
    private Float WarpYaw;
    @Expose
    private Float WarpPitch;

    public LegacyTeamWarp(@NotNull String name, @NotNull String world, @NotNull Double x, @NotNull Double y, @NotNull Double z, @NotNull Float yaw, @NotNull Float pitch) {
        this.name = name;
        this.WarpWorld = world;
        this.WarpX = x;
        this.WarpY = y;
        this.WarpZ = z;
        this.WarpYaw = yaw;
        this.WarpPitch = pitch;
    }

    public String getName() {
        return name;
    }

    public String getWarpWorld(){
        return WarpWorld;
    }

    public double getWarpX() {
        return WarpX;
    }

    public double getWarpY(){
        return WarpY;
    }

    public double getWarpZ(){
        return WarpZ;
    }

    public float getWarpYaw(){
        return WarpYaw;
    }

    public float getWarpPitch(){
        return WarpPitch;
    }

    public Location getLocation() {
        return new Location(
                Bukkit.getWorld(this.WarpWorld),
                this.WarpX,
                this.WarpY,
                this.WarpZ,
                this.WarpYaw,
                this.WarpPitch
        );
    }
}