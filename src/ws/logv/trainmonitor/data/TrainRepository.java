package ws.logv.trainmonitor.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import android.content.Context;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.DatabaseConnection;
import ws.logv.trainmonitor.FavChangedListener;
import ws.logv.trainmonitor.model.FavouriteTrain;
import ws.logv.trainmonitor.model.Train;

public class TrainRepository 
{
   private static final String TAG = "TrainRepository" ;
   private static FavChangedListener listener = null;
	
	public static void loadTrains(Context context,final Action<List<Train>> callback)
	{
		new DatabaseTask<List<Train>>(new Func<List<Train>, Context>(){

			public List<Train> exec(Context param) {
				DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
				try {
					Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();			
					return dao.query(dao.queryBuilder().limit(20l).prepare());
				} catch (SQLException e) {
					return null;
				}
				finally
				{
					OpenHelperManager.releaseHelper();
					databaseHelper = null;
				}
			}},
			new Action<List<Train>>(){

				public void exec(List<Train> param) {
					callback.exec(param);					
				}}).execute(context);	
	}
    public static void loadTrainsOrdered(Context context, final long offset, final Action<List<Train>> callback)
    {
        new DatabaseTask<List<Train>>(new Func<List<Train>, Context>(){

            public List<Train> exec(Context param) {
                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
                try {
                    Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();
                    return dao.query(dao.queryBuilder().limit(50l).offset(offset).orderBy("trainId", true).prepare());
                } catch (SQLException e) {
                    return null;
                }
                finally
                {
                    OpenHelperManager.releaseHelper();
                    databaseHelper = null;
                }
            }},
                new Action<List<Train>>(){

                    public void exec(List<Train> param) {
                        callback.exec(param);
                    }}).execute(context);
    }
	
	public static void searchTrain(Context ctx, final String name, final Action<List<Train>> callback)
	{
		new DatabaseTask<List<Train>>(new Func<List<Train>, Context>(){

			public List<Train> exec(Context param) {
				DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
				try {
					Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();			
					return dao.query(dao.queryBuilder().orderBy("trainId", true).where().like("trainId", name + "%").prepare());
				} catch (SQLException e) {
					return null;
				}
				finally
				{
					OpenHelperManager.releaseHelper();
					databaseHelper = null;
				}
			}},
			new Action<List<Train>>(){

				public void exec(List<Train> param) {
					callback.exec(param);					
				}}).execute(ctx);
	}
	public static void saveTrains(Context ctx, final Collection<Train> data, final Action<Boolean> callback)
	{
		new DatabaseTask<Boolean>(new Func<Boolean, Context>(){

			public Boolean exec(Context param) {
				DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
				try {
					Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();
					for(Train train : data)
					{
						dao.createOrUpdate(train);
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
    public static void saveTrains(Context ctx, final Collection<Train> data, final Action<Boolean> callback, final Action<Integer> progress)
    {
        new DatabaseTask<Boolean>(new Func2<Boolean, Context, Action<Integer>>(){
            @Override
            public Boolean exec(Context param, Action<Integer> param2) {
                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
                try {
                    Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();

                    int i = 0;
                    for(Train train : data)
                    {
                        i++;
                        dao.createOrUpdate(train);
                        param2.exec(i);
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
            }
        },
                new Action<Boolean>(){

                    public void exec(Boolean param) {
                        if(callback != null)
                            callback.exec(param);

                    }}, progress).execute(ctx);
    }
	
	public static void deleteTrains(Context ctx, final Action<Boolean> callback)
	{
		
		new DatabaseTask<Boolean>(new Func<Boolean, Context>(){

			public Boolean exec(Context param) {
				DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
				try {
					Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();			
					dao.delete(dao.deleteBuilder().prepare());
					return true;
				} catch (SQLException e) {
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
    public static Boolean isTrainFav(Context ctx, Train train)
    {
        return isTrainFav(ctx, train.getTrainId());
    }

    public static Boolean isTrainFav(Context ctx, String trainId)
    {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
        try {
            Dao<FavouriteTrain, Integer> dao = databaseHelper.getFavouriteTrainDao();
            return dao.countOf(dao.queryBuilder().setCountOf(true).where().eq("trainId",trainId).prepare()) != 0;
        } catch (Exception e) {
            return false;
        }
        finally
        {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public static DatabaseTask<Boolean> favTrain(Context ctx, final Train train, final Action<Boolean> callback)
    {
        DatabaseTask<Boolean> task = favTrain(ctx, train.getTrainId(), callback);
        return task;
    }

    public static DatabaseTask<Boolean> favTrain(Context ctx, final String trainId, final Action<Boolean> callback)
    {
        DatabaseTask<Boolean> task =
                new DatabaseTask<Boolean>(new Func<Boolean, Context>(){

                    public Boolean exec(Context param) {
                        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
                        try {
                            Dao<FavouriteTrain, Integer> dao = databaseHelper.getFavouriteTrainDao();

                            FavouriteTrain data = new FavouriteTrain();
                            data.setTrainId(trainId);
                            dao.create(data);
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
                                if(listener != null)
                                    listener.onFavChanged();
                                if(callback != null)
                                    callback.exec(param);

                            }});

        task.execute(ctx);
        return task;
    }

    public static DatabaseTask<Boolean> unFavTrain(Context ctx, final Train train, final Action<Boolean> callback)
    {
        DatabaseTask<Boolean> task = unFavTrain(ctx, train.getTrainId(), callback);
        return task;
    }

    public static DatabaseTask<Boolean> unFavTrain(Context ctx, final String trainId, final Action<Boolean> callback)
    {
        DatabaseTask<Boolean> task =
                new DatabaseTask<Boolean>(new Func<Boolean, Context>(){

                    public Boolean exec(Context param) {
                        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
                        try {
                            Dao<FavouriteTrain, Integer> dao = databaseHelper.getFavouriteTrainDao();
                            DeleteBuilder<FavouriteTrain, Integer> builder =  dao.deleteBuilder();

                            builder.setWhere(dao.queryBuilder().where().eq("trainId", trainId));
                            dao.delete(builder.prepare());
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
                                if(listener != null)
                                    listener.onFavChanged();
                                if(callback != null)
                                    callback.exec(param);

                            }});

        task.execute(ctx);
        return task;
    }

    public static DatabaseTask<Collection<FavouriteTrain>> loadFavouriteTrains(Context ctx, final Action<Collection<FavouriteTrain>> callback)
    {
        DatabaseTask<Collection<FavouriteTrain>> task = new DatabaseTask<Collection<FavouriteTrain>>(new Func<Collection<FavouriteTrain>, Context>() {
            @Override
            public Collection<FavouriteTrain> exec(Context param) {
                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
                try {
                    Dao<FavouriteTrain, Integer> dao = databaseHelper.getFavouriteTrainDao();
                    return dao.queryForAll();
                } catch (Exception e) {
                    return null;
                }
                finally
                {
                    OpenHelperManager.releaseHelper();
                    databaseHelper = null;
                }
            }
        }, new Action<Collection<FavouriteTrain>>() {
            @Override
            public void exec(Collection<FavouriteTrain> param) {
                if(callback != null)
                    callback.exec(param);
            }
        });

        task.execute(ctx);
        return task;
    }

    public static void setFavChangedListener(FavChangedListener favChangedListener) {
        listener = favChangedListener;
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
}
