package ws.logv.trainmonitor.app;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.api.IApiCallback;
import ws.logv.trainmonitor.data.Action;
import ws.logv.trainmonitor.data.StationRepository;
import ws.logv.trainmonitor.data.TrainRepository;
import ws.logv.trainmonitor.model.Train;

import java.math.MathContext;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 04.10.12
 * Time: 06:54
 * To change this template use File | Settings | File Templates.
 */
public class SyncManager {
    private final Context mCtx;
    public SyncManager(Context ctx)
    {
         mCtx = ctx;
    }
    public void syncTrains(final Action<String> progress, final Action<Integer> complete) {
        ApiClient api = new ApiClient(mCtx);
        api.getTrains(new IApiCallback<Collection<Train>>() {

            public void onError(Throwable tr) {
                Log.e(this.getClass().getName(), "Error getting trains ", tr);
                Toast toast = Toast.makeText(mCtx, R.string.error_getting_trains, Toast.LENGTH_LONG);
                toast.show();
                complete.exec(0);
            }

            public void onNoConnection() {
                Toast toast = Toast.makeText(mCtx, R.string.error_no_connection, Toast.LENGTH_LONG);
                toast.show();
                complete.exec(0);
            }

            public void onComplete(final Collection<Train> data) {
                progress.exec(mCtx.getString(R.string.refresh_trains_2));
                final Integer count = data.size();
                TrainRepository.deleteTrains(mCtx, new Action<Boolean>() {
                    @Override
                    public void exec(Boolean param) {
                        TrainRepository.saveTrains(mCtx, data, new Action<Boolean>() {
                                    @Override
                                    public void exec(Boolean param) {
                                        complete.exec(count);
                                    }
                                }, new Action<Integer>() {
                                    @Override
                                    public void exec(final Integer param) {
                                         progress.exec(mCtx.getString(R.string.refresh_trains_3, param, count));
                                    }
                                }
                        );
                    }
                });
            }
        });
    }

    public Boolean trainsNeedSync()
    {
        return !TrainRepository.hasTrains(mCtx);
    }

    public Boolean stationsNeedSync()
    {
        return !StationRepository.haveStations(mCtx);
    }
}
