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
import android.os.PowerManager;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.command.delete.DeleteSubscriptionCommand;
import ws.logv.trainmonitor.command.delete.DeleteSubscriptionResult;
import ws.logv.trainmonitor.command.fetch.FetchTrainDetailsCommand;
import ws.logv.trainmonitor.command.fetch.FetchTrainDetailsResult;
import ws.logv.trainmonitor.data.StationRepository;
import ws.logv.trainmonitor.data.TrainRepository;
import ws.logv.trainmonitor.event.*;
import ws.logv.trainmonitor.model.Station;
import ws.logv.trainmonitor.model.Subscribtion;
import ws.logv.trainmonitor.model.Train;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

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

    public BackendManager(Context ctx) {
        mCtx = ctx;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventAsync(TrainSyncEvent event) {
        PowerManager pm = (PowerManager) mCtx.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "Trainsync lock");
        wakeLock.acquire();
        try {

            ApiClient apiClient = new ApiClient(mCtx);
            if (!apiClient.isConnected()) {
                Workflow.getEventBus(mCtx).post(new NoConnectionEvent());
                return;
            }
            EventBus bus = Workflow.getEventBus(mCtx);
            Collection<Train> trains = apiClient.getTrains();
            try {
                bus.post(new TrainSyncProgressEvent(mCtx.getString(R.string.refresh_trains_2), 0, 0));
                TrainRepository.deleteTrains(mCtx);
            } catch (SQLException e) {
                Log.e(LOG_TAG, "Error deleting trains", e);
            }

            int count = trains.size();
            int i = 1;
            for (Train train : trains) {
                try {
                    TrainRepository.saveTrain(mCtx, train);
                    bus.post(new TrainSyncProgressEvent(mCtx.getString(R.string.refresh_trains_3, i, count), count, i));
                    i++;
                } catch (SQLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            bus.postSticky(new TrainSyncCompleteEvent());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error syncing trains!", e);
            Workflow.getEventBus(mCtx).post(new FatalErrorEvent(e, R.string.error_getting_trains));
        } finally {
            wakeLock.release();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventAsync(StationSyncEvent event) {
        PowerManager pm = (PowerManager) mCtx.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "Stationsync lock");
        wakeLock.acquire();
        try {
            EventBus bus = Workflow.getEventBus(mCtx);
            ApiClient apiClient = new ApiClient(mCtx);
            if (!apiClient.isConnected()) {
                Workflow.getEventBus(mCtx).post(new NoConnectionEvent());
                return;
            }
            List<Station> stations = apiClient.getStations();
            try {
                StationRepository.saveStations(mCtx, stations);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            bus.post(new StationSyncCompleteEvent());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error syncing stations!", e);
            Workflow.getEventBus(mCtx).post(new FatalErrorEvent(e, R.string.error_getting_stations));
        } finally {
            wakeLock.release();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventAsync(DeleteSubscriptionCommand event) {
        try {
            ApiClient client = new ApiClient(mCtx);
            if (!client.isConnected()) {
                Workflow.getEventBus(mCtx).post(new NoConnectionEvent());
                return;
            }
            if (!client.isConnected()) {
                Workflow.getEventBus(mCtx).post(new NoConnectionEvent());
                return;
            }
            client.deleteSubscription(event.getSubscribtion());
            Workflow.getEventBus(mCtx).post(new DeleteSubscriptionResult());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error deleting subscription", e);
            Workflow.getEventBus(mCtx).post(new DeleteSubscriptionResult(e));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventAsync(FetchTrainDetailsCommand event) {
        try {
            EventBus bus = Workflow.getEventBus(mCtx);
            ApiClient apiClient = new ApiClient(mCtx);
            if (!apiClient.isConnected()) {
                Workflow.getEventBus(mCtx).post(new NoConnectionEvent());
                return;
            }
            Train train = apiClient.getTrainDetail(event.getTrain());
            bus.post(new FetchTrainDetailsResult(train, event.getTag()));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error fetching traindetails", e);
            Workflow.getEventBus(mCtx).post(new FatalErrorEvent(e, R.string.train_details_error));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventBackgroundThread(DeviceRegisteredEvent event) {
        try {
            GCMRegistrar.checkDevice(mCtx);
            GCMRegistrar.checkManifest(mCtx);
            final String regId = GCMRegistrar.getRegistrationId(mCtx);
            if (regId.equals("")) {
                GCMRegistrar.register(mCtx, Constants.GCM.SENDER_ID);
            } else {
                Workflow.getEventBus(mCtx).post(new RegisteredToGcmEvent());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "GCM not available", e);
            Workflow.getEventBus(mCtx).post(new FatalErrorEvent(e));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventBackgroundThread(DeviceUpdatedEvent event) {
        Workflow.getEventBus(mCtx).post(new PushSubscriptionsEvent());
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventBackgroundThread(PushSubscriptionsEvent event) {
        ApiClient client = new ApiClient(mCtx);
        if (!client.isConnected()) {
            Workflow.getEventBus(mCtx).post(new NoConnectionEvent());
            return;
        }
        try {
            List<Subscribtion> subscriptions = TrainRepository.loadSubscriptions(mCtx);
            subscriptions = client.postSubscriptions(subscriptions);
            TrainRepository.clearSubscriptions(mCtx);
            TrainRepository.saveSubscriptions(mCtx, subscriptions);
            Workflow.getEventBus(mCtx).post(new FavouriteTrainsChangedEvent());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error pushing subscriptions", e);
            Workflow.getEventBus(mCtx).post(new FatalErrorEvent(e));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventBackgroundThread(PullSubscriptionsEvent event) {
        ApiClient client = new ApiClient(mCtx);
        if (!client.isConnected()) {
            Workflow.getEventBus(mCtx).post(new NoConnectionEvent());
            return;
        }

        try {
            List<Subscribtion> subscriptions = client.pullSubscriptions();
            TrainRepository.clearSubscriptions(mCtx);
            TrainRepository.saveSubscriptions(mCtx, subscriptions);
            Workflow.getEventBus(mCtx).post(new FavouriteTrainsChangedEvent());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error pulling subscriptions", e);
            Workflow.getEventBus(mCtx).post(new FatalErrorEvent(e));
        }

    }

    public Boolean trainsNeedSync() {
        return !TrainRepository.hasTrains(mCtx);
    }

}
