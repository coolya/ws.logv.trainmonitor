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

package ws.logv.trainmonitor.app;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.api.IApiCallback;
import ws.logv.trainmonitor.data.Action;
import ws.logv.trainmonitor.data.DatabaseTask;
import ws.logv.trainmonitor.data.StationRepository;
import ws.logv.trainmonitor.model.Station;
import ws.logv.trainmonitor.model.StationInfo;

import android.os.Handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 9/29/12
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class TrainDetailAdapter extends BaseArrayAdapter<StationInfo> {

    private Context mCtx;
    private final Handler mHandler = new Handler();
    private HashMap<Integer, Station> mStationCache = new HashMap<Integer, Station>();
    LayoutInflater mInflater = null;

    public TrainDetailAdapter(Context context, int textViewResourceId, List<StationInfo> objects) {
        super(context, textViewResourceId, objects);
        mCtx = context;
        mInflater = (LayoutInflater) mCtx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StationInfo item = this.getItem(position);
        DatabaseTask<Station> task = null;
        Station station = null;

        if(!mStationCache.containsKey(item.getStationId()))
        {
            task = StationRepository.loadStation(item.getStationId(), this.getContext(), null);
        }
        else
        {
            station = mStationCache.get(item.getStationId());
        }

        View rowView = mInflater.inflate(R.layout.station_info_item, parent, false);
        TextView tvName =  (TextView) rowView.findViewById(R.id.name);
        TextView tvDelay = (TextView) rowView.findViewById(R.id.delay);
        TextView tvArrival = (TextView) rowView.findViewById(R.id.arrival);

        try {
            int delay = item.getDelay();
            tvDelay.setText(String.valueOf(delay));
            Time time = new Time();

            int seconds = item.getArrival();
            if(seconds == 0)
                seconds = item.getDeparture();

            time.set(seconds * 60 * 1000);
            time.hour = time.hour -1;

            time.normalize(false);
            String arrival = time.format("%H:%M");
            tvArrival.setText(arrival);
            if(delay == 0)
            {
                //rowView.setBackgroundColor(mCtx.getResources().getColor(R.color.OnTime));
            }
            else if(delay > 14)
            {
                rowView.setBackgroundColor(mCtx.getResources().getColor(R.color.Late));
            }
            else
            {
                rowView.setBackgroundColor(mCtx.getResources().getColor(R.color.LittleLate));
            }
            if(station == null)
            {
                station = task.get();
                if(station == null)
                {
                    final Object lock = new Object();
                    final Context ctx = this.getContext();
                    ApiClient client = new ApiClient(ctx);
                    client.getStations(new IApiCallback<Collection<Station>>() {

                        @Override
                        public void onComplete(final Collection<Station> data) {
                            StationRepository.saveStations(ctx, data, new Action<Boolean>() {
                                @Override
                                public void exec(Boolean param) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            lock.notify();
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable tr) {
                            Log.e(this.getClass().getName(), "Error getting train details ", tr);
                            Toast toast = Toast.makeText(ctx,R.string.train_details_error, Toast.LENGTH_LONG);
                            toast.show();
                        }

                        @Override
                        public void onNoConnection() {
                            Toast toast = Toast.makeText(ctx, R.string.error_no_connection, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                    lock.wait();
                    station = StationRepository.loadStation(item.getStationId(), this.getContext(), null).get();
                }

                mStationCache.put(item.getStationId(), station);
            }

            if(station != null && tvName != null)
                tvName.setText(station.getName());

        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return rowView;
    }


}
