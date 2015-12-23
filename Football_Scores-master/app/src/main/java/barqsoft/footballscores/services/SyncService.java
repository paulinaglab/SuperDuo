package barqsoft.footballscores.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import barqsoft.footballscores.syncadapters.FootballScoresSyncAdapter;

// This class is copied from Google Developers.
public class SyncService extends Service {

    private static FootballScoresSyncAdapter sSyncAdapter;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new FootballScoresSyncAdapter(this, true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
