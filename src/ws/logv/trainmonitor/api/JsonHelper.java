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

package ws.logv.trainmonitor.api;

import java.lang.reflect.Type;

import android.os.AsyncTask;
import ws.logv.trainmonitor.data.Action;
import ws.logv.trainmonitor.model.TrainStatus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonHelper {

	public <T> T fromJson(String json, Type typeOfT)
	{
		GsonBuilder b = new GsonBuilder();
		b.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		b.registerTypeAdapter(TrainStatus.class, new TrainStatusDeserializer());
		Gson gson = b.create();			
	
		return gson.fromJson(json, typeOfT);
	}

    public <T> void fromJsonAsync(String json, final Type typeOfT, final Action<T> callback)
    {
        new AsyncTask<String, Void, T>() {
            @Override
            protected T doInBackground(String... strings) {
                GsonBuilder b = new GsonBuilder();
                b.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                b.registerTypeAdapter(TrainStatus.class, new TrainStatusDeserializer());
                Gson gson = b.create();

                return gson.fromJson(strings[0], typeOfT);
            }

            @Override
            protected void onPostExecute(T t) {
                super.onPostExecute(t);
                callback.exec(t);
            }
        } .execute(json);
    }
	
	public String toJson(Object data)
	{
		GsonBuilder b = new GsonBuilder();
		b.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");				
		Gson gson = b.create();				
		
		return gson.toJson(data);
	}
}
