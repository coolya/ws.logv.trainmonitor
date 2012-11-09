package ws.logv.trainmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.api.IApiCallback;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.app.DeviceManager;
import ws.logv.trainmonitor.app.SyncManager;
import ws.logv.trainmonitor.model.StationInfo;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 05.10.12
 * Time: 06:57
 * To change this template use File | Settings | File Templates.
 */
public class GCMIntentService extends GCMBaseIntentService {

    private final String TAG = "GCMIntentService";

    public GCMIntentService()
    {
        super(Constants.GCM.SENDER_ID);
    }
    @Override
    protected void onMessage(final Context context, Intent intent) {
        ApiClient client = new ApiClient(context);
        final String train = intent.getStringExtra("train");

        client.getTrainDetail(train, new IApiCallback<ws.logv.trainmonitor.model.Train>() {
            @Override
            public void onComplete(ws.logv.trainmonitor.model.Train data) {
                Collection<StationInfo> stations = data.getStations();
                boolean late = false;
                for (StationInfo station : stations)
                {
                    if(station.getDelay() > 0)
                    {
                        late = true;
                        continue;
                    }
                }

                if(late)
                {
                    generateNotification(context, train);
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

    @Override
    protected void onError(Context context, String error) {
        Log.e(TAG, "Got GCM error: " + error);
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        Log.i(TAG, "Registered to GCM");
        DeviceManager mng = new DeviceManager(context);
        mng.registeredToGCM(regId);
    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        Log.i(TAG, "Unregistered from GCM");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            DeviceManager mng = new DeviceManager(context);
            mng.unregisteredFromGCM(regId);
            SyncManager snycMan = new SyncManager(context);
            snycMan.syncSubscribtions();
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String trainId) {
        int icon = 0; //todo add icon for notification
        long when = System.currentTimeMillis();
        String message = context.getString(R.string.notification_mesage, trainId);

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

        Notification notification = new NotificationCompat.Builder(context)
                .setContentText(message)
                .setContentTitle(title)
                .setSmallIcon(icon)
                .setWhen(when)
                .setContentIntent(intent).build();

        notificationManager.notify(0, notification);
    }
}
