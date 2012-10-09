package ws.logv.trainmonitor;

import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.Fragment;
import ws.logv.trainmonitor.app.IRefreshable;

public class MainActivity extends SherlockFragmentActivity implements com.actionbarsherlock.app.ActionBar.OnNavigationListener {


    private MenuItem mMenueSearch;
    private MenuItem mMenueRefresh;
    private IRefreshable mCurrentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.navigation, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);

       /* GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            GCMRegistrar.register(this, Constants.GCM.SENDER_ID);
        }     */


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

        //Used to put dark icons on light action bar
       mMenueSearch =  menu.add("Search");
       mMenueSearch.setIcon(R.drawable.ic_search)
                .setActionView(R.layout.collapsible_edittext)

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
