package ws.logv.trainminitor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import ws.logv.trainmonintor.app.Constants;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.api.IApiCallback;
import ws.logv.trainmonitor.data.Action;
import ws.logv.trainmonitor.data.TrainRepository;
import ws.logv.trainmonitor.model.Train;
import android.os.Bundle;
import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AllTrainsActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trains);
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
        private Boolean trainsFromServer = false;
        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.activity_my_trains, container, false);
                                   
        	PullToRefreshListView mPullRefreshListView;
        	final ArrayAdapter<String> mAdapter;
        	LinkedList<String> mListItems;
        	final Handler handler = new Handler();
        	
        	mListItems = new LinkedList<String>();            
            mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.pull_to_refresh_listview);            
            mAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, mListItems);
            
            TrainRepository.loadTrains(v.getContext(), new Action<List<Train>>(){
				@Override
				public void exec(final List<Train> trains){
					handler.post(new Runnable() {					

						public void run() {
							refreshTrains(mAdapter, trains, v.getContext());							
						}});					
				}});
            
            mPullRefreshListView.getRefreshableView()
            mPullRefreshListView.getRefreshableView().setAdapter(mAdapter);            
            mPullRefreshListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Log.v(this.getClass().getName(), "Clicked");
					if(!trainsFromServer)
					{
						refreshDataFromServer(v, mAdapter, handler);
					}else
					{
						String trainId = mAdapter.getItem(position);
						Intent intent = new Intent(view.getContext(), ws.logv.trainminitor.Train.class);
						intent.putExtra(Constants.IntentsExtra.Train, trainId);
						view.getContext().startActivity(intent);
					}

				}            	
            });
            
            mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>(){

				public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
					refreshDataFromServer(v, mAdapter, handler);					
				}		            	
            });            
            return v;
        }        
		public void refreshDataFromServer(final View v,
				final ArrayAdapter<String> mAdapter,
				final Handler handler)
		{
			ApiClient api = new ApiClient(v.getContext());
			api.getTrains(new IApiCallback<Collection<Train>>() {

				public void onError(Throwable tr) {
					Log.e(this.getClass().getName(), "Error getting trains ", tr);
					Toast toast = Toast.makeText(v.getContext(),R.string.error_getting_trains, Toast.LENGTH_LONG);
					toast.show();							
				}

				public void onNoConnection() {
					Toast toast = Toast.makeText(v.getContext(), R.string.error_no_connection, Toast.LENGTH_LONG);
					toast.show();								
				}

				public void onComplete(final Collection<Train> data) {
					handler.post(new Runnable() {
						public void run() {									
							refreshTrains(mAdapter, data, v.getContext());							
						}});
					
					TrainRepository.saveTrains(v.getContext(), data, null);							
				}						
			});
		}
        private void refreshTrains(ArrayAdapter<String> adapter, Collection<Train> data, Context ctx)
        {
        	adapter.clear();
        	
        	if(data.size() == 0)
        	{        	
        		trainsFromServer = false;
        		adapter.add(ctx.getResources().getString(R.string.no_trains_added));
        	}
        	else
        	{
            	for(Train train : data)
            	{
            		String id = train.getTrainId();
            		if(id != null)
            			adapter.add(id);
            	}
            	trainsFromServer = true;
        	}        	
        	adapter.notifyDataSetChanged();
        }
    }
}
