package ws.logv.trainmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.app.DeviceManager;

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
    protected void onMessage(Context context, Intent intent) {

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
        String message = "";

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, Train.class);
        notificationIntent.putExtra(Constants.IntentsExtra.Train, trainId);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
}
