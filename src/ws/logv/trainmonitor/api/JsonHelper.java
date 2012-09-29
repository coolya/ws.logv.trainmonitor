package ws.logv.trainmonitor.api;

import java.lang.reflect.Type;

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
	
	public String toJson(Object data)
	{
		GsonBuilder b = new GsonBuilder();
		b.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");				
		Gson gson = b.create();				
		
		return gson.toJson(data);
	}
}
