package dev.xf3d3.ultimateteams.team;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Home {

    @Expose
    private Position position;
    @Nullable
    @Expose
    private String server;
    @Expose
    @SerializedName("public")
    private boolean isPublic = false;

    private Home(@NotNull Position position, @Nullable String server) {
        this.position = position;
        this.server = server;
    }

    /**
     * Create a town spawn from a position and server
     *
     * @param position the {@link Position} of the spawn
     * @param server   the ID of the server the spawn is on
     * @return the spawn
     */
    @NotNull
    public static Home of(@NotNull Position position, @Nullable String server) {
        return new Home(position, server);
    }

    @SuppressWarnings("unused")
    private Home() {
    }

    /**
     * Get the position of the spawn
     *
     * @return the position of the spawn
     */
    @NotNull
    public Position getPosition() {
        return position;
    }

    /**
     * Get the ID of the server the spawn is on
     *
     * @return the ID of the server the spawn is on
     */
    @Nullable
    public String getServer() {
        return server;
    }

    /**
     * Check if the spawn is public
     *
     * @return {@code true} if the spawn is public, {@code false} otherwise
     */
    public boolean isPublic() {
        return isPublic;
    }

    /**
     * Set the spawn privacy
     *
     * @param isPublic {@code true} if the spawn is public, {@code false} otherwise
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public String toString() {
        return getPosition() + " (" + getServer() + ")";
    }
}
