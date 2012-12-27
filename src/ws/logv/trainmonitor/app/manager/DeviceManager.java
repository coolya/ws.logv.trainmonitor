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

import android.content.Context;
import com.google.android.gcm.GCMRegistrar;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.event.DeviceReadyEvent;
import ws.logv.trainmonitor.event.DeviceUpdatedEvent;
import ws.logv.trainmonitor.event.PrepareDeviceEvent;
import ws.logv.trainmonitor.event.RegisteredToGcmEvent;
import ws.logv.trainmonitor.model.Device;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public DeviceManager(Context ctx) {
        mCtx = ctx;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEvent(RegisteredToGcmEvent event)
    {
        if(sDevice == null)
              return;

        String regId = GCMRegistrar.getRegistrationId(mCtx);

        if(!(regId == null) && !regId.equals(sDevice.getGcmRegId()))
        {
            sDevice.setGcmRegId(regId);
            File file = new File(mCtx.getFilesDir(), FILENAME);

            if(file.exists())
            {
                try {
                    writeFile(file, sDevice.toString().getBytes());
                    Workflow.getEventBus(mCtx).post(new DeviceUpdatedEvent());
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEvent(PrepareDeviceEvent event)
    {
        File file = new File(mCtx.getFilesDir(), FILENAME);
        if (!file.exists()) {
            ApiClient client = new ApiClient(mCtx);
            Device device =  client.registerDevice();
            try {
                file.createNewFile();
                writeFile(file, device.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        if(file.exists())
        {
            try {
                String data = readFile(file);
                sDevice = Device.fromString(data);
                Workflow.getEventBus(mCtx).post(new DeviceReadyEvent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Device getsDevice()
    {
        return sDevice;
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
