/*
 * Copyright 2012. Kolja Dummann <k.dummann@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
	@DatabaseField(uniqueIndex = true)
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
        ret.notificationStart = new Date(1);
        ret.notificationEnd = new Date(24 * 59 * 60 * 1000 - 1);
        return ret;
	}
	public UUID getDevice() {
		return device;
	}
	public void setDevice(UUID device) {
		this.device = device;
	}
	
}
