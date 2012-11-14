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
import ws.logv.trainmonitor.model.Station;
import ws.logv.trainmonitor.model.Train;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 9/29/12
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class StationRepository {

    public static DatabaseTask<List<Station>> loadStations(Context context,final Action<List<Station>> callback)
    {
        DatabaseTask<List<Station>> task =
         new DatabaseTask<List<Station>>(new Func<List<Station>, Context>(){

            public List<Station> exec(Context param) {
                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(param, DatabaseHelper.class);
                try {
                    Dao<Station, Integer> dao = databaseHelper.getStationDao();
                    return dao.query(dao.queryBuilder().prepare());
                } catch (SQLException e) {
                    return null;
                }
                finally
                {
                    OpenHelperManager.releaseHelper();
                    databaseHelper = null;
                }
            }},
                new Action<List<Station>>(){

                    public void exec(List<Station> param) {
                        if(callback != null)
                            callback.exec(param);
                    }});
        task.execute(context);
        return task;
    }

    public  static DatabaseTask<Station> loadStation(final int id, Context ctx, final Action<Station> callback)
    {
        DatabaseTask<Station> task =
        new DatabaseTask<Station>(new Func<Station, Context>(){

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
        new DatabaseTask<Boolean>(new Func<Boolean, Context>(){

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


}
