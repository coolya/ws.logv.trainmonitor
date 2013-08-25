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
import java.util.UUID;

import ws.logv.trainmonitor.model.FavouriteTrain;
import ws.logv.trainmonitor.model.Station;
import ws.logv.trainmonitor.model.Subscribtion;
import ws.logv.trainmonitor.model.Train;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper  extends OrmLiteSqliteOpenHelper{
	// name of the database file for your application -- change to something appropriate for your app
		private static final String DATABASE_NAME = "ws.logv.trainmonitor.db";
		// any time you make changes to your database objects, you may have to increase the database version
		private static final int DATABASE_VERSION = 4;

		// the DAO object we use to access the SimpleData table
		private Dao<Train, Integer> trainDao = null;
		private Dao<Station, Integer> stationDao = null;
		private Dao<Subscribtion, UUID> subscribtionDao = null;

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * This is called when the database is first created. Usually you should call createTable statements here to create
		 * the tables that will store your data.
		 */
		@Override
		public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
			try {
				Log.i(DatabaseHelper.class.getName(), "onCreate");
				TableUtils.createTable(connectionSource, Train.class);
				TableUtils.createTable(connectionSource, Station.class);
				TableUtils.createTable(connectionSource, Subscribtion.class);
                TableUtils.createTable(connectionSource, FavouriteTrain.class);

			} catch (SQLException e) {
				Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
				throw new RuntimeException(e);
			} 
		}

		/**
		 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
		 * the various data to match the new version number.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			try {
                if(oldVersion == 3)
                {
                    TableUtils.dropTable(connectionSource, Subscribtion.class, true);
                    TableUtils.createTable(connectionSource, Subscribtion.class);
                }
                else if (oldVersion < 3)
                {
                    TableUtils.dropTable(connectionSource, Train.class, true);
                    TableUtils.dropTable(connectionSource, Station.class, true);
                    TableUtils.dropTable(connectionSource, Subscribtion.class, true);
                    TableUtils.dropTable(connectionSource, FavouriteTrain.class, true);
                    // after we drop the old databases, we create the new ones
                    onCreate(db, connectionSource);
                }
               /* else if(oldVersion == 4)
                {
                    db.beginTransaction();
                    try {
                        db.execSQL("ALTER TABLE subscribtion RENAME TO subscribtion_old");
                        TableUtils.createTable(connectionSource, Subscribtion.class);
                        db.execSQL("INSERT into subscribtion ( SELECT * FROM subscribtion_old GROUP BY train)");
                        db.execSQL("DROP TABLE subscribtion_old");
                        db.setTransactionSuccessful();
                    } catch (Throwable tr)
                    {
                        Log.e(DatabaseHelper.class.getName(), "Can't update database to version 5", tr);
                    }
                    db.endTransaction();

                }  */
                else
                {
                    throw new IllegalStateException("No update strategy from " + String.valueOf(oldVersion) + " to " +
                                                    String.valueOf(newVersion));
                }
			} catch (SQLException e) {
				Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
		 * value.
		 * @throws java.sql.SQLException 
		 */
		public Dao<Train, Integer> getTrainDataDao() throws java.sql.SQLException {
			if (trainDao == null) {
				trainDao = getDao(Train.class);
			}
			return trainDao;
		}
		
		public Dao<Station, Integer> getStationDao() throws java.sql.SQLException {
			if(stationDao == null)
			{
				stationDao = getDao(Station.class);
			}
			return stationDao;
		}
		
		public Dao<Subscribtion, UUID> getSubscribtionDao() throws java.sql.SQLException {
			if(subscribtionDao == null)
			{
				subscribtionDao = getDao(Subscribtion.class);
			}
			return subscribtionDao;
		}


		/**
		 * Close the database connections and clear any cached DAOs.
		 */
		@Override
		public void close() {
			super.close();
			trainDao = null;
		}	

}
