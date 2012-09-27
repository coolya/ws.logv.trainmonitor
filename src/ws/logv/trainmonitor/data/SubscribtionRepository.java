package ws.logv.trainmonitor.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import ws.logv.trainmonitor.model.Subscribtion;
import android.content.Context;

public class SubscribtionRepository {

	public static void loadSubscribtions(Context context,final Action<List<Subscribtion>> callback)
	{		
		new DatabaseTask<List<Subscribtion>>(new Func<List<Subscribtion>, Context>(){
	
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
						callback.exec(param);					
					}}).execute(context);	
	}
	public static void saveSubscribtions(Context context, final Collection<Subscribtion> data, final Action<Boolean> callback)
	{
		new DatabaseTask<Boolean>(new Func<Boolean, Context>(){
			
			public Boolean exec(Context param) {
				DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
				try {
					Dao<Subscribtion, UUID> dao = databaseHelper.getSubscribtionDao();			
					
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
				}}).execute(context);
	}
}
