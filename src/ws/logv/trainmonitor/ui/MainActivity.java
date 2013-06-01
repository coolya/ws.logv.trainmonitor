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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import com.korovyansk.android.slideout.SlideoutActivity;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.app.Installation;
import ws.logv.trainmonitor.event.AccountChoosnEvent;
import ws.logv.trainmonitor.event.DisclaimerAcceptedEvent;
import ws.logv.trainmonitor.event.ui.*;
import ws.logv.trainmonitor.ui.contract.NavigationTarget;
import ws.logv.trainmonitor.ui.contract.OnRefreshRequestStateHandler;
import ws.logv.trainmonitor.ui.fragments.ChooseAccountFragment;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends Activity {

    private MenuItem mMenueSearch;
    private MenuItem mMenueRefresh;
    private Fragment mPendingFragment;
    private static final String LOG_TAG = "MainActivity";

    private EventBus mBus = Workflow.getEventBus(this);
    private boolean isPaused = false;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (intent != null) {
            NotificationManager notificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (intent.getBooleanExtra(Constants.IntentsExtra.NOTIFICATION, false)) {

                notificationManager.cancelAll();
            } else if (Constants.Actions.SHOWW_ALL_TRAINS_FRAGMENT.equals(intent.getAction())) {
                notificationManager.cancel(Constants.Notification.PROGRESS);
                mBus.post(new NavigateToEvent(NavigationTarget.ALL_TRAINS));
            }
        }

    }

    @Override
    protected synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        try {
            File httpCacheDir = new File(this.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
        }

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        WindowMediator.setOnRefreshStateRequestHandler(new OnRefreshRequestStateHandler() {
            @Override
            public void onRefreshStart() {
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            public void onRefreshEnd() {
                setProgressBarIndeterminateVisibility(false);
            }
        });

        setContentView(R.layout.activity_main);
        mBus.registerSticky(this);
        mBus.postSticky(new ShowDisclaimerEvent());
        Intent intent = this.getIntent();
        handleIntent(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mPendingFragment != null) {
            doFragmentTransition();
        }
    }

    private void doFragmentTransition() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.details, mPendingFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        mPendingFragment = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBus.unregister(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(SetUpActionBarEvent event) {
        if (mMenueSearch != null)
            mMenueSearch.setVisible(event.isSearchEnabled());

        if (mMenueRefresh != null)
            mMenueRefresh.setVisible(event.isRefreshEnabled());

        if (event.getDropDownItems() != null && event.getNavigationListener() != null) {
            Context context = getActionBar().getThemedContext();

            ArrayAdapter<String> list = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item,
                    Arrays.asList(event.getDropDownItems()));
            list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            getActionBar().setListNavigationCallbacks(list, event.getNavigationListener());
            getActionBar().setSelectedNavigationItem(event.getSelectedItem());
        } else {
            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(ShowDisclaimerEvent event) {
        mBus.removeStickyEvent(ShowDisclaimerEvent.class);
        if (!Installation.wasDisclaimerShown(this)) {
            final MainActivity that = this;
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.disclaimer_header)
                    .setMessage(R.string.disclaimer)
                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Installation.setDisclaimerShown(that);
                            mBus.post(new DisclaimerAcceptedEvent());
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            that.finish();
                        }
                    }).create();
            dialog.show();
        } else {
            mBus.post(new DisclaimerAcceptedEvent());
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEvent(DisclaimerAcceptedEvent event) {
        if (!Installation.wasChooseAccountShown(this)) {
            final MainActivity that = this;
            new ChooseAccountFragment(new Runnable() {
                @Override
                public void run() {
                    Installation.setChooseAccountShown(that);
                    mBus.post(new AccountChoosnEvent());
                }
            }).show(getFragmentManager(), "choose_account");
        } else {
            mBus.post(new AccountChoosnEvent());
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(AccountChoosnEvent event) {
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mBus.post(new NavigateToEvent(NavigationTarget.MY_TRAINS));
    }

    @Override
    protected void onPause() {
        isPaused = true;
        super.onPause();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(NavigateToEvent event) {

        switch (event.getTarget()) {
            case ALL_TRAINS:
                mPendingFragment = AllTrainsActivity.AllTrainsFragment.newInstance();
                break;
            case MY_TRAINS:
                mPendingFragment = MyTrainsActivity.MyTrainsFragment.newInstance();
                break;
            case MAP:
                break;
            case ADD_TRAIN:
                break;
            case SETTINGS:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                this.startActivity(settingsIntent);
                break;
        }

        if (!isPaused)
            doFragmentTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    this.getResources().getInteger(R.integer.visible_width_content_dp),
                    getResources().getDisplayMetrics());
            SlideoutActivity.prepare(MainActivity.this, android.R.id.content, width);
            startActivity(new Intent(MainActivity.this, MenuActivity.class));
            overridePendingTransition(0, 0);
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MainActivity that = this;

        SearchView searchView = new SearchView(this);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mBus.post(new SearchEvent(query));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if ("".equals(newText))
                    mBus.post(new SearchEvent(true));
                return true;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });


        //Used to put dark icons on light action bar
        mMenueSearch = menu.add("Search");
        mMenueSearch.setIcon(R.drawable.ic_search)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        mMenueRefresh = menu.add("refresh");
        mMenueRefresh.setIcon(R.drawable.ic_refresh)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        mBus.post(new RefreshEvent());
                        return true;
                    }
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(getString(R.string.Settings)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent settingsIntent = new Intent(that, SettingsActivity.class);
                that.startActivity(settingsIntent);
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
    }


}
