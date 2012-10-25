package ws.logv.trainmonitor.model;

import java.util.Date;
import java.util.UUID;

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
	
	public static Subscribtion createNew(Device device)
	{
		Subscribtion ret = new Subscribtion();
		
		ret.id = UUID.randomUUID();
		ret.device = device.getId();
		
		return ret;
	}
	public UUID getDevice() {
		return device;
	}
	public void setDevice(UUID device) {
		this.device = device;
	}
	
}
