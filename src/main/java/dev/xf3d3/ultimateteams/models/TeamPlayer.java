package dev.xf3d3.ultimateteams.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class TeamPlayer {

    private String javaUUID;
    private String lastPlayerName;
    private boolean isBedrockPlayer;
    private String bedrockUUID;
    private boolean canChatSpy;

    public TeamPlayer(@NotNull String UUID, @NotNull String playerName, @Nullable Boolean isBedrock, @Nullable String bedrockUuid, @Nullable Boolean canSpy) {
        javaUUID = UUID;
        lastPlayerName = playerName;
        isBedrockPlayer = isBedrock != null ? isBedrock : false;
        bedrockUUID = bedrockUuid;
        canChatSpy = canSpy != null ? canSpy : false;
    }

    public String getJavaUUID() {
        return javaUUID;
    }

    public void setJavaUUID(String javaUUID) {
        this.javaUUID = javaUUID;
    }

    public String getLastPlayerName() {
        return lastPlayerName;
    }

    public void setLastPlayerName(String lastPlayerName) {
        this.lastPlayerName = lastPlayerName;
    }


    public boolean getCanChatSpy() {
        return canChatSpy;
    }

    public void setCanChatSpy(boolean canChatSpy) {
        this.canChatSpy = canChatSpy;
    }

    public boolean isBedrockPlayer() {
        return isBedrockPlayer;
    }

    public void setBedrockPlayer(boolean bedrockPlayer) {
        isBedrockPlayer = bedrockPlayer;
    }

    public String getBedrockUUID() {
        return bedrockUUID;
    }

    public void setBedrockUUID(String bedrockUUID) {
        this.bedrockUUID = bedrockUUID;
    }
}
