package ws.logv.trainmonitor.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;

import android.util.Base64;
import ws.logv.trainmonitor.app.Installation;
import ws.logv.trainmonitor.data.Action;
import ws.logv.trainmonitor.model.*;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
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
					JsonHelper helper = new JsonHelper();
					 helper.fromJsonAsync(data,
                            new TypeToken<Train[]>() {
                            }.getType(), new Action<Train[]>() {
                        @Override
                        public void exec(Train[] param) {
                            apiCallback.onComplete(Arrays.asList(param));
                        }
                    });

				}
				catch (Exception ex)
				{
				 Log.e(this.getClass().getName(), "Failed to load trains from API", ex);
				}
				
			}
        };
        ParameterMap params = httpClient.newParams();
        params.add("install", Installation.Id(ctx));
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
					JsonHelper helper = new JsonHelper();					
					Train ret = helper.fromJson(data, new TypeToken<Train>() {}.getType());
					apiCallback.onComplete(ret);				
				}
				catch (Exception ex)
				{
					apiCallback.onError(ex);
				}				
			}
        };
        ParameterMap params = httpClient.newParams();
        params.add("install", Installation.Id(ctx));
        params.add("client", "ANDROID");
        try {
            httpClient.get(URLEncoder.encode("trains/" + trainId, "UTF-8"), params, callback);
        } catch (UnsupportedEncodingException e) {
            apiCallback.onError(e);
        }
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
					JsonHelper helper = new JsonHelper();					
					Station[] ret = helper.fromJson(data, new TypeToken<Station[]>() {}.getType());
					apiCallback.onComplete(Arrays.asList(ret));
				}
				catch (Exception ex)
				{
					apiCallback.onError(ex);
				}				
			}
        };
        ParameterMap params = httpClient.newParams();
        params.add("install", Installation.Id(ctx));
        params.add("client", "ANDROID");
		httpClient.get("stations",params , callback);
	}	
	
	public void postSubscribtion(Collection<Subscribtion> data, final IApiCallback<Collection<Subscribtion>> apiCallback)
	{
		final JsonHelper jsonHelper = new JsonHelper();
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
					Collection<Subscribtion> ret = jsonHelper.fromJson(data, new TypeToken<Collection<Subscribtion>>() {}.getType());
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


    public void registerDevice(final IApiCallback<Device> apiCallback)
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
                    JsonHelper helper = new JsonHelper();
                    Device ret = helper.fromJson(data, new TypeToken<Device>() {}.getType());
                    apiCallback.onComplete(ret);
                }
                catch (Exception ex)
                {
                    apiCallback.onError(ex);
                }
            }
        };
        ParameterMap params = httpClient.newParams();
        params.add("type", "ANDROID");
        httpClient.post("devices", params, callback);
    }

    public void putDevice(Device device)
    {
        AsyncCallback callback = new AsyncCallback() {
            @Override
            public void onComplete(HttpResponse httpResponse) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };

        JsonHelper helper = new JsonHelper();
        String data = helper.toJson(device);
        String devicePart = Base64.encodeToString(device.getId().toString().getBytes(), Base64.URL_SAFE);
        try {
            httpClient.put(URLEncoder.encode("devices/" + devicePart.substring(0, 22), "UTF-8"), "application/json", data.getBytes(), callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
