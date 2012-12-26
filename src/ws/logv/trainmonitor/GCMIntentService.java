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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import ws.logv.trainmonitor.ui.Train;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.api.IApiCallback;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.app.manager.DeviceManager;
import ws.logv.trainmonitor.app.manager.BackendManager;
import ws.logv.trainmonitor.model.StationInfo;

import java.util.Collection;

public class GCMIntentService extends GCMBaseIntentService {

    private final String TAG = "GCMIntentService";
    private static final int TRAIN = 1;

    public GCMIntentService()
    {
        super(Constants.GCM.SENDER_ID);
    }
    @Override
    protected void onMessage(final Context context, Intent intent) {
        ApiClient client = new ApiClient(context);

        if(intent.hasExtra("command"))
        {

            String command = intent.getStringExtra("command");

            if("sync".equals(command))
            {
                String type = intent.getStringExtra("type");

                if("subscription".equals(type))
                {
                    new BackendManager(context).pullSubscriptions();
                }
            }

        }
        else
        {
        final String train = intent.getStringExtra("train");


        client.getTrainDetail(train, new IApiCallback<ws.logv.trainmonitor.model.Train>() {
            @Override
            public void onComplete(ws.logv.trainmonitor.model.Train data) {
                Collection<StationInfo> stations = data.getStations();
                int delay = 0;
                for (StationInfo station : stations)
                {
                    if(station.getDelay() > delay)
                        delay = station.getDelay();

                }

                if(delay > 0)
                {
                    generateTrainLateNotification(context, train, delay);
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

    @Override
    protected void onError(Context context, String error) {
        Log.e(TAG, "Got GCM error: " + error);
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        Log.i(TAG, "Registered to GCM");
        DeviceManager mng = new DeviceManager(context);
        mng.registeredToGCM(regId);
        new BackendManager(context).pushSubscriptions();
    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        Log.i(TAG, "Unregistered from GCM");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            DeviceManager mng = new DeviceManager(context);
            mng.unregisteredFromGCM(regId);
            BackendManager snycMan = new BackendManager(context);
            snycMan.pushSubscriptions();
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

        if(!pref.getBoolean(Constants.Settings.NOTIFICATION_ON, true))
            return;

        int icon = R.drawable.notification;
        long when = System.currentTimeMillis();
        String message = context.getString(R.string.notification_message, trainId);
        String subText = context.getString(R.string.notification_detail, String.valueOf(delay));

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, Train.class);
        notificationIntent.putExtra(Constants.IntentsExtra.Train, trainId);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder  builder =  new NotificationCompat.Builder(context)
                .setContentText(message)
                .setContentTitle(title)
                .setSmallIcon(icon)
                .setWhen(when)
                .setAutoCancel(true)
                .setSubText(subText)
                .setContentIntent(intent);
        builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS);

        Notification notification = builder.build();

        notificationManager.notify(trainId.hashCode(), notification);
    }
}
