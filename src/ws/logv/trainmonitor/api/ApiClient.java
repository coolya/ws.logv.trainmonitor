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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

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
	private final String URI = "http://trainmonitor.logv.ws/api/v1/";
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
		httpClient.get("germany/trains", params, callback);
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
            httpClient.get(URLEncoder.encode("germany/trains/" + trainId, "UTF-8"), params, callback);
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
		httpClient.get("germany/stations",params , callback);
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

        httpClient.post("devices?type=ANDROID", params, callback);
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
        String devicePart = Base64.encodeToString(asByteArray(device.getId()), Base64.URL_SAFE);
        try {
            httpClient.put("devices/" + devicePart.substring(0, 22), "application/json", data.getBytes(), callback);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static byte[] asByteArray(UUID uuid) {

        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }

        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }
        return buffer;

    }

}
