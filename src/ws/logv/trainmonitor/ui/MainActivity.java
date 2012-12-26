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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.gcm.GCMRegistrar;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.app.manager.BackendManager;
import ws.logv.trainmonitor.event.RefreshEvent;
import ws.logv.trainmonitor.event.SearchEvent;
import ws.logv.trainmonitor.event.SetUpActionBarEvent;
import ws.logv.trainmonitor.ui.contract.OnRefreshRequestStateHandler;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.app.*;
import ws.logv.trainmonitor.app.manager.DeviceManager;
import ws.logv.trainmonitor.app.manager.UserManager;
import ws.logv.trainmonitor.ui.fragments.ChooseAccountFragment;

public class MainActivity extends SherlockFragmentActivity implements com.actionbarsherlock.app.ActionBar.OnNavigationListener {

    private MenuItem mMenueSearch;
    private MenuItem mMenueRefresh;
    private static final String LOG_TAG = "MainActivity";

    private EventBus mBus = Workflow.getEventBus(this);


    @Override
    protected synchronized  void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);


        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
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

        final MainActivity that = this;

        showDisclaimer(that);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(SetUpActionBarEvent event)
    {
        if(mMenueSearch != null)
            mMenueSearch.setVisible(event.isSearchEnabled());

        if(mMenueRefresh != null)
            mMenueRefresh.setVisible(event.isRefreshEnabled());

    }

    private void showDisclaimer(final MainActivity that) {
        if(!Installation.wasDisclaimerShown(this))
        {

            AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.disclaimer_header)
                .setMessage(R.string.disclaimer)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Installation.setDisclaimerShown(that);
                        that.chooseAccount(that);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        that.finish();
                    }
                }).create();
            dialog.show();
        }
        else
        {
            chooseAccount(this);
        }
    }

    private void chooseAccount(final MainActivity that)
    {
        if(!Installation.wasChooseAccountShown(this))
        {
        new ChooseAccountFragment(new Runnable() {
            @Override
            public void run() {
                Installation.setChooseAccountShown(that);
                init();
            }
        }).show(getSupportFragmentManager(), "choose_account");
        }
        else {
            init();
        }

    }

    private void init() {
        Installation.showMotd(this);


        Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.navigation, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);

        final  MainActivity that = this;

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try{
                    String string = getSharedPreferences(Constants.Settings.PERF, 0).getString(Constants.Settings.CURRENT_ACCOUNT, "");
                    UserManager.Init(string);

                GCMRegistrar.checkDevice(that);
                GCMRegistrar.checkManifest(that);
                final String regId = GCMRegistrar.getRegistrationId(that);
                if (regId.equals("")) {
                    GCMRegistrar.register(that, Constants.GCM.SENDER_ID);
                }else
                {
                    new DeviceManager(that).registeredToGCM(regId);
                    new BackendManager(that).pushSubscriptions();
                }
            } catch (Exception e)
            {
                Log.e(LOG_TAG, "GCM not available", e);
            }
            }
        };
        //running registration on background thread
        new Thread(runnable).start();

    }


    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        Fragment frag = null;

        if(i == 0)
        {
             frag = AllTrainsActivity.AllTrainsFragment.newInstance();

        } else if (i == 1) {
            frag = MyTrainsActivity.MyTrainsFragment.newInstance();
        }

        if(frag != null)
        {

            FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.details, frag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            return true;
        }
        return false;
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
                if("".equals(newText))
                     mBus.post(new SearchEvent(true));
                return true;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });



        //Used to put dark icons on light action bar
       mMenueSearch =  menu.add("Search");
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

        menu.add("Settings").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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
