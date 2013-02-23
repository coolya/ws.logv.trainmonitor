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
import android.widget.ProgressBar;
import android.widget.TextView;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.command.fetch.FetchTrainDetailsCommand;
import ws.logv.trainmonitor.command.fetch.FetchTrainDetailsResult;
import ws.logv.trainmonitor.command.load.LoadStationCommand;
import ws.logv.trainmonitor.command.load.LoadStationResult;
import ws.logv.trainmonitor.model.FavouriteTrain;
import ws.logv.trainmonitor.model.StationInfo;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 9/29/12
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class FavouriteTrainAdapter extends BaseArrayAdapter<FavouriteTrain> {
    private Context mCtx;
    private EventBus mBus;

    public FavouriteTrainAdapter(Context context, List<FavouriteTrain> objects) {
        super(context, 0, objects);
        mCtx = context;
        mBus = Workflow.getEventBus(mCtx);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(FetchTrainDetailsResult event) {
        if (!event.isFaulted()) {
            if (event.getTag() instanceof View) {
                View rowView = (View) event.getTag();

                TextView tvNextStation = (TextView) rowView.findViewById(R.id.next_station);
                TextView tvDelay = (TextView) rowView.findViewById(R.id.delay);
                TextView tvArrival = (TextView) rowView.findViewById(R.id.arrival);
                ProgressBar pgb = (ProgressBar) rowView.findViewById(R.id.waiter);

                Time time = new Time();
                time.setToNow();
                time.normalize(false);
                int current = time.hour * 60 + time.minute;
                int i = 0;
                Collection<StationInfo> stations = event.getTrain().getStations();
                for (StationInfo info : stations) {
                    int minutes = info.getArrival();
                    if (minutes == 0)
                        minutes = info.getDeparture();

                    if ((minutes * 60 * 1000) < current) {
                        i++;
                    }
                }

                StationInfo[] stationinfos = stations.toArray(new StationInfo[]{});

                if (i >= stationinfos.length)
                    i = stationinfos.length - 1;

                StationInfo nextStation = stationinfos[i];

                mBus.post(new LoadStationCommand(nextStation.getStationId(), tvNextStation));

                tvDelay.setText(String.valueOf(nextStation.getDelay()));

                int seconds = nextStation.getArrival();
                if (seconds == 0)
                    seconds = nextStation.getDeparture();

                time.set(seconds * 60 * 1000);
                time.normalize(false);
                time.hour = time.hour - 1;
                String arrival = time.format("%H:%M");
                tvArrival.setText(arrival);

                pgb.setVisibility(View.INVISIBLE);
                tvDelay.setVisibility(View.VISIBLE);
                tvArrival.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(LoadStationResult event) {
        if (!event.isFaulted()) {
            if (event.getTag() instanceof TextView) {
                TextView view = (TextView) event.getTag();
                view.setText(event.getResult().getName());
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mCtx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_my_trains, parent, false);
        FavouriteTrain item = this.getItem(position);

        TextView tvNextStation = (TextView) rowView.findViewById(R.id.next_station);
        TextView tvDelay = (TextView) rowView.findViewById(R.id.delay);
        TextView tvArrival = (TextView) rowView.findViewById(R.id.arrival);
        ProgressBar pgb = (ProgressBar) rowView.findViewById(R.id.waiter);

        pgb.setVisibility(View.VISIBLE);
        tvNextStation.setVisibility(View.INVISIBLE);
        tvDelay.setVisibility(View.INVISIBLE);
        tvArrival.setVisibility(View.INVISIBLE);

        TextView tvTrainId = (TextView) rowView.findViewById(R.id.name);
        tvTrainId.setText(item.getTrainId());

        mBus.post(new FetchTrainDetailsCommand(item.getTrainId(), rowView));
        return rowView;
    }

    public void register() {
        mBus.register(this);
    }

    public void unRegister() {
        mBus.unregister(this);
    }

}
