package ws.logv.trainmonitor.model;

import java.net.Authenticator;
import java.util.Date;
import java.util.UUID;

import android.text.format.Time;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

public class Subscribtion {
	@DatabaseField(id = true)
	private UUID id;
	@DatabaseField
    @SerializedName("train_id")
	private String train;
    @SerializedName("notification_start")
	@DatabaseField
	private Date notificationStart;
    @SerializedName("notification_end")
	@DatabaseField
	private Date notificationEnd;
    @DatabaseField
    @SerializedName("device")
	private UUID device;
    @DatabaseField
    @SerializedName("last_change")
    private Date lastChange;
	public String getTrain() {
		return train;
	}
	public void setTrain(String train) {
		this.train = train;
	}
	public Date getNotificationStart() {
		return notificationStart;
	}
	public void setNotificationStart(Date notificationStart) {
		this.notificationStart = notificationStart;
	}
	public Date getNotificationEnd() {
		return notificationEnd;
	}
	public void setNotificationEnd(Date notificationEnd) {
		this.notificationEnd= notificationEnd;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}

    private Subscribtion()
    {

    }
	public static Subscribtion createNew(Device device)
	{
		Subscribtion ret = new Subscribtion();
		
		ret.id = UUID.randomUUID();
		ret.device = device.getId();
        Time now = new Time();
        now.setToNow();
        ret.lastChange = new Date();
        ret.lastChange.setTime(now.toMillis(false));
        ret.notificationStart = new Date();
        ret.notificationEnd = new Date(24 * 60 * 60 * 1000 - 1);
        return ret;
	}
	public UUID getDevice() {
		return device;
	}
	public void setDevice(UUID device) {
		this.device = device;
	}
	
}
