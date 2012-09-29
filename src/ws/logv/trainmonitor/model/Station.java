package ws.logv.trainmonitor.model;

import com.j256.ormlite.field.DatabaseField;

public class Station {
	@DatabaseField
	private double latitude;
	@DatabaseField
	private double longitude;
	@DatabaseField
	private String name;
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
    public void setId(int id)
    {
        this.id = id;
    }
	@DatabaseField(id = true)
	private int id;
}
