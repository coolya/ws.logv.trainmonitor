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

    public static Subscribtion getSubscribtionByTrain(Context ctx, final String trainId)
    {

                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
                try {
                    Dao<Subscribtion, UUID> dao = databaseHelper.getSubscribtionDao();

                    List<Subscribtion> data = dao.query(dao.queryBuilder().where().eq("train", trainId).prepare());
                    if(data.size() > 0)
                        return data.get(0);
                    return null;
                } catch (Exception e) {
                    return null;
                }
                finally
                {
                    OpenHelperManager.releaseHelper();
                    databaseHelper = null;
                }
            }
    }

