/*
 * Copyright 2013. Kolja Dummann <k.dummann@gmail.com>
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

package ws.logv.trainmonitor.app.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.event.TrainSyncCompleteEvent;
import ws.logv.trainmonitor.event.TrainSyncEvent;
import ws.logv.trainmonitor.event.TrainSyncProgressEvent;
import ws.logv.trainmonitor.ui.MainActivity;

/**
 * Created with IntelliJ IDEA.
 * User: kolja
 * Date: 23.02.13
 * Time: 22:05
 * To change this template use File | Settings | File Templates.
 */
public class BackendService extends IntentService {

    private EventBus mBus = EventBus.getDefault();
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public BackendService() {
        super("BackendService");
    }

    @Override
    public void onCreate() {
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getString(R.string.train_sync))
                .setContentText(getString(R.string.train_sync_starting))
                .setSmallIcon(R.drawable.notification);
        mBus.register(this);
        super.onCreate();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(TrainSyncProgressEvent event) {
        mBuilder.setContentText(event.getMessage()).setProgress(event.getTotalCount(), event.getCurrentCount(), false);
        mNotifyManager.notify(Constants.Notification.PROGRESS, mBuilder.build());
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(TrainSyncCompleteEvent event) {
        stopForeground(false);
        mBuilder.setProgress(0, 0, false);
        mBuilder.setContentText(getString(R.string.train_sync_completed));
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.Actions.SHOWW_ALL_TRAINS_FRAGMENT);
        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mBuilder.setContentIntent(pi);
        mNotifyManager.notify(Constants.Notification.PROGRESS, mBuilder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String command = intent.getStringExtra(Constants.IntentsExtra.COMMAND);

            if (Constants.Commands.SYNC_TRAINS.equals(command)) {
                mBuilder.setProgress(0, 0, true);
                startForeground(Constants.Notification.PROGRESS, mBuilder.build());
                mBus.post(new TrainSyncEvent());
            }
        }
    }

    public static Intent getTrainSyncIntent(Context context) {
        Intent intent = new Intent(context, BackendService.class);
        intent.putExtra(Constants.IntentsExtra.COMMAND, Constants.Commands.SYNC_TRAINS);
        return intent;
    }

}
