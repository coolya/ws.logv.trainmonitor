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

package ws.logv.trainmonitor.data;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.command.load.LoadStationCommand;
import ws.logv.trainmonitor.command.load.LoadStationResult;
import ws.logv.trainmonitor.event.StationSyncCompleteEvent;
import ws.logv.trainmonitor.event.StationSyncEvent;
import ws.logv.trainmonitor.model.Station;

import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 9/29/12
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class StationRepository {

    private Context mContext;
    private Queue<LoadStationCommand> pendingResponses = new LinkedList<LoadStationCommand>();
    private boolean syncForeced;
    private Object lock = new Object();

    public StationRepository(Context context)
    {
        mContext = context;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventAsync(LoadStationCommand event)
    {
        try {
            Station station = loadStation(mContext, event.getId());
            if(station == null)
            {
                synchronized (lock)
                {
                    if(!syncForeced)
                    {
                        station = loadStation(mContext, event.getId());
                        if(station == null)
                        {
                            syncForeced = true;
                            Workflow.getEventBus(mContext).register(this, "onSyncComplete", StationSyncCompleteEvent.class);
                            Workflow.getEventBus(mContext).post(new StationSyncEvent());
                        }
                        else
                        {
                            Workflow.getEventBus(mContext).post(new LoadStationResult(station, event.getTag()));
                        }
                    }
                    else
                    {
                        pendingResponses.add(event);
                    }
                }
            }
            else
            {
                Workflow.getEventBus(mContext).post(new LoadStationResult(station, event.getTag()));
            }
        } catch (SQLException e) {
            Workflow.getEventBus(mContext).post(new LoadStationResult(e, event.getTag()));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onSyncComplete(StationSyncCompleteEvent event)
    {
        Workflow.getEventBus(mContext).unregister(this, StationSyncCompleteEvent.class);
        List<LoadStationCommand> commands = new ArrayList<LoadStationCommand>();
        synchronized (lock)
        {
            syncForeced = false;
            while (pendingResponses.isEmpty())
            {
                commands.add(pendingResponses.poll());
            }
        }

        EventBus bus = Workflow.getEventBus(mContext);
        for (LoadStationCommand command : commands)
        {
            try {
                Station station = loadStation(mContext, command.getId());
                bus.post(new LoadStationResult(station, command.getTag()));
            } catch (SQLException e) {
               bus.post(new LoadStationResult(e, command.getTag()));
            }
        }

    }

    private static Station loadStation(Context context, int id) throws SQLException {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            Dao<Station, Integer> dao = databaseHelper.getStationDao();
            return dao.queryForId(id);
        }
        finally
        {
            OpenHelperManager.releaseHelper();
        }
    }

    public  static Task<Station> loadStation(final int id, Context ctx, final Action<Station> callback)
    {
        Task<Station> task =
        new Task<Station>(new Func<Station, Context>(){

            public Station exec(Context param) {
                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
                try {
                    Dao<Station, Integer> dao = databaseHelper.getStationDao();
                    return dao.queryForId(id);
                } catch (SQLException e) {
                    return null;
                }
                finally
                {
                    OpenHelperManager.releaseHelper();
                    databaseHelper = null;
                }
            }},
                new Action<Station>(){
                    public void exec(Station param) {
                        if(callback != null)
                            callback.exec(param);
                    }});
        task.execute(ctx);
        return task;
    }

    public  static Boolean haveStations(Context ctx)
    {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
        try {
            Dao<Station, Integer> dao = databaseHelper.getStationDao();
            return dao.countOf() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    public static void saveStations(Context ctx, final Collection<Station> data, final Action<Boolean> callback)
    {
        new Task<Boolean>(new Func<Boolean, Context>(){

            public Boolean exec(Context param) {
                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
                try {
                    Dao<Station, Integer> dao = databaseHelper.getStationDao();
                    for(Station station : data)
                    {
                        dao.createOrUpdate(station);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
                finally
                {
                    OpenHelperManager.releaseHelper();
                    databaseHelper = null;
                }
            }},
                new Action<Boolean>(){

                    public void exec(Boolean param) {
                        if(callback != null)
                            callback.exec(param);

                    }}).execute(ctx);
    }

    public static void saveStations(Context context, Collection<Station> stations) throws SQLException {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            Dao<Station, Integer> dao = databaseHelper.getStationDao();
            for(Station station : stations)
            {
                dao.createOrUpdate(station);
            }
        }
        finally
        {
            OpenHelperManager.releaseHelper();
        }
    }


}
