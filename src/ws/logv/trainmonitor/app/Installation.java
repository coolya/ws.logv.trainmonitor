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

package ws.logv.trainmonitor.app;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import ws.logv.trainmonitor.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class Installation {
    private static final String LOG_TAG = "Installation";

	private static final String FILENAME = "INSTALLATION";
    private static final String DISCLAIMER_FILE = "DISCLAIMER";
    private static final String ACCOUNT_FILE = "ACCOUNT";
    private static final String MOTD_FILE = "MOTD";

	private static String ID;
	
	public static String Id(Context ctx)
	{
		if(ID == null)
		{
			File file = new File(ctx.getFilesDir(), FILENAME);
			
			if(file.exists())
			{
				try {
					ID = readFile(file);
				} catch (Exception e) {
					ID = null;
				}
			}
			else
			{
				String id = UUID.randomUUID().toString();
				try {
					file.createNewFile();
					writeFile(file, id.getBytes());
				} catch (Exception e) {
					ID = null;
				}
			}		 
		}		
		return ID;
	}

    public static Boolean wasDisclaimerShown(Context ctx)
    {
        File file = new File(ctx.getFilesDir(), DISCLAIMER_FILE);

        return file.exists();
    }

    public static void setDisclaimerShown(Context ctx)
    {
        File file = new File(ctx.getFilesDir(), DISCLAIMER_FILE);
        if(!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error creatin disclaimer file", e);
            }
    }
    public static Boolean wasChooseAccountShown(Context ctx)
    {
        File file = new File(ctx.getFilesDir(), ACCOUNT_FILE);

        return file.exists();
    }

    public static void setChooseAccountShown(Context ctx)
    {
        File file = new File(ctx.getFilesDir(), ACCOUNT_FILE);
        if(!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error creatin disclaimer file", e);
            }
    }

    public static void showMotd(Context ctx)
    {
        return;
        /*
        int versionCode = getVersion(ctx);

        File file = new File(ctx.getFilesDir(), MOTD_FILE);

        if(file.exists())
        {
            try {
                String data = readFile(file);
                int lastVersion = Integer.parseInt(data);

                if(lastVersion >= versionCode)
                    return;

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error reading MOTD file", e);
            }
        }

        View view = LayoutInflater.from(ctx).inflate(R.layout.motd_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(ctx).setView(view).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        }).create();
        WebView webView = (WebView) view.findViewById(R.id.webview);

        webView.loadUrl("http://trainmonitor.logv.ws/android/motd/" + String.valueOf(versionCode));
        dialog.show();

        try {
            writeFile(file, String.valueOf(versionCode).getBytes());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error writing MOTD file", e);
        }                      */

    }

    private static int getVersion(Context ctx) {
        try {
            return ctx.getApplicationContext().getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "can not get app verison", e);
            return 0;
        }
    }

    private static void writeFile(File file, byte[] content) throws Exception
	{		
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(content);
		fos.close();		
	}
	
	private static String readFile(File file) throws Exception
	{
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[255];
		fis.read(buffer);
		fis.close();
		return new String(buffer).trim();		
	}
	
	
}
