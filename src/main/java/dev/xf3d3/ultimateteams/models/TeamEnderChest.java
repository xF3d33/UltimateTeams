package dev.xf3d3.ultimateteams.models;

import com.google.gson.annotations.Expose;
import lombok.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.util.Base64;

/**
 * Represents a team's ender chest with serializable inventory contents
 */
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamEnderChest {
    
    @Expose
    @Getter @Setter
    private int chestNumber; // 1, 2, 3, etc.
    
    @Expose
    @Getter @Setter
    private int rows; // Number of rows (1-6), where 6 rows = double chest
    
    @Expose
    @Getter @Setter
    @Nullable
    private String serializedContents; // Base64 encoded inventory contents
    
    /**
     * Get the size of the chest in slots
     * @return number of slots (rows * 9)
     */
    public int getSize() {
        return rows * 9;
    }

    /**
     * Serialize inventory contents to Base64 string (YAML Format)
     * @param contents The ItemStack array to serialize
     * @return Base64 encoded string
     */
    @NotNull
    public static String serializeContents(@NotNull ItemStack[] contents) {
        try {

            byte[] bytes = ItemStack.serializeItemsAsBytes(contents);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {

            System.err.println("UltimateTeams: Error while serializing contents of a team echest");
            return "";
        }
    }

    /**
     * Deserialize Base64 string to ItemStack array
     * @param data The Base64 encoded string
     * @param size The expected size of the inventory
     * @return ItemStack array or empty array if deserialization fails
     */
    @NotNull
    public static ItemStack[] deserializeContents(@Nullable String data, int size) {
        if (data == null || data.isEmpty()) {
            return new ItemStack[size];
        }

        // Try NEW Format
        try {
            byte[] bytes = Base64.getDecoder().decode(data);

            return ItemStack.deserializeItemsFromBytes(bytes);
        } catch (Exception ignored) {
            // proceed to legacy fallback
        }

        // Fallback to LEGACY Format (BukkitObjectInputStream)
        // This is required to read existing 1.21.4 data
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            int length = dataInput.readInt();
            ItemStack[] contents = new ItemStack[length];

            for (int i = 0; i < length; i++) {
                contents[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();

            return contents;
        } catch (Exception e) {

            // log when 1.21.5 tries to read 1.21.4 data
            System.err.println("UltimateTeams: Failed to deserialize legacy ender chest. If you are on 1.21.5, this data may be unreadable without downgrading. Contact the developer for help.");
            return new ItemStack[size];
        }
    }
    
    /**
     * Get the deserialized contents of this chest
     * @return ItemStack array
     */
    @NotNull
    public ItemStack[] getContents() {
        return deserializeContents(serializedContents, getSize());
    }
    
    /**
     * Set the contents of this chest
     * @param contents ItemStack array to store
     */
    public void setContents(@NotNull ItemStack[] contents) {
        this.serializedContents = serializeContents(contents);
    }
}
