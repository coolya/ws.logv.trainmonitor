package ws.logv.trainmonitor.model;

import java.util.Collection;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

public class Train {
	@DatabaseField
	@SerializedName("train_nr")
	private String trainId;
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private TrainStatus status;
	public String getTrainId() {
		return trainId;
	}
	public void setTrainId(String trainId) {
		this.trainId = trainId;
	}
	public TrainStatus getStatus() {
		return status;
	}
	public void setStatus(TrainStatus status) {
		this.status = status;
	}
	public Collection<StationInfo> getStations() {
		return stations;
	}
	public void setStations(Collection<StationInfo> stations) {
		this.stations = stations;
	}
	public Date getStarted() {
		return started;
	}
	public void setStarted(Date started) {
		this.started = started;
	}
	public Date getNextrun() {
		return nextrun;
	}
	public void setNextrun(Date nextrun) {
		this.nextrun = nextrun;
	}
	public Date getFinished() {
		return finished;
	}
	public void setFinished(Date finished) {
		this.finished = finished;
	}
	private Collection<StationInfo> stations;
	@DatabaseField
	private Date started;
	@DatabaseField
	private Date nextrun;
	@DatabaseField
	private Date finished;
}
