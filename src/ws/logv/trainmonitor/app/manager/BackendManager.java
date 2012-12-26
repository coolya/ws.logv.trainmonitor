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

package ws.logv.trainmonitor.app.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.api.IApiCallback;
import ws.logv.trainmonitor.event.*;
import ws.logv.trainmonitor.command.fetch.FetchTrainDetailsCommand;
import ws.logv.trainmonitor.command.fetch.FetchTrainDetailsResult;
import ws.logv.trainmonitor.data.*;
import ws.logv.trainmonitor.model.Device;
import ws.logv.trainmonitor.model.Station;
import ws.logv.trainmonitor.model.Subscribtion;
import ws.logv.trainmonitor.model.Train;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 04.10.12
 * Time: 06:54
 * To change this template use File | Settings | File Templates.
 */
public class BackendManager {
    private final Context mCtx;
    private static final String LOG_TAG = "BackendManager";

    public BackendManager(Context ctx)
    {
        mCtx = ctx;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventAsync(TrainSyncEvent event)
    {
        ApiClient apiClient = new ApiClient(mCtx);
        EventBus bus = Workflow.getEventBus(mCtx);
        Collection<Train> trains = apiClient.getTrains();
        try {
            bus.post(new TrainSyncProgressEvent(mCtx.getString(R.string.refresh_trains_2)));
            TrainRepository.deleteTrains(mCtx);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error deleting trains", e);
        }

        int count = trains.size();
        int i = 1;
        for (Train train : trains)
        {
            try {
                TrainRepository.saveTrain(mCtx, train);
                bus.post(new TrainSyncProgressEvent(mCtx.getString(R.string.refresh_trains_3, i, count)));
                i++;
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        bus.post(new TrainSyncCompleteEvent());
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventAsync(StationSyncEvent event)
    {
        EventBus bus = Workflow.getEventBus(mCtx);
        ApiClient apiClient = new ApiClient(mCtx);
        List<Station> stations = apiClient.getStations();
        try {
            StationRepository.saveStations(mCtx, stations);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        bus.post(new StationSyncCompleteEvent());
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventAsync(FetchTrainDetailsCommand event)
    {
        EventBus bus = Workflow.getEventBus(mCtx);
        ApiClient apiClient = new ApiClient(mCtx);
        Train train = apiClient.getTrainDetail(event.getTrain());
        bus.post(new FetchTrainDetailsResult(train, event.getTag()));
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

    public void pushSubscriptions()
    {
        DeviceManager devMng = new DeviceManager(mCtx);
        Device dev = devMng.getsDevice();

        if(dev == null || dev.getGcmRegId() == null ||dev.getGcmRegId().isEmpty())
            return;
        try {

            ApiClient client = new ApiClient(mCtx);
            Task<List<Subscribtion>> task = SubscribtionRepository.loadSubscribtions(mCtx, null);
            ArrayList<Subscribtion> subscription = new ArrayList<Subscribtion>(task.get());
            final Context ctx = mCtx;
            client.postSubscribtion(subscription, new IApiCallback<Collection<Subscribtion>>() {
                @Override
                public void onComplete(Collection<Subscribtion> data) {
                    SubscribtionRepository.saveSubscribtions(ctx, data, null);
                }

                @Override
                public void onError(Throwable tr) {
                    Log.e(LOG_TAG, "Failed to post Subscribtions", tr);
                }

                @Override
                public void onNoConnection() {
                    Log.i(LOG_TAG, "No Internet connection for sync, retrying on next start");
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void pullSubscriptions()
    {
        DeviceManager devMng = new DeviceManager(mCtx);
        Device dev = devMng.getsDevice();

        if(dev == null || dev.getGcmRegId() == null ||dev.getGcmRegId().isEmpty())
            return;

        ApiClient client = new ApiClient(mCtx);
        final Context ctx = mCtx;
        client.getSubscriptions(new IApiCallback<Collection<Subscribtion>>() {
            @Override
            public void onComplete(Collection<Subscribtion> data) {
                try {
                    SubscribtionRepository.clearSubscribtions(ctx, null).get();
                    SubscribtionRepository.saveSubscribtions(ctx, data, null);
                    TrainRepository.notifyFav();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (ExecutionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            @Override
            public void onError(Throwable tr) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onNoConnection() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }
}
