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
