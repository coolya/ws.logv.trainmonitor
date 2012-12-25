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
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.api.IApiCallback;
import ws.logv.trainmonitor.data.DatabaseTask;
import ws.logv.trainmonitor.data.StationRepository;
import ws.logv.trainmonitor.model.FavouriteTrain;
import ws.logv.trainmonitor.model.Station;
import ws.logv.trainmonitor.model.StationInfo;
import ws.logv.trainmonitor.model.Train;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 9/29/12
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class FavouriteTrainAdapter extends BaseArrayAdapter<FavouriteTrain> {
    private Context mCtx;

    public FavouriteTrainAdapter(Context context, int textViewResourceId, List<FavouriteTrain> objects) {
        super(context, textViewResourceId, objects);
        mCtx = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mCtx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.my_trains_item, parent, false);
        final FavouriteTrain item = this.getItem(position);

        final TextView tvNextStation = (TextView) rowView.findViewById(R.id.next_station);
        final TextView tvDelay = (TextView) rowView.findViewById(R.id.delay);
        final TextView tvArrival = (TextView) rowView.findViewById(R.id.arrival);
        final ProgressBar pgb = (ProgressBar) rowView.findViewById(R.id.waiter);

        pgb.setVisibility(View.VISIBLE);
        tvNextStation.setVisibility(View.INVISIBLE);
        tvDelay.setVisibility(View.INVISIBLE);
        tvArrival.setVisibility(View.INVISIBLE);

        ApiClient apiClient = new ApiClient(mCtx);

        apiClient.getTrainDetail(item.getTrainId(), new IApiCallback<Train>() {
            @Override
            public void onComplete(Train data) {
                Time time = new Time();
                time.setToNow();
                time.normalize(false);
                int current = time.hour * 60 + time.minute;
                int i  = 0;
                Collection<StationInfo> stations =  data.getStations();
                for(StationInfo info : stations)
                {
                    int minutes = info.getArrival();
                    if(minutes == 0)
                        minutes = info.getDeparture();

                    if((minutes * 60 * 1000) < current)
                    {
                        i++;
                    }
                }

                StationInfo[] stationinfos = stations.toArray(new StationInfo [] {});

                if(i >= stationinfos.length)
                    i = stationinfos.length -1;

                StationInfo nextStation = stationinfos[i];

                DatabaseTask<Station> task = StationRepository.loadStation(nextStation.getStationId(), mCtx, null);
                tvDelay.setText(String.valueOf(nextStation.getDelay()));

                int seconds = nextStation.getArrival();
                if(seconds == 0)
                    seconds = nextStation.getDeparture();

                time.set(seconds * 60 * 1000);
                time.normalize(false);
                time.hour = time.hour -1;
                String arrival = time.format("%H:%M");
                tvArrival.setText(arrival);

                pgb.setVisibility(View.INVISIBLE);

                tvNextStation.setVisibility(View.VISIBLE);
                tvDelay.setVisibility(View.VISIBLE);
                tvArrival.setVisibility(View.VISIBLE);

                try {
                    Station next = task.get();
                    tvNextStation.setText(next.getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (ExecutionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            @Override
            public void onError(Throwable tr) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onNoConnection() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        TextView tvTrainId = (TextView) rowView.findViewById(R.id.name);
        tvTrainId.setText(item.getTrainId());
        return rowView;

    }
}
