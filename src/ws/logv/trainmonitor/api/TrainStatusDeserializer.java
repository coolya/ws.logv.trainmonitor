package ws.logv.trainmonitor.api;

import java.lang.reflect.Type;

import ws.logv.trainmonitor.model.TrainStatus;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class TrainStatusDeserializer implements JsonDeserializer<TrainStatus> {

	public TrainStatus deserialize(JsonElement json, Type type,
			JsonDeserializationContext ctx) throws JsonParseException {
		String value = json.getAsString();
		return TrainStatus.findByAbbr(value);
		
	}

}
