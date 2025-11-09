package dev.xf3d3.ultimateteams.models;

import com.google.gson.annotations.Expose;
import lombok.*;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a backup of a team's ender chest
 */
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class TeamEnderChestBackup {
    
    @Expose
    private int teamId;
    
    @Expose
    private int chestNumber;
    
    @Expose
    private long timestamp; // When the backup was created
    
    @Expose
    private String serializedContents; // Base64 encoded inventory contents
    
    @Expose
    private int rows; // Number of rows at backup time
    
    /**
     * Create a backup from a TeamEnderChest
     * @param teamId The team ID
     * @param chest The chest to backup
     * @return A new backup instance
     */
    @NotNull
    public static TeamEnderChestBackup fromChest(int teamId, @NotNull TeamEnderChest chest) {
        return TeamEnderChestBackup.builder()
                .teamId(teamId)
                .chestNumber(chest.getChestNumber())
                .timestamp(System.currentTimeMillis())
                .serializedContents(chest.getSerializedContents())
                .rows(chest.getRows())
                .build();
    }
    
    /**
     * Get a formatted timestamp string
     * @return Formatted date/time string
     */
    @NotNull
    public String getFormattedTimestamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new java.util.Date(timestamp));
    }
    
    /**
     * Get how long ago this backup was created
     * @return Human-readable time ago string
     */
    @NotNull
    public String getTimeAgo() {
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / (60 * 1000);
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else {
            return "just now";
        }
    }
}
