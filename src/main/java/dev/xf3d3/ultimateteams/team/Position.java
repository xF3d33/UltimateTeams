package dev.xf3d3.ultimateteams.team;


import com.google.gson.annotations.Expose;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a position in a claim world
 */
@SuppressWarnings("unused")
public class Position {

    @Expose
    private double x;
    @Expose
    private double y;
    @Expose
    private double z;
    @Expose
    private World world;
    @Expose
    private float yaw;
    @Expose
    private float pitch;

    protected Position(double x, double y, double z, @NotNull World world, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @SuppressWarnings("unused")
    private Position() {
    }

    @NotNull
    public static Position at(double x, double y, double z, @NotNull World world, float yaw, float pitch) {
        return new Position(x, y, z, world, yaw, pitch);
    }

    @NotNull
    public static Position at(double x, double y, double z, @NotNull World world) {
        return new Position(x, y, z, world, 0, 0);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @NotNull
    public World getWorld() {
        return world;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public double distanceBetween(@NotNull Position other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2));
    }

    public Location getLocation() {
        return new Location(
                world,
                x,
                y,
                z,
                yaw,
                pitch
        );
    }
}
