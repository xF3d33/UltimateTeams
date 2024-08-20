package dev.xf3d3.ultimateteams.database.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ultimateteams_teams")
public class TeamTable {
    public TeamTable() {}

    @DatabaseField(generatedId = true)
    private int id = 0;

    @DatabaseField
    private String uuid;

    @DatabaseField
    private String name;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] data;

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}