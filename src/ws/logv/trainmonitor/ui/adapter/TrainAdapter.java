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

import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.command.load.LoadFavouriteTrainsCommand;
import ws.logv.trainmonitor.command.load.LoadFavouriteTrainsResult;
import ws.logv.trainmonitor.event.FavTrainEvent;
import ws.logv.trainmonitor.event.FavouriteTrainsChangedEvent;
import ws.logv.trainmonitor.model.FavouriteTrain;
import ws.logv.trainmonitor.model.Train;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

public class TrainAdapter extends BaseArrayAdapter<Train> {
    private HashMap<String, Boolean> favs = new HashMap<String, Boolean>();

    private EventBus mBus = Workflow.getEventBus(this.getContext());

	private Context ctx;
	public TrainAdapter(Context context, int textViewResourceId, List<Train> objects) {
		super(context, textViewResourceId, objects);
		this.ctx = context;
        mBus.register(this, FavouriteTrainsChangedEvent.class, LoadFavouriteTrainsResult.class);
        mBus.post(new LoadFavouriteTrainsCommand());
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEvent(FavouriteTrainsChangedEvent event)
    {
        mBus.register(this, LoadFavouriteTrainsResult.class);
        mBus.post(new LoadFavouriteTrainsCommand());
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEvent(LoadFavouriteTrainsResult event)
    {
        mBus.unregister(this, LoadFavouriteTrainsResult.class);
        if(!event.isFaulted())
        {
            synchronized (favs)
            {
                favs.clear();

                for(FavouriteTrain train : event.getData())
                {
                    favs.put(train.getTrainId(), true);
                }
                this.notifyDataSetChanged();
            }
        }
    }

    public void onDestroy()
    {
        mBus.unregister(this);
    }


    @Override
	public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        if(convertView != null)
        {
            rowView = convertView;
        }
        else {
            LayoutInflater inflater = (LayoutInflater) ctx
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.all_trains_item, parent, false);
        }
        final Train item = this.getItem(position);

        TextView tvId = (TextView) rowView.findViewById(R.id.train_id);
        tvId.setText(item.getTrainId());
        CheckBox cbFav = (CheckBox) rowView.findViewById(R.id.fav);
        cbFav.setOnCheckedChangeListener(null);
        synchronized (favs)
        {
            cbFav.setChecked(favs.containsKey(item.getTrainId()));
        }
        cbFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mBus.post(new FavTrainEvent(item.getTrainId(), b));
            }
        });
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ws.logv.trainmonitor.ui.Train.class);
                intent.putExtra(Constants.IntentsExtra.Train, item.getTrainId());
                view.getContext().startActivity(intent);
            }
        });
        return rowView;
	}


}
