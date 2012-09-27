package ws.logv.trainmonintor.app;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;

public class ApplicationBackupAgent extends BackupAgentHelper {

    // An arbitrary string used within the BackupAgentHelper implementation to
    // identify the SharedPreferencesBackupHelper's data.
    static final String BACKUP_KEY = "perferences";

    // Simply allocate a helper and install it
    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper =
                new SharedPreferencesBackupHelper(this, Constants.Perferences.Installation);
        addHelper(BACKUP_KEY, helper);
    }
    
    public static void requestBackup(Context ctx)
    {
    	BackupManager bm = new BackupManager(ctx);
    	bm.dataChanged();
    }
}
