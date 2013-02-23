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

package ws.logv.trainmonitor.ui.adapter;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.command.load.LoadStationCommand;
import ws.logv.trainmonitor.command.load.LoadStationResult;
import ws.logv.trainmonitor.model.Station;
import ws.logv.trainmonitor.model.StationInfo;

import java.util.HashMap;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 9/29/12
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class TrainDetailAdapter extends BaseArrayAdapter<StationInfo> {

    private Context mCtx;
    private HashMap<Integer, Station> mStationCache = new HashMap<Integer, Station>();
    private LayoutInflater mInflater = null;
    private EventBus mBus;

    public TrainDetailAdapter(Context context, int textViewResourceId, List<StationInfo> objects) {
        super(context, textViewResourceId, objects);
        mCtx = context;
        mInflater = (LayoutInflater) mCtx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBus = Workflow.getEventBus(context);
        mBus.register(this);
    }

    public void unRegister() {
        mBus.unregister(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(LoadStationResult event) {
        if (!event.isFaulted()) {
            if (event.getTag() instanceof TextView) {
                TextView view = (TextView) event.getTag();
                view.setText(event.getResult().getName());
                mStationCache.put(event.getResult().getId(), event.getResult());
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StationInfo item = this.getItem(position);
        View rowView = mInflater.inflate(R.layout.item_station_info, parent, false);
        TextView tvName = (TextView) rowView.findViewById(R.id.name);
        TextView tvDelay = (TextView) rowView.findViewById(R.id.delay);
        TextView tvArrival = (TextView) rowView.findViewById(R.id.arrival);

        if (mStationCache.containsKey(item.getStationId())) {
            tvName.setText(mStationCache.get(item.getStationId()).getName());
        } else {
            mBus.post(new LoadStationCommand(item.getStationId(), tvName));
        }

        int delay = item.getDelay();
        tvDelay.setText(String.valueOf(delay));
        Time time = new Time();

        int seconds = item.getArrival();
        if (seconds == 0)
            seconds = item.getDeparture();

        time.set(seconds * 60 * 1000);
        time.hour = time.hour - 1;

        time.normalize(false);
        String arrival = time.format("%H:%M");
        tvArrival.setText(arrival);
        if (delay == 0) {
            //rowView.setBackgroundColor(mCtx.getResources().getColor(R.color.OnTime));
        } else if (delay > 14) {
            rowView.setBackgroundColor(mCtx.getResources().getColor(R.color.Late));
        } else {
            rowView.setBackgroundColor(mCtx.getResources().getColor(R.color.LittleLate));
        }

        return rowView;
    }
}
