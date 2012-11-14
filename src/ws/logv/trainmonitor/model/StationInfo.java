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

import com.google.gson.annotations.SerializedName;

public class StationInfo {

	private int arrival;
	private int departure;
	private Boolean scraped;
    @SerializedName("station_id")private int stationId;
	private int delay;
	private String delayCause;
	
	public int getArrival() {
		return arrival;
	}
	public void setArrival(int arrival) {
		this.arrival = arrival;
	}
	public int getDeparture() {
		return departure;
	}
	public void setDeparture(int departure) {
		this.departure = departure;
	}
	public Boolean getScraped() {
		return scraped;
	}
	public void setScraped(Boolean scraped) {
		this.scraped = scraped;
	}
	public int getStationId() {
		return stationId;
	}
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public String getDelayCause() {
		return delayCause;
	}
	public void setDelayCause(String delayCause) {
		this.delayCause = delayCause;
	}

	
}
