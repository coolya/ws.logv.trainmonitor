package ws.logv.trainmonintor.app;

import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.data.DatabaseTask;
import ws.logv.trainmonitor.data.TrainRepository;
import ws.logv.trainmonitor.model.FavouriteTrain;
import ws.logv.trainmonitor.model.Train;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TrainAdapter extends BaseArrayAdapter<Train>{
    private HashMap<String, Boolean> favs = new HashMap<String, Boolean>();

	private Context ctx;
	public TrainAdapter(Context context, int textViewResourceId, List<Train> objects) {
		super(context, textViewResourceId, objects);
		this.ctx = context;

        refreshFavs(context);
    }

    public void refreshFavs(Context context) {
        DatabaseTask<Collection<FavouriteTrain>> task = TrainRepository.loadFavouriteTrains(context, null);
        try {
            Collection<FavouriteTrain> trains = task.get();
            favs.clear();

            for(FavouriteTrain train : trains)
            {
                favs.put(train.getTrainId(), true);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.all_trains_item, parent, false);
        final Train item = this.getItem(position);

        TextView tvId = (TextView) rowView.findViewById(R.id.train_id);
        tvId.setText(item.getTrainId());
        CheckBox cbFav = (CheckBox) rowView.findViewById(R.id.fav);

        cbFav.setChecked(favs.containsKey(item.getTrainId()));
        cbFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                  TrainRepository.favTrain(ctx, item, null);
                else
                    TrainRepository.unFavTrain(ctx, item, null);
            }
        });
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ws.logv.trainmonitor.Train.class);
                intent.putExtra(Constants.IntentsExtra.Train, item.getTrainId());
                view.getContext().startActivity(intent);
            }
        });
        return rowView;
	}
	
	
	
	

}
