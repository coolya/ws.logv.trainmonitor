package ws.logv.trainmonitor.model;

import java.util.Date;
import java.util.UUID;

public class Subscribtion {
	private UUID id;
	private String train;
	private Date notificationStart;
	private Date notificationEnd;
	private Device device;
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
		ret.device = device;
		
		return ret;
	}
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	
}
