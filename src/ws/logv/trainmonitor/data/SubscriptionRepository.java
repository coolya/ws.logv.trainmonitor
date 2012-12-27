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

import ws.logv.trainmonitor.model.Subscribtion;
import android.content.Context;

public class SubscriptionRepository {

    public static List<Subscribtion> loadSubscriptions(Context context) throws SQLException {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            Dao<Subscribtion, UUID> dao = databaseHelper.getSubscribtionDao();
            return dao.query(dao.queryBuilder().limit(20l).prepare());
        }
        finally
        {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public static void clearSubscribtions(Context context) throws SQLException {
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

    public static void saveSubscribtions(Context context, final Collection<Subscribtion> data) throws SQLException {
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

