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
import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.app.manager.DeviceManager;
import ws.logv.trainmonitor.command.load.LoadFavouriteTrainsCommand;
import ws.logv.trainmonitor.command.load.LoadFavouriteTrainsResult;
import ws.logv.trainmonitor.command.load.LoadTrainCommand;
import ws.logv.trainmonitor.command.load.LoadTrainResult;
import ws.logv.trainmonitor.event.*;
import ws.logv.trainmonitor.model.FavouriteTrain;
import ws.logv.trainmonitor.model.Subscribtion;
import ws.logv.trainmonitor.model.Train;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class TrainRepository 
{
   private static final String TAG = "TrainRepository" ;
    private Context mContext;

    public TrainRepository(Context context)
    {
        mContext = context;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventBackgroundThread(LoadTrainCommand event)
    {
        if(event.getQuery() != null)
        {
             searchTrain(mContext, event.getQuery());
        }
        else
        {
            if(event.getCount() > 0)
            {
                loadTrainsOrdered(mContext, event.getStart(), event.getCount());
            }
            else
                loadTrains(mContext);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventBackgroundThread(FavTrainEvent event)
    {
        if(event.isFav())
        {
            try {
                favTrain(mContext, event.getTrain());
                Workflow.getEventBus(mContext).post(new PushSubscriptionsEvent());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                unFavTrain(mContext, event.getTrain());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventBackgroundThread(LoadFavouriteTrainsCommand event)
    {
        loadFavouriteTrains(mContext);
    }

    private static void loadTrains(final Context context)
    {

                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
                try {
                    Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();
                    Workflow.getEventBus(context).post(new LoadTrainResult(dao.query(dao.queryBuilder().limit(20l).prepare())));
                } catch (SQLException e) {
                    Workflow.getEventBus(context).post(new LoadTrainResult(e));
                }
                finally
                {
                    OpenHelperManager.releaseHelper();
                }
    }
    private static void loadTrainsOrdered(Context context, long offset, long count)
    {
                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
                try {
                    Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();
                    Workflow.getEventBus(context).post(new LoadTrainResult(dao.query(dao.queryBuilder()
                            .limit(count).offset(offset).orderBy("trainId", true)
                            .prepare())));
                } catch (SQLException e) {
                    Workflow.getEventBus(context).post(new LoadTrainResult(e));
                }
                finally
                {
                    OpenHelperManager.releaseHelper();
                }
    }

    public static Boolean isTrainFav(Context ctx, String trainId)
    {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
        try {
            Dao<Subscribtion, UUID> dao = databaseHelper.getSubscribtionDao();
            return dao.countOf(dao.queryBuilder().setCountOf(true).where().eq("train",trainId).prepare()) != 0;
        } catch (Exception e) {
            return false;
        }
        finally
        {
            OpenHelperManager.releaseHelper();
        }
    }

    public static Boolean hasTrains(Context ctx) {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
        Boolean ret;
        try {
            Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();
            long count = dao.countOf();
            ret = count > 0l;
        } catch (Exception e) {
            Log.e(TAG, "Error in hasTrains", e);
            ret = false;
        }
        return ret;
    }

    public static void deleteTrains(Context mCtx) throws SQLException {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(mCtx, DatabaseHelper.class);
        try {
            Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();
            dao.delete(dao.deleteBuilder().prepare());
        }
        finally
        {
            OpenHelperManager.releaseHelper();
        }
    }

    public static void saveTrain(Context context, Train train) throws SQLException {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();
            dao.create(train);
        }
        finally
        {
            OpenHelperManager.releaseHelper();
        }
    }

    private static void searchTrain(Context ctx, String name)
    {
                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
                try {
                    Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();
                    Workflow.getEventBus(ctx).post(new LoadTrainResult(dao.query(dao.queryBuilder()
                            .orderBy("trainId", true)
                            .where().like("trainId", "%" + name + "%").prepare())));
                } catch (SQLException e) {
                    Workflow.getEventBus(ctx).post(new LoadTrainResult(e));
                }
                finally
                {
                    OpenHelperManager.releaseHelper();
                }
    }

    private static void favTrain(Context context, String train) throws SQLException {
        if(!isTrainFav(context, train))
        {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            try {
                Dao<Subscribtion, UUID> dao = databaseHelper.getSubscribtionDao();
                Subscribtion data = Subscribtion.createNew(new DeviceManager(context).getsDevice());
                data.setTrain(train);
                dao.create(data);
                Workflow.getEventBus(context).post(new FavouriteTrainsChangedEvent());
            }
            finally
            {
                OpenHelperManager.releaseHelper();
            }
        }
    }

    private static void unFavTrain(Context context, String train) throws SQLException {
        if(isTrainFav(context, train))
        {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            try {
                Dao<Subscribtion, UUID> dao = databaseHelper.getSubscribtionDao();
                DeleteBuilder<Subscribtion, UUID> builder =  dao.deleteBuilder();

                builder.setWhere(dao.queryBuilder().where().eq("train", train));
                dao.delete(builder.prepare());
                Workflow.getEventBus(context).post(new FavouriteTrainsChangedEvent());
            }
            finally
            {
                OpenHelperManager.releaseHelper();
            }
        }
    }

    private static void loadFavouriteTrains(Context context)
    {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            List<FavouriteTrain> ret = new ArrayList<FavouriteTrain>();
            Dao<Subscribtion,UUID> dao = databaseHelper.getSubscribtionDao();

            for(Subscribtion item : dao.queryForAll())
            {
                FavouriteTrain newTrain =  new FavouriteTrain();
                newTrain.setTrainId(item.getTrain());
                ret.add(newTrain);
            }
            Workflow.getEventBus(context).post(new LoadFavouriteTrainsResult(ret));
        } catch (Exception e) {
            Workflow.getEventBus(context).post(new LoadFavouriteTrainsResult(e));
        }
        finally
        {
            OpenHelperManager.releaseHelper();
        }
    }

    public static List<Subscribtion> loadSubscriptions(Context context) throws SQLException {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            Dao<Subscribtion, UUID> dao = databaseHelper.getSubscribtionDao();
            return dao.query(dao.queryBuilder().limit(20l).prepare());
        }
        finally
        {
            OpenHelperManager.releaseHelper();
        }
    }

    public static void clearSubscriptions(Context context) throws SQLException {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            Dao<Subscribtion, UUID> dao = databaseHelper.getSubscribtionDao();
            dao.delete(dao.deleteBuilder().prepare());
        }
        finally
        {
            OpenHelperManager.releaseHelper();
        }
    }

    public static void saveSubscriptions(Context context, final Collection<Subscribtion> data) throws SQLException {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            Dao<Subscribtion, UUID> dao = databaseHelper.getSubscribtionDao();

            for(Subscribtion subscribtion : data)
            {
                dao.createOrUpdate(subscribtion);
            }
        }
        finally
        {
            OpenHelperManager.releaseHelper();
        }

    }
}
