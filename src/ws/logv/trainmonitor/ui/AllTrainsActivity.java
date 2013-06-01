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

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.app.manager.BackendManager;
import ws.logv.trainmonitor.app.services.BackendService;
import ws.logv.trainmonitor.command.load.LoadTrainCommand;
import ws.logv.trainmonitor.command.load.LoadTrainResult;
import ws.logv.trainmonitor.data.TrainType;
import ws.logv.trainmonitor.event.FatalErrorEvent;
import ws.logv.trainmonitor.event.NoConnectionEvent;
import ws.logv.trainmonitor.event.TrainSyncCompleteEvent;
import ws.logv.trainmonitor.event.ui.RefreshEvent;
import ws.logv.trainmonitor.event.ui.SearchEvent;
import ws.logv.trainmonitor.event.ui.SetUpActionBarEvent;
import ws.logv.trainmonitor.model.Train;
import ws.logv.trainmonitor.ui.adapter.TrainAdapter;

import java.util.LinkedList;

public class AllTrainsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trains);
    }


    public static class AllTrainsFragment extends Fragment implements ActionBar.OnNavigationListener {
        private static final String LOG_TAG = AllTrainsFragment.class.getSimpleName();

        static AllTrainsFragment newInstance() {
            AllTrainsFragment f = new AllTrainsFragment();
            return f;
        }

        private EventBus mBus = Workflow.getEventBus(this.getActivity());
        private TrainAdapter mAdapter;
        private PullToRefreshListView mRefreshView;
        private boolean mLoadMore = true;
        private TrainType mCurrentType = TrainType.NONE;

        @Override
        public void onPause() {
            super.onPause();
            mBus.unregister(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            mBus.registerSticky(this);
            showWaiter(isBackendServiceRunning());
        }

        @Override
        public void onDestroy() {
            mAdapter.onDestroy();
            super.onDestroy();
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_all_trains, container, false);

            LinkedList<Train> mListItems;

            mListItems = new LinkedList<Train>();
            mRefreshView = (PullToRefreshListView) v.findViewById(R.id.pull_to_refresh_listview);

            mAdapter = new TrainAdapter(v.getContext(), android.R.layout.simple_list_item_1, mListItems);
            mRefreshView.getRefreshableView().setAdapter(mAdapter);
            mRefreshView.setMode(PullToRefreshBase.Mode.DISABLED);
            mRefreshView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
                @Override
                public void onLastItemVisible() {
                    if (mLoadMore) {
                        if (mCurrentType == TrainType.All)
                            fetchData();
                        else
                            fetchData(mCurrentType);
                    }
                }
            });

            BackendManager syncAdapter = new BackendManager(getActivity());

            if (syncAdapter.trainsNeedSync()) {
                refreshDataFromServer();
            } else {
                //fetchData();
            }

            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            int selectedItem = sharedPref.getInt(Constants.Settings.SELECTED_TRAIN_TYPE, 0);

            mBus.post(new SetUpActionBarEvent(true, true, getResources().getStringArray(R.array.traintypes),
                    selectedItem, this));

            if (selectedItem == 0) {
                this.onNavigationItemSelected(0, 0);
            }
            return v;
        }

        private void fetchData() {
            WindowMediator.RequestRefreshState();
            mBus.post(new LoadTrainCommand(50l, mAdapter.getCount()));
        }

        private void fetchData(TrainType type) {
            WindowMediator.RequestRefreshState();
            mBus.post(new LoadTrainCommand(50l, mAdapter.getCount(), type));
        }

        @SuppressWarnings("UnusedDeclaration")
        public void onEventMainThread(LoadTrainResult result) {
            if (result.isFaulted()) {
                Log.e(LOG_TAG, "Error getting trains from DB", result.getException());
                Toast.makeText(getActivity(), R.string.error_reading_trains, Toast.LENGTH_LONG).show();
            } else {
                mAdapter.addAll(result.getResult());
            }
            WindowMediator.EndRefreshState();
        }

        @SuppressWarnings("UnusedDeclaration")
        public void onEventMainThread(NoConnectionEvent event) {
            showWaiter(false);


            Toast.makeText(getActivity().getApplicationContext(), R.string.error_no_connection, Toast.LENGTH_LONG).show();
        }

        @SuppressWarnings("UnusedDeclaration")
        public void onFatalError(FatalErrorEvent event) {
            mBus.unregister(this, FatalErrorEvent.class);
            showWaiter(false);
            if (event.getResId() != 0) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), event.getResId(), Toast.LENGTH_SHORT);
                toast.show();
            }

        }

        @SuppressWarnings("UnusedDeclaration")
        public void onEventMainThread(TrainSyncCompleteEvent event) {

            NotificationManager notificationManager = (NotificationManager)
                    getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Constants.Notification.PROGRESS);

            showWaiter(false);
            mAdapter.clear();
            fetchData();
            mBus.removeStickyEvent(event);
        }

        @SuppressWarnings("UnusedDeclaration")
        public void onEventMainThread(SearchEvent event) {
            WindowMediator.RequestRefreshState();
            mAdapter.clear();
            if (!event.getSearchCanceled()) {
                mLoadMore = false;
                mBus.post(new LoadTrainCommand(event.getQuery()));
            } else {
                mLoadMore = true;
                mBus.post(new LoadTrainCommand(50l, 0));
            }
        }

        @SuppressWarnings("UnusedDeclaration")
        public void onEvent(RefreshEvent event) {
            refreshDataFromServer();
        }

        public void refreshDataFromServer() {

            showWaiter(true);
            Intent intent = BackendService.getTrainSyncIntent(getActivity());
            getActivity().startService(intent);

        }

        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {

            //plus 1 because we have none in the enum but not in the GUI
            TrainType type = TrainType.values()[itemPosition + 1];

            if (type != mCurrentType) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(Constants.Settings.SELECTED_TRAIN_TYPE, itemPosition);
                editor.commit();
                mCurrentType = type;
                mAdapter.clear();
                fetchData(type);
            }

            return true;
        }

        private boolean isBackendServiceRunning() {
            ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (BackendService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }

        private void showWaiter(boolean visible) {
            View v = getView();
            if(v != null)
            {
                View progressBar = v.findViewById(R.id.waiter);
                View p2r = v.findViewById(R.id.pull_to_refresh_listview);
                if (visible) {
                    progressBar.setVisibility(View.VISIBLE);
                    p2r.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    p2r.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
