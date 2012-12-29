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

package ws.logv.trainmonitor.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.*;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.command.fetch.FetchTrainDetailsCommand;
import ws.logv.trainmonitor.command.fetch.FetchTrainDetailsResult;
import ws.logv.trainmonitor.data.TrainRepository;
import ws.logv.trainmonitor.event.FavTrainEvent;
import ws.logv.trainmonitor.event.NoConnectionEvent;
import ws.logv.trainmonitor.model.StationInfo;
import ws.logv.trainmonitor.ui.adapter.TrainDetailAdapter;

import java.util.ArrayList;

public class Train extends Activity {
    private ProgressDialog mDialog;
    private String mCurrentTrain;
    private EventBus mBus = Workflow.getEventBus(this);
    private TrainDetailAdapter mAdapter;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        Intent intent = this.getIntent();

        if (intent != null) {
            handleIntent(intent);
        }
    }

    @Override
    protected void onStop() {
        if (mAdapter != null)
            mAdapter.unRegister();

        super.onStop();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(FetchTrainDetailsResult event) {
        mBus.unregister(this, FetchTrainDetailsResult.class);
        mBus.unregister(this, NoConnectionEvent.class);
        if (!event.isFaulted()) {
            setListView(event.getTrain());
            hideDialog();
        } else {
            Log.e(this.getClass().getName(), "Error getting train details ", event.getException());
            hideDialog();
            Toast toast = Toast.makeText(this.getApplicationContext(), R.string.train_details_error, Toast.LENGTH_LONG);
            toast.show();
            this.finish();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEvent(NoConnectionEvent event) {
        mBus.unregister(this, NoConnectionEvent.class);
        hideDialog();
        Toast toast = Toast.makeText(getApplicationContext(), R.string.error_no_connection, Toast.LENGTH_LONG);
        toast.show();
        this.finish();
    }

    private void handleIntent(Intent intent) {
        mCurrentTrain = intent.getStringExtra(Constants.IntentsExtra.Train);
        mBus.register(this, FetchTrainDetailsResult.class);
        mBus.register(this, NoConnectionEvent.class);
        mBus.post(new FetchTrainDetailsCommand(mCurrentTrain));
        TextView tv = (TextView) findViewById(R.id.train_id);
        tv.setText(mCurrentTrain);

        CheckBox cbFav = (CheckBox) findViewById(R.id.fav);

        cbFav.setChecked(TrainRepository.isTrainFav(this, mCurrentTrain));
        cbFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mBus.post(new FavTrainEvent(mCurrentTrain, b));
            }
        });

        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.progess_getting_train_details));
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void setListView(ws.logv.trainmonitor.model.Train data) {
        mAdapter = new TrainDetailAdapter(this, R.id.listView_stations, new ArrayList<StationInfo>(data.getStations()));
        ListView lv = (ListView) findViewById(R.id.listView_stations);
        lv.setAdapter(mAdapter);
        TextView tv = (TextView) findViewById(R.id.next_refresh);
        tv.setText(DateFormat.getTimeFormat(this).format(data.getNextrun()) + " " + DateFormat.getMediumDateFormat(this).format(data.getNextrun()));
    }

    private void hideDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

}
