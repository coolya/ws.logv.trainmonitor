package ws.logv.trainmonitor.model;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Device {
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getGcmRegId() {
        return gcmRegId;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }

    @SerializedName("id")private UUID id;
    @SerializedName("provider_handle")private String gcmRegId;

    public String toString()
    {
        return id.toString() + ";" + gcmRegId;
    }

    public static Device fromString(String value)
    {
        String[] parts = value.split(";");

        Device ret = new Device();
        ret.setId(UUID.fromString(parts[0]));
        ret.setGcmRegId(parts[1]);

        return ret;
    }
}
