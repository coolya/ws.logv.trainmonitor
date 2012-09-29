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
		private static final int DATABASE_VERSION = 3;

		// the DAO object we use to access the SimpleData table
		private Dao<Train, Integer> trainDao = null;
		private Dao<Station, Integer> stationDao = null;
		private Dao<Subscribtion, UUID> subscribtionDao = null;
        private Dao<FavouriteTrain, Integer> favouriteTrainDao = null;

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
				TableUtils.dropTable(connectionSource, Train.class, true);
				TableUtils.dropTable(connectionSource, Station.class, true);
				TableUtils.dropTable(connectionSource, Subscribtion.class, true);
                TableUtils.dropTable(connectionSource, FavouriteTrain.class, true);
			} catch (SQLException e) {
				Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
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

        public Dao<FavouriteTrain, Integer> getFavouriteTrainDao() throws SQLException {
            if(favouriteTrainDao == null)
            {
                favouriteTrainDao = getDao(FavouriteTrain.class);
            }

            return favouriteTrainDao;
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
