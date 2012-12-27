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

package ws.logv.trainmonitor.app.manager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.event.AccountChoosnEvent;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;


    public class UserManager {
    private static UserManager sInstance;
    private Context mContext;
    private String mEmail;
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";

    public UserManager(Context context)
    {
        mContext = context;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEvent(AccountChoosnEvent event)
    {
        String string = mContext.getSharedPreferences(Constants.Settings.PERF, 0).getString(Constants.Settings.CURRENT_ACCOUNT, "");
        Init(string);
    }

    private UserManager(String email)
    {
           mEmail = email;
    }

    public Map<String, String> getAuthHeader(Context ctx)
    {
        if(mEmail != null && !"".equals(mEmail))
        {
        try {
            String token = fetchToken(ctx);
            TreeMap<String, String> ret = new TreeMap<String, String>();
            ret.put("Authorization", "Google " + token);
            return ret;
        } catch (IOException e) {


        }
        }
        return new TreeMap<String, String>();
    }

    /**
     * Get a authentication token if one is not available. If the error is not recoverable then
     * it displays the error message on parent activity.
     */
    private String fetchToken(Context ctx) throws IOException {
        try {

            return GoogleAuthUtil.getToken(ctx, mEmail, SCOPE);
        } catch (GooglePlayServicesAvailabilityException playEx) {
            // GooglePlayServices.apk is either old, disabled, or not present.

        } catch (UserRecoverableAuthException userRecoverableException) {
            // Unable to authenticate, but the user can fix this.
            // Forward the user to the appropriate activity.
            ctx.startActivity(userRecoverableException.getIntent());

        } catch (GoogleAuthException fatalException) {
            Log.e("UserManager", "Failed to get Token", fatalException);
        }
        return null;
    }

    public synchronized static UserManager Instance()
    {
        return sInstance;
    }

    private synchronized  static void Init(String email)
    {
        if(sInstance == null)
        {
            sInstance  = new UserManager(email);
        }
    }

}
