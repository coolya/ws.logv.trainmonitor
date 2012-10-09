package ws.logv.trainmonitor;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.*;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.app.TrainDetailAdapter;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.api.IApiCallback;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import ws.logv.trainmonitor.data.Action;
import ws.logv.trainmonitor.data.StationRepository;
import ws.logv.trainmonitor.data.TrainRepository;
import ws.logv.trainmonitor.model.Station;
import ws.logv.trainmonitor.model.StationInfo;

import java.util.ArrayList;
import java.util.Collection;
import android.os.Handler;

public class Train extends Activity implements IApiCallback<ws.logv.trainmonitor.model.Train>{
    private ProgressDialog mDialog;
    private final Handler mHander = new Handler();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        
        Intent intent = this.getIntent();
        
        if(intent != null)
        {
        	final String trainId = intent.getStringExtra(Constants.IntentsExtra.Train);
            ApiClient client = new ApiClient(this);
            client.getTrainDetail(trainId, this);
            TextView tv = (TextView) findViewById(R.id.train_id);
            tv.setText(trainId);

            CheckBox cbFav = (CheckBox) findViewById(R.id.fav);
            final Context ctx = this;

            cbFav.setChecked(TrainRepository.isTrainFav(this, trainId));
            cbFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                        TrainRepository.favTrain(ctx, trainId, null);
                    else
                        TrainRepository.unFavTrain(ctx, trainId, null); }});

            mDialog = new ProgressDialog(this);
            mDialog.setMessage(getString(R.string.progess_getting_train_details));
            mDialog.setIndeterminate(true);
            mDialog.setCancelable(false);
            mDialog.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_train, menu);
        return true;
    }

	public void onComplete(final ws.logv.trainmonitor.model.Train train) {

        if(!StationRepository.haveStations(this))
        {
            ApiClient client = new ApiClient(this);
            final Context ctx = this;
            client.getStations(new IApiCallback<Collection<Station>>() {

                @Override
                public void onComplete(final Collection<Station> data) {
                    StationRepository.saveStations(ctx, data, new Action<Boolean>() {
                        @Override
                        public void exec(Boolean param) {
                           mHander.post(new Runnable() {
                               @Override
                               public void run() {
                                   setListView(train);
                                   hideDialog();
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
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.error_no_connection, Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
        else
        {
            setListView(train);
            hideDialog();
        }
    }

    private void setListView(ws.logv.trainmonitor.model.Train data) {
        TrainDetailAdapter adapter = new TrainDetailAdapter(this, R.id.listView_stations, new ArrayList<StationInfo>(data.getStations()));
        ListView lv = (ListView) findViewById(R.id.listView_stations);
        lv.setAdapter(adapter);
    }

    private void hideDialog() {
        if(mDialog != null)
        {
            mDialog.dismiss();
        }
    }

    public void onError(Throwable tr) {
		Log.e(this.getClass().getName(), "Error getting train details ", tr);
        hideDialog();
		Toast toast = Toast.makeText(this.getApplicationContext(),R.string.train_details_error, Toast.LENGTH_LONG);
		toast.show();
		this.finish();
	}

	public void onNoConnection() {
        hideDialog();
		Toast toast = Toast.makeText(getApplicationContext(), R.string.error_no_connection, Toast.LENGTH_LONG);
		toast.show();
		this.finish();
	}
}
