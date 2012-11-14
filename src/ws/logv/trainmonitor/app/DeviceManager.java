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

import android.content.Context;
import android.util.Log;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.api.IApiCallback;
import ws.logv.trainmonitor.model.Device;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.FileNameMap;
import java.util.ConcurrentModificationException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 05.10.12
 * Time: 07:14
 * To change this template use File | Settings | File Templates.
 */
public class DeviceManager {

    private final static String LOG_TAG = "DeviceManager";
    private final Context mCtx;
    private static final String FILENAME = "DEVICE";
    private static Device sDevice;
    private static final Object _lock = new Object();

    public DeviceManager(Context ctx) {
        mCtx = ctx;

        synchronized (_lock)
        {
            if(sDevice == null)
                prepare();
        }
    }

    private void prepare() {
        final File file = new File(mCtx.getFilesDir(), FILENAME);
        synchronized (file) {
            if (!file.exists()) {
                final java.util.concurrent.locks.ReentrantLock lock = new ReentrantLock();
                lock.lock();
                ApiClient client = new ApiClient(mCtx);
                client.registerDevice(new IApiCallback<Device>() {
                    @Override
                    public void onComplete(Device data) {
                        try {
                            file.createNewFile();
                            writeFile(file, data.toString().getBytes());
                            lock.unlock();
                        } catch (Exception e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }

                    @Override
                    public void onError(Throwable tr) {
                        lock.unlock();
                    }

                    @Override
                    public void onNoConnection() {
                        lock.unlock();
                    }
                });
                lock.lock();
            }
        }

        if (file.exists()) {
            try {
                String data = readFile(file);
                sDevice = Device.fromString(data);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public Device getsDevice()
    {
        return sDevice;
    }

    public void registeredToGCM(String regId) {

        if(sDevice == null)
        {
            Log.w(LOG_TAG, "DeviceManager did not prepare!");
            return;
        }

        sDevice.setGcmRegId(regId);

        ApiClient client = new ApiClient(mCtx);

        client.putDevice(sDevice);
    }

    public void unregisteredFromGCM(String regId) {

        if(sDevice == null)
            throw new IllegalStateException("The devices manager did not prepare correctly");

        if(sDevice.getGcmRegId().equals(regId))
        {
            sDevice.setGcmRegId(null);
            ApiClient client = new ApiClient(mCtx);
            client.putDevice(sDevice);
        }

    }

    private static void writeFile(File file, byte[] content) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content);
        fos.close();
    }

    private static String readFile(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[255];
        fis.read(buffer);
        fis.close();
        return new String(buffer).trim();
    }
}
