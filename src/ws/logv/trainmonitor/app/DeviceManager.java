package ws.logv.trainmonitor.app;

import android.content.Context;
import ws.logv.trainmonitor.model.Device;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 05.10.12
 * Time: 07:14
 * To change this template use File | Settings | File Templates.
 */
public class DeviceManager {

    private final Context mCtx;
    private Device mMe;

    public DeviceManager(Context ctx)
    {
         mCtx = ctx;
    }

    public void registeredToGCM(String regId)
    {

    }

    public void unregisteredFromGCM(String regId)
    {

    }
}
