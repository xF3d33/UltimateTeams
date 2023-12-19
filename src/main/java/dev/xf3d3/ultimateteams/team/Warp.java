package dev.xf3d3.ultimateteams.team;

import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class Warp {
    @Expose
    private String name;
    @Expose
    private Position position;
    @Expose
    private String server;

    public Warp(@NotNull String name, @NotNull String server, @NotNull String world, @NotNull Double x, @NotNull Double y, @NotNull Double z, @NotNull Float yaw, @NotNull Float pitch) {
        this.name = name;
        this.position = Position.at(
                x,
                y,
                z,
                Objects.requireNonNull(Bukkit.getWorld(world)),
                yaw,
                pitch
        );
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Position getPosition() {
        return this.position;
    }
}
