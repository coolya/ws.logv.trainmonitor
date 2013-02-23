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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragment;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.command.load.LoadFavouriteTrainsCommand;
import ws.logv.trainmonitor.command.load.LoadFavouriteTrainsResult;
import ws.logv.trainmonitor.event.FavouriteTrainsChangedEvent;
import ws.logv.trainmonitor.event.ui.RefreshEvent;
import ws.logv.trainmonitor.event.ui.SetUpActionBarEvent;
import ws.logv.trainmonitor.model.FavouriteTrain;
import ws.logv.trainmonitor.ui.adapter.FavouriteTrainAdapter;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 9/29/12
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
class MyTrainsActivity extends FragmentActivity {


    public static class MyTrainsFragment extends SherlockFragment {
        public MyTrainsFragment() {
        }

        static MyTrainsFragment newInstance() {
            MyTrainsFragment f = new MyTrainsFragment();
            return f;
        }

        private FavouriteTrainAdapter mAdapter;
        private EventBus mBus = Workflow.getEventBus(this.getActivity());

        @Override
        public void onPause() {
            super.onPause();
            mBus.unregister(this);
            mAdapter.unRegister();
        }

        @Override
        public void onResume() {
            super.onResume();
            mBus.register(this, RefreshEvent.class);
            mBus.register(this, FavouriteTrainsChangedEvent.class);
            mAdapter.register();
            mBus.post(new RefreshEvent());
        }

        @SuppressWarnings("UnusedDeclaration")
        public void onEventMainThread(LoadFavouriteTrainsResult event) {
            mBus.unregister(this, LoadFavouriteTrainsResult.class);
            if (!event.isFaulted()) {
                mAdapter.setNotifyOnChange(false);
                mAdapter.clear();
                mAdapter.addAll(event.getData());
                mAdapter.notifyDataSetChanged();
                mAdapter.setNotifyOnChange(true);
            }
        }

        @SuppressWarnings("UnusedDeclaration")
        public void onEvent(RefreshEvent event) {
            refreshMe();
        }

        @SuppressWarnings("UnusedDeclaration")
        public void onEvent(FavouriteTrainsChangedEvent event) {
            refreshMe();
        }

        private void refreshMe() {
            mBus.register(this, LoadFavouriteTrainsResult.class);
            mBus.post(new LoadFavouriteTrainsCommand());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View v = inflater.inflate(R.layout.activity_my_trains, container, false);
            ListView lvTrains = (ListView) v.findViewById(R.id.listView_trains);


            LinkedList<FavouriteTrain> list = new LinkedList<FavouriteTrain>();
            mAdapter = new FavouriteTrainAdapter(v.getContext(), list);
            lvTrains.setAdapter(mAdapter);

            lvTrains.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    FavouriteTrain item = mAdapter.getItem(i);
                    Intent intent = new Intent(view.getContext(), Train.class);
                    intent.setAction(Constants.Actions.TRAIN_ACTION + item.getTrainId());
                    view.getContext().startActivity(intent);
                }
            });
            mBus.post(new SetUpActionBarEvent(false, true));
            refreshMe();
            return v;
        }
    }

}
