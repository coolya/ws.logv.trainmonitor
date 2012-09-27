package ws.logv.trainmonitor.api;

import java.util.Collection;

import ws.logv.trainmonintor.app.Installation;
import ws.logv.trainmonitor.model.Station;
import ws.logv.trainmonitor.model.Subscribtion;
import ws.logv.trainmonitor.model.Train;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

public class ApiClient {
	private AndroidHttpClient httpClient;
	private final String URI = "http://trainmonitor.logv.ws/api/v1/germany/";
	private final Context ctx;
	
	public ApiClient(Context ctx)
	{
		httpClient = new AndroidHttpClient(URI);
		this.ctx = ctx;
	}
	
	private Boolean isConnected()
	{
	    ConnectivityManager connMgr = (ConnectivityManager) 
	            ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    return (networkInfo != null && networkInfo.isConnected());
	}
	
	public void getTrains(final IApiCallback<Collection<Train>> apiCallback)
	{
		if(!isConnected())
		{
			apiCallback.onNoConnection();
			return;
		}
		
		AsyncCallback callback = new AsyncCallback() {
         
            @Override
            public void onError(Exception e) {
                apiCallback.onError(e);
            }
			@Override
			public void onComplete(HttpResponse httpResponse) {				
				String data = httpResponse.getBodyAsString();			
				try	{
					JsonHelper<Collection<Train>> helper = new JsonHelper<Collection<Train>>();
					Collection<Train> ret = helper.fromJson(data);
					apiCallback.onComplete(ret);				
				}
				catch (Exception ex)
				{
				 Log.e(this.getClass().getName(), "Failed to load trains from API", ex);
				}
				
			}
        };
        ParameterMap params = httpClient.newParams();
        params.add("install", Installation.Id());
        params.add("client", "ANDROID");
		httpClient.get("trains", params, callback);	
	}
	
	public void getTrainDetail(String trainId, final IApiCallback<Train> apiCallback)
	{
		AsyncCallback callback = new AsyncCallback() {
	         
            @Override
            public void onError(Exception e) {
                apiCallback.onError(e);
            }
			@Override
			public void onComplete(HttpResponse httpResponse) {				
				String data = httpResponse.getBodyAsString();				
				try	{
					JsonHelper<Train> helper = new JsonHelper<Train>();					
					Train ret = helper.fromJson(data);
					apiCallback.onComplete(ret);				
				}
				catch (Exception ex)
				{
					apiCallback.onError(ex);
				}				
			}
        };
        ParameterMap params = httpClient.newParams();
        params.add("install", Installation.Id());
        params.add("client", "ANDROID");
		httpClient.get("trains/" + trainId, params, callback);
	}
	
	public void getStations(final IApiCallback<Collection<Station>> apiCallback)
	{
		AsyncCallback callback = new AsyncCallback() {
	         
            @Override
            public void onError(Exception e) {
                apiCallback.onError(e);
            }
			@Override
			public void onComplete(HttpResponse httpResponse) {				
				String data = httpResponse.getBodyAsString();				
				try	{
					JsonHelper<Collection<Station>> helper = new JsonHelper<Collection<Station>>();					
					Collection<Station> ret = helper.fromJson(data);
					apiCallback.onComplete(ret);				
				}
				catch (Exception ex)
				{
					apiCallback.onError(ex);
				}				
			}
        };
        ParameterMap params = httpClient.newParams();
        params.add("install", Installation.Id());
        params.add("client", "ANDROID");
		httpClient.get("stations",params , callback);
	}	
	
	public void postSubscribtion(Subscribtion data, final IApiCallback<Subscribtion> apiCallback)
	{
		final JsonHelper<Subscribtion> jsonHelper = new JsonHelper<Subscribtion>();
		String json = jsonHelper.toJson(data);
		
		AsyncCallback callback = new AsyncCallback() {
	         
            @Override
            public void onError(Exception e) {
                apiCallback.onError(e);
            }
			@Override
			public void onComplete(HttpResponse httpResponse) {				
				String data = httpResponse.getBodyAsString();				
				try	{							
					Subscribtion ret = jsonHelper.fromJson(data);
					apiCallback.onComplete(ret);				
				}
				catch (Exception ex)
				{
					apiCallback.onError(ex);
				}				
			}
        };
		
		httpClient.post("subscribtions", "application/json", json.getBytes(), callback);
	}

}
