package ws.logv.trainmonitor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SearchViewCompat;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.gcm.GCMRegistrar;
import ws.logv.trainmonitor.app.*;

import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends SherlockFragmentActivity implements com.actionbarsherlock.app.ActionBar.OnNavigationListener {


    private MenuItem mMenueSearch;
    private MenuItem mMenueRefresh;
    private IRefreshable mCurrentView;
    private ISearchable mSearchable;
    private static final String LOG_TAG = "MainActivity";

    @Override
    protected synchronized  void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
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
        final ReentrantLock lock = new ReentrantLock();

        if(!Installation.wasDisclaimerShown(this))
        {

            AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.disclaimer_header)
                .setMessage(R.string.disclaimer)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Installation.setDisclaimerShown(that);
                        that.init();
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
                GCMRegistrar.checkDevice(that);
                GCMRegistrar.checkManifest(that);
                final String regId = GCMRegistrar.getRegistrationId(that);
                if (regId.equals("")) {
                    GCMRegistrar.register(that, Constants.GCM.SENDER_ID);
                }else
                {
                    new DeviceManager(that).registeredToGCM(regId);
                    new SyncManager(that).syncSubscribtions();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        Fragment frag = null;

        if(i == 0)
        {
             frag = AllTrainsActivity.AllTrainsFragment.newInstance(i);

             if(mMenueRefresh != null)
                 mMenueRefresh.setVisible(true);

             if(mMenueSearch != null)
                 mMenueSearch.setVisible(true);

        } else if (i == 1) {
            frag = MyTrainsActivity.MyTrainsFragment.newInstance(i);


            if(mMenueRefresh != null)
                mMenueRefresh.setVisible(true);

            if(mMenueSearch != null)
                mMenueSearch.setVisible(false);
        }

        if(frag != null)
        {
            mCurrentView = (IRefreshable)frag;

            if(frag instanceof ISearchable)
                mSearchable = (ISearchable)frag;
            else
                mSearchable = null;


            FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.details, frag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            return true;
        }
        return false;
    }

    public void performSearch(String query)
    {
        if(mSearchable != null)
            mSearchable.query(query);
    }

    public void searchClosed()
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MainActivity that = this;

        SearchView searchView = new SearchView(this);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                that.performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if("".equals(newText))
                    mSearchable.searchClosed();
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
                        if(mCurrentView != null)
                            mCurrentView.refresh();
                        return true;
                    }
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

}
