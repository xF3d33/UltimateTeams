package dev.xf3d3.ultimateteams.database.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ultimateteams_users")
public class UserTable {
    public UserTable() {}

    @DatabaseField(generatedId = true)
    private int id = 0;

    @DatabaseField
    private String uuid;

    @DatabaseField
    private String username;

    @DatabaseField
    private boolean isBedrock = false;

    @DatabaseField
    private String bedrockUUID;

    @DatabaseField
    private boolean canChatSpy = false; 

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isBedrock() {
        return isBedrock;
    }

    public void setBedrock(boolean isBedrock) {
        this.isBedrock = isBedrock;
    }

    public String getBedrockUUID() {
        return bedrockUUID;
    }

    public void setBedrockUUID(String bedrockUUID) {
        this.bedrockUUID = bedrockUUID;
    }

    public boolean canChatSpy() {
        return canChatSpy;
    }

    public void setChatSpy(boolean canChatSpy) {
        this.canChatSpy = canChatSpy;
    }
}