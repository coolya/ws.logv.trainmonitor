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

import android.content.Context;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.app.manager.BackendManager;
import ws.logv.trainmonitor.app.manager.DeviceManager;
import ws.logv.trainmonitor.data.legacy.StationRepository;
import ws.logv.trainmonitor.data.legacy.TrainRepository;
import ws.logv.trainmonitor.debug.FatalEventListener;
import ws.logv.trainmonitor.event.WorkflowWiredUpEvent;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 26.12.12
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
public class Workflow {
    private static  boolean wiredup;
    private static final Object lock = new Object();
    private static EventBus mBus = EventBus.getDefault();

    public static EventBus getEventBus(Context context)
    {
        wireUp(context);
        return  mBus;
    }

    private static void wireUp(Context context)
    {
        //sets up all the workflow that is need to get data and sync to the cloud
        //the UI has to register and unregister itself this is just for business logic
        if(!wiredup)
        {
            synchronized (lock)
            {
                if(!wiredup)
                {
                    mBus.register(new TrainRepository(context));
                    mBus.register(new BackendManager(context));
                    mBus.register(new StationRepository(context));
                    mBus.register(new DeviceManager(context));
                    if(BuildConfig.DEBUG)
                    {
                        mBus.register(new FatalEventListener());
                    }
                    wiredup = true;
                    mBus.postSticky(new WorkflowWiredUpEvent());
                }
            }
        }
    }
}
