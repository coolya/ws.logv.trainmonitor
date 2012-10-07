package ws.logv.trainmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.app.FavouriteTrainAdapter;
import ws.logv.trainmonitor.data.Action;
import ws.logv.trainmonitor.data.DatabaseTask;
import ws.logv.trainmonitor.data.TrainRepository;
import ws.logv.trainmonitor.model.FavouriteTrain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 9/29/12
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyTrainsActivity extends FragmentActivity {

    public static class MyTrainsFragment extends Fragment
    {
        static MyTrainsFragment newInstance(int num) {
            MyTrainsFragment f = new MyTrainsFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View v = inflater.inflate(R.layout.activity_my_trains, container, false);
            DatabaseTask<Collection<FavouriteTrain>> task =  TrainRepository.loadFavouriteTrains(v.getContext(), null);
            ListView lvTrains = (ListView) v.findViewById(R.id.listView_trains);

            try {
                Collection<FavouriteTrain> trains = task.get();
                LinkedList<FavouriteTrain> list = new LinkedList<FavouriteTrain>();
                list.addAll(trains);
                final FavouriteTrainAdapter adapter = new FavouriteTrainAdapter(v.getContext(), 0, list);
                lvTrains.setAdapter(adapter);

                lvTrains.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        FavouriteTrain item = adapter.getItem(i);
                        Intent intent = new Intent(view.getContext(), ws.logv.trainmonitor.Train.class);
                        intent.putExtra(Constants.IntentsExtra.Train, item.getTrainId());
                        view.getContext().startActivity(intent);
                    }
                });

                TrainRepository.setFavChangedListener(new FavChangedListener() {
                    @Override
                    public void onFavChanged() {
                        DatabaseTask<Collection<FavouriteTrain>> task =  TrainRepository.loadFavouriteTrains(v.getContext(), new Action<Collection<FavouriteTrain>>() {
                            @Override
                            public void exec(Collection<FavouriteTrain> param) {
                                adapter.clear();
                                adapter.addAll(param);
                            }
                        });
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ExecutionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return v;
        }
    }

}
