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

import java.util.*;

import android.net.Uri;
import android.util.Base64;
import ws.logv.trainmonitor.BuildConfig;
import ws.logv.trainmonitor.app.manager.UserManager;
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

    private final String URI ;
	private final Context ctx;

	public ApiClient(Context ctx)
	{
       if(BuildConfig.DEBUG)
       {
          // URI  = "https://trainmonitor.logv.ws/api/v1/";
           URI  = "http://[2001:470:1f15:5e3:11c4:492:eb77:f7ab]:8080/api/v1/";
       }
        else
       {
           URI  = "https://trainmonitor.logv.ws/api/v1/";
       }
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
        AndroidHttpClient httpClient = new AndroidHttpClient(URI, new LogvSslRequestHandler(ctx));
        ParameterMap params = httpClient.newParams();
        params.add("client", "ANDROID");
		httpClient.get("germany/trains", params, callback);


	}

    public Collection<Train> getTrains()
    {
        AndroidHttpClient httpClient = new AndroidHttpClient(URI, new LogvSslRequestHandler(ctx));
        ParameterMap params = httpClient.newParams();
        params.add("client", "ANDROID");
        HttpResponse res = httpClient.get("germany/trains", params);

        String data = res.getBodyAsString();
        try	{
            JsonHelper helper = new JsonHelper();
            Train[] ret = helper.fromJson(data, new TypeToken<Train[]>() {}.getType());
            return Arrays.asList(ret);
        }
        catch (Exception ex)
        {
            Log.e(this.getClass().getName(), "Failed to load trains from API", ex);
            return  null;
        }
    }

    public  Train getTrainDetail(String trainId)
    {
        AndroidHttpClient httpClient = new AndroidHttpClient(URI, new LogvSslRequestHandler(ctx));
        ParameterMap params = httpClient.newParams();
        params.add("client", "ANDROID");
        HttpResponse res =  httpClient.get("germany/trains/" + Uri.encode(trainId), params);

        String data = res.getBodyAsString();
        JsonHelper helper = new JsonHelper();
        Train ret = helper.fromJson(data, new TypeToken<Train>() {}.getType());
        return ret;
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
        AndroidHttpClient httpClient = new AndroidHttpClient(URI, new LogvSslRequestHandler(ctx));
        ParameterMap params = httpClient.newParams();
        params.add("client", "ANDROID");
        try {
            httpClient.get("germany/trains/" + Uri.encode(trainId), params, callback);
        } catch (Exception e) {
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
        AndroidHttpClient httpClient = new AndroidHttpClient(URI, new LogvSslRequestHandler(ctx));
        ParameterMap params = httpClient.newParams();
        params.add("client", "ANDROID");
		httpClient.get("germany/stations",params , callback);
	}

    public List<Station> getStations()
    {
        AndroidHttpClient httpClient = new AndroidHttpClient(URI, new LogvSslRequestHandler(ctx));
        ParameterMap params = httpClient.newParams();
        params.add("client", "ANDROID");
        HttpResponse res =  httpClient.get("germany/stations",params);
        String data = res.getBodyAsString();
        JsonHelper helper = new JsonHelper();
        Station[] ret = helper.fromJson(data, new TypeToken<Station[]>() {}.getType());
        return Arrays.asList(ret);
    }
	
	public void postSubscribtion(final Collection<Subscribtion> data, final IApiCallback<Collection<Subscribtion>> apiCallback)
	{
        new Thread() {
            @Override
            public void run() {
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
                Map<String, String> headers = UserManager.Instance().getAuthHeader(ctx);
                AndroidHttpClient httpClient = new AndroidHttpClient(URI, new LogvSslRequestHandler(ctx));

                for(Map.Entry<String, String> item : headers.entrySet())
                {
                    httpClient.addHeader(item.getKey(), item.getValue());
                }
                httpClient.post("subscribtions", "application/json", json.getBytes(), callback);
            }
        }.start();
	}

    public void getSubscriptions(final IApiCallback<Collection<Subscribtion>> apiCallback)
    {
        new Thread() {
            @Override
            public void run() {
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
                            Collection<Subscribtion> ret = helper.fromJson(data, new TypeToken<Collection<Subscribtion>>() {}.getType());
                            apiCallback.onComplete(ret);
                        }
                        catch (Exception ex)
                        {
                            apiCallback.onError(ex);
                        }
                    }
                };

                Map<String, String> headers = UserManager.Instance().getAuthHeader(ctx);
                AndroidHttpClient httpClient = new AndroidHttpClient(URI, new LogvSslRequestHandler(ctx));

                for(Map.Entry<String, String> item : headers.entrySet())
                {
                    httpClient.addHeader(item.getKey(), item.getValue());
                }

                ParameterMap params = httpClient.newParams();
                params.add("client", "ANDROID");

                httpClient.get("subscribtions", params, callback);
            }
        }.start();
    }


    public void registerDevice(final IApiCallback<Device> apiCallback)
    {
        new Thread() {
            @Override
            public void run() {
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
                Map<String, String> headers = UserManager.Instance().getAuthHeader(ctx);
                AndroidHttpClient httpClient = new AndroidHttpClient(URI, new LogvSslRequestHandler(ctx));

                for(Map.Entry<String, String> item : headers.entrySet())
                {
                    httpClient.addHeader(item.getKey(), item.getValue());
                }
                ParameterMap params = httpClient.newParams();

                httpClient.post("devices?type=ANDROID", params, callback);
            }
        }.start();
    }

    public void putDevice(final Device device)
    {
        new Thread()
        {
            @Override
            public void run() {
                AsyncCallback callback = new AsyncCallback() {
                    @Override
                    public void onComplete(HttpResponse httpResponse) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }
                };

                JsonHelper helper = new JsonHelper();
                String data = helper.toJson(device);
                String devicePart = Base64.encodeToString(asByteArray(device.getId()), Base64.URL_SAFE);
                Map<String, String> headers = UserManager.Instance().getAuthHeader(ctx);
                AndroidHttpClient httpClient = new AndroidHttpClient(URI, new LogvSslRequestHandler(ctx));

                for(Map.Entry<String, String> item : headers.entrySet())
                {
                    httpClient.addHeader(item.getKey(), item.getValue());
                }

                try {
                    httpClient.put("devices/" + devicePart.substring(0, 22), "application/json", data.getBytes(), callback);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }.start();

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
