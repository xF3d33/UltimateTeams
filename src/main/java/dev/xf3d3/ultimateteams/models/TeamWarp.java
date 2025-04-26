package dev.xf3d3.ultimateteams.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class TeamWarp {
    @Expose
    @Getter @Setter
    private String name;

    @Expose
    @Nullable
    @Getter @Setter
    private String server;

    @Expose
    @SerializedName("world")
    @Getter @Setter
    private String WarpWorld;

    @Expose
    @SerializedName("x")
    @Getter @Setter
    private double WarpX;

    @Expose
    @SerializedName("y")
    @Getter @Setter
    private double WarpY;

    @Expose
    @SerializedName("z")
    @Getter @Setter
    private double WarpZ;

    @Expose
    @SerializedName("yaw")
    @Getter @Setter
    private float WarpYaw;

    @Expose
    @SerializedName("pitch")
    @Getter @Setter
    private float WarpPitch;

    private TeamWarp(@NotNull String name, @NotNull Location location, @Nullable String server) {
        this.name = name;
        this.server = server;

        this.WarpWorld = location.getWorld().getName();
        this.WarpX = location.getX();
        this.WarpY = location.getY();
        this.WarpZ = location.getZ();
        this.WarpYaw = location.getYaw();
        this.WarpPitch = location.getPitch();
    }

    public static TeamWarp of(@NotNull String name, @NotNull Location location, @Nullable String server) {
        return new TeamWarp(name, location, server);
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
