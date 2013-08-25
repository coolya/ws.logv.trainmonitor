/*
 * Copyright 2013. Kolja Dummann <k.dummann@gmail.com>
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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ws.logv.trainmonitor.model.Train;

import java.util.Collection;

/**
 * Created by kolja on 02.06.13.
 */
public class TrainDB {

	private Context context;
	private DatabaseHelper databaseHelper;

	private static final String LOG_TAG = "TRAIN_DB";


	public TrainDB(Context context)
	{
		this.context = context;
		databaseHelper = new DatabaseHelper(context);
	}

	public Cursor loadTrains()
	{
		return loadTrains(null, null, null);
	}

	public Cursor loadTrains(String[] projection, String selection, String[] selectionArgs)
	{
		SQLiteDatabase db = databaseHelper.getReadableDatabase();

		String sortOrder =
				Tables.Train.ColumnNames.TRAIN_ID + " DESC";

		return db.query(Tables.Train.NAME, projection, selection, selectionArgs, null, null, sortOrder);
	}

	public Cursor loadTrains(TrainType trainType)
	{
		String selection = Tables.Train.ColumnNames.TRAIN_ID + "LIKE %?";

		return loadTrains(null, selection, new String[]{TrainType.getString(trainType)});
	}

	public Train loadTrain(String id)
	{
		String selection = Tables.Train.ColumnNames.TRAIN_ID + "LIKE ?";

		Cursor cursor = loadTrains(null, selection, new String[]{id});

		if(cursor != null && cursor.moveToFirst())
		{
			Train ret = Train.fromCursor(cursor);
			cursor.close();
			return ret;
		}

		return null;
	}

	public void saveTrain(Train train)
	{
		SQLiteDatabase db = databaseHelper.getWritableDatabase();


	}

	private void saveTrain(SQLiteDatabase db, Train train)
	{
		db.insert(Tables.Train.NAME, null, Train.toContent(train));
	}

	public void saveTrains(Collection<Train> trains)
	{

	}
}
