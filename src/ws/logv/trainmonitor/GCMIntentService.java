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

package ws.logv.trainmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.app.manager.DeviceManager;
import ws.logv.trainmonitor.command.fetch.FetchTrainDetailsCommand;
import ws.logv.trainmonitor.command.fetch.FetchTrainDetailsResult;
import ws.logv.trainmonitor.event.PullSubscriptionsEvent;
import ws.logv.trainmonitor.event.PushSubscriptionsEvent;
import ws.logv.trainmonitor.event.RegisteredToGcmEvent;
import ws.logv.trainmonitor.model.StationInfo;
import ws.logv.trainmonitor.ui.MainActivity;
import ws.logv.trainmonitor.ui.Train;

import java.util.Collection;

public class GCMIntentService extends GCMBaseIntentService {

    private final String TAG = "GCMIntentService";
    private PowerManager.WakeLock wl;

    public GCMIntentService() {
        super(Constants.GCM.SENDER_ID);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEvent(FetchTrainDetailsResult result) {
        Workflow.getEventBus(this).unregister(this, FetchTrainDetailsResult.class);
        Collection<StationInfo> stations = result.getTrain().getStations();
        int delay = 0;
        for (StationInfo station : stations) {
            if (station.getDelay() > delay)
                delay = station.getDelay();

        }

        if (delay > 0) {
            generateTrainLateNotification(this, result.getTrain().getTrainId(), delay);
        }
        wl.release();
    }

    @Override
    protected void onMessage(final Context context, Intent intent) {

        if (intent.hasExtra("command")) {
            String command = intent.getStringExtra("command");

            if ("sync".equals(command)) {
                String type = intent.getStringExtra("type");

                if ("subscription".equals(type)) {
                    Workflow.getEventBus(context).post(new PullSubscriptionsEvent());
                }
            }

        } else {
            String train = intent.getStringExtra("train");
            if (wl == null) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Refresh trains");
            }

            wl.acquire(2000);
            EventBus bus = Workflow.getEventBus(context);
            bus.register(this, FetchTrainDetailsResult.class);
            bus.post(new FetchTrainDetailsCommand(train));
        }
    }

    @Override
    protected void onError(Context context, String error) {
        Log.e(TAG, "Got GCM error: " + error);
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        Log.i(TAG, "Registered to GCM");
        Workflow.getEventBus(context).post(new RegisteredToGcmEvent());
    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        Log.i(TAG, "Unregistered from GCM");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            DeviceManager mng = new DeviceManager(context);
            mng.unregisteredFromGCM(regId);
            Workflow.getEventBus(context).post(new PushSubscriptionsEvent());
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    public static void generateTrainLateNotification(Context context, String trainId, int delay) {

        SharedPreferences pref = context.getSharedPreferences(Constants.Settings.PERF, 0);

        if (!pref.getBoolean(Constants.Settings.NOTIFICATION_ON, true))
            return;

        int icon = R.drawable.notification;
        long when = System.currentTimeMillis();
        String message = context.getString(R.string.notification_message, trainId);
        String subText = context.getString(R.string.notification_detail, String.valueOf(delay));

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra(Constants.IntentsExtra.NOTIFICATION, true);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Intent iShow = new Intent(context, Train.class);
        iShow.setAction(Constants.Actions.TRAIN_ACTION + trainId);
        PendingIntent piShow = PendingIntent.getActivity(context, 0, iShow, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentText(message)
                .setContentTitle(title)
                .setSmallIcon(icon)
                .setWhen(when)
                .setAutoCancel(true)
                .setSubText(subText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .addAction(R.drawable.notification_trains, context.getString(R.string.show), piShow)
                .setContentIntent(intent);

        builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS);

        Notification notification = builder.build();

        notificationManager.notify(trainId.hashCode(), notification);
    }
}
