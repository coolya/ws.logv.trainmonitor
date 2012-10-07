package ws.logv.trainmonitor.app;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.data.DatabaseTask;
import ws.logv.trainmonitor.data.StationRepository;
import ws.logv.trainmonitor.model.Station;
import ws.logv.trainmonitor.model.StationInfo;

import android.os.Handler;

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
            time.normalize(true);
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
                mStationCache.put(item.getStationId(), station);
            }

            tvName.setText(station.getName());
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return rowView;
    }


}
