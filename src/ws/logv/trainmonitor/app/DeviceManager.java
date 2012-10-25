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

    public DeviceManager(Context ctx) {
        mCtx = ctx;

        synchronized (DeviceManager.class)
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
            throw new IllegalStateException("The devices manager did not prepare correctly");

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
