package ws.logv.trainmonitor.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import android.content.Context;
import ws.logv.trainmonitor.model.Train;

public class TrainRepository 
{
	
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
	
	public static void searchTrain(Context ctx, final String name, final Action<List<Train>> callback)
	{
		new DatabaseTask<List<Train>>(new Func<List<Train>, Context>(){

			public List<Train> exec(Context param) {
				DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
				try {
					Dao<Train, Integer> dao = databaseHelper.getTrainDataDao();			
					return dao.query(dao.queryBuilder().where().like("trainId", name).prepare());
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

}
