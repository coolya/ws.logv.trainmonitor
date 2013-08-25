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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import ws.logv.trainmonitor.data.Tables;

public class Train {
	@DatabaseField
	@SerializedName("train_nr")
	private String trainId;
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField(index = true)
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

	public static Train fromCursor(Cursor cursor) {
		Train ret = new Train();

		try {
			ret.setFinished(DateFormat.getInstance().parse(cursor.getString(Tables.Train.ColumnIds.FINISHED)));
			ret.setNextrun(DateFormat.getInstance().parse(cursor.getString(Tables.Train.ColumnIds.NEXT_RUN)));
			ret.setStarted(DateFormat.getInstance().parse(cursor.getString(Tables.Train.ColumnIds.STARTED)));
			ret.setTrainId(cursor.getString(Tables.Train.ColumnIds.TRAIN_ID));
			ret.id = cursor.getInt(Tables.Train.ColumnIds.ID);
		} catch (ParseException e) {
			return null;
		}

		return ret;
	}

	public static ContentValues toContent(Train train) {
		ContentValues ret = new ContentValues();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


		ret.put(Tables.Train.ColumnNames.FINISHED, format.format(train.getFinished()));
		ret.put(Tables.Train.ColumnNames.NEXT_RUN, format.format(train.getNextrun()));
		ret.put(Tables.Train.ColumnNames.STARTED, format.format(train.getStarted()));
		//ret.put(Tables.Train.ColumnNames.STATUS, train.getStatus().toString() );
		ret.put(Tables.Train.ColumnNames.TRAIN_ID, train.getTrainId());

		return ret;
	}
}
