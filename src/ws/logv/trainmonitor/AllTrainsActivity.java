package ws.logv.trainmonitor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import ws.logv.trainmonitor.app.SyncManager;
import ws.logv.trainmonitor.app.TrainAdapter;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.api.IApiCallback;
import ws.logv.trainmonitor.data.Action;
import ws.logv.trainmonitor.data.TrainRepository;
import ws.logv.trainmonitor.model.Train;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class AllTrainsActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trains);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_my_trains, menu);
        return true;
    }
    
    public static class AllTrainsFragment extends Fragment {
        int mNum;


        static AllTrainsFragment newInstance(int num) {
        	AllTrainsFragment f = new AllTrainsFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;

        }
        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            final View v = inflater.inflate(R.layout.activity_all_trains, container, false);
        	final PullToRefreshListView mPullRefreshListView;
        	final TrainAdapter mAdapter;
        	LinkedList<Train> mListItems;
        	final Handler handler = new Handler();

        	
        	mListItems = new LinkedList<Train>();
            mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.pull_to_refresh_listview);            
            mAdapter = new TrainAdapter(v.getContext(), android.R.layout.simple_list_item_1, mListItems);

            mPullRefreshListView.getRefreshableView().setAdapter(mAdapter);
            mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
                @Override
                public void onLastItemVisible() {
                    getNextFromDb(v, mAdapter, handler);
                }
            });
            
            mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>(){
				public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
                    refreshView.onRefreshComplete();
					refreshDataFromServer(v, mAdapter, handler, refreshView);
				}		            	
            });

            SyncManager adapter = new SyncManager(v.getContext());

            if(adapter.trainsNeedSync())
            {
                refreshDataFromServer(v, mAdapter, handler, mPullRefreshListView);
            } else {
                getNextFromDb(v, mAdapter, handler);
            }

            return v;
        }

        private static void getNextFromDb(View v, final TrainAdapter mAdapter, final Handler handler) {
            WindowMediator.RequestRefreshState();
            TrainRepository.loadTrainsOrdered(v.getContext(), mAdapter.getCount(), new Action<List<Train>>() {
                @Override
                public void exec(final List<Train> trains) {
                    handler.post(new Runnable() {

                        public void run() {
                            refreshTrains(mAdapter, trains);
                            WindowMediator.EndRefreshState();
                        }
                    });
                }
            });
        }

        public static void refreshDataFromServer(final View v,
				final TrainAdapter mAdapter,
				final Handler handler, final PullToRefreshBase<ListView> refreshView)
		{
            final Context ctx = v.getContext();
            final ProgressDialog dialog = new ProgressDialog(ctx);
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.setMessage(ctx.getString(R.string.refresh_trains_1));
            dialog.show();

            SyncManager mng = new SyncManager(ctx);

            mng.syncTrains(new Action<String>() {
                @Override
                public void exec(final String param) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.setMessage(param);
                        }
                    });
                }
            }, new Action<Integer>() {
                               @Override
                               public void exec(Integer param) {
                                   dialog.dismiss();
                                   mAdapter.clear();
                                   mAdapter.refreshFavs(ctx);
                                   getNextFromDb(v, mAdapter, handler);
                               }
                           });
		}
        private static void refreshTrains(TrainAdapter adapter, Collection<Train> data)
        {
            adapter.addAll(data);
        }
    }
}
