package ws.logv.trainmonitor.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import ws.logv.trainmonitor.app.DeviceManager;
import ws.logv.trainmonitor.app.Installation;
import ws.logv.trainmonitor.model.Subscribtion;
import android.content.Context;

public class SubscribtionRepository {

	public static  DatabaseTask<List<Subscribtion>> loadSubscribtions(Context context,final Action<List<Subscribtion>> callback)
	{
        DatabaseTask<List<Subscribtion>> task = new DatabaseTask<List<Subscribtion>>(new Func<List<Subscribtion>, Context>(){
	
				public List<Subscribtion> exec(Context param) {
					DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
					try {
						Dao<Subscribtion, UUID> dao = databaseHelper.getSubscribtionDao();			
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
				new Action<List<Subscribtion>>(){
	
					public void exec(List<Subscribtion> param) {
						if(callback != null)
                            callback.exec(param);
					}});
        task.execute(context);
        return task;
	}
	public static DatabaseTask<Boolean> saveSubscribtions(Context context, final Collection<Subscribtion> data, final Action<Boolean> callback)
	{
        DatabaseTask<Boolean> task = new DatabaseTask<Boolean>(new Func<Boolean, Context>(){
			
			public Boolean exec(Context param) {
				DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
				try {
					Dao<Subscribtion, UUID> dao = databaseHelper.getSubscribtionDao();			
					if(data == null)
                        return true;

					for(Subscribtion subscribtion : data)
					{
						dao.createOrUpdate(subscribtion);
					}
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
				}});
        task.execute(context);
        return task;
	}

    public static DatabaseTask<Subscribtion> getSubscribtionByTrain(Context ctx, final String trainId, Action<Subscribtion> callback)
    {
        DatabaseTask task = new DatabaseTask<Subscribtion>(new Func<Subscribtion, Context>() {
            @Override
            public Subscribtion exec(Context param) {
                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
                try {
                    Dao<Subscribtion, UUID> dao = databaseHelper.getSubscribtionDao();
                    return dao.query(dao.queryBuilder().where().eq("train", trainId).prepare()).get(0);
                } catch (Exception e) {
                    return null;
                }
                finally
                {
                    OpenHelperManager.releaseHelper();
                    databaseHelper = null;
                }
            }
        }, callback);
        task.doInBackground(ctx);
        return task;
    }
}
