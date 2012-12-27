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

import android.content.Context;
import com.turbomanage.httpclient.BasicRequestHandler;
import com.turbomanage.httpclient.HttpMethod;
import ws.logv.trainmonitor.R;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyStore;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 25.12.12
 * Time: 21:23
 * To change this template use File | Settings | File Templates.
 */
class LogvSslRequestHandler extends BasicRequestHandler{
    private Context mContext;
    public LogvSslRequestHandler(Context context)
    {
      mContext = context;
    }
    private SSLSocketFactory sslSocketFactory;

    @Override
    public void prepareConnection(HttpURLConnection urlConnection, HttpMethod httpMethod, String contentType) throws IOException {
        super.prepareConnection(urlConnection, httpMethod, contentType);
        if(urlConnection instanceof HttpsURLConnection)
        {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;

            if(sslSocketFactory == null)
                sslSocketFactory = newSslSocketFactory();

            httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
        }
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            InputStream in = mContext.getResources().openRawResource(R.raw.keystore);
            try {
                trusted.load(in, "mysecret".toCharArray());
            } finally {
                in.close();
            }
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(trusted);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            return context.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
