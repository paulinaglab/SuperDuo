package barqsoft.footballscores.activities;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import barqsoft.footballscores.FootballScores;
import barqsoft.footballscores.R;
import barqsoft.footballscores.data.FootballScoresContract;
import barqsoft.footballscores.fragments.FirstSyncErrorDialog;
import barqsoft.footballscores.syncadapters.FootballScoresSyncAdapter;

public class LaunchActivity extends AppCompatActivity implements FirstSyncErrorDialog.Listener {

    private BroadcastReceiver syncStateBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int syncState = FootballScoresSyncAdapter.getSyncState(this);
        if (syncState != FootballScoresSyncAdapter.SYNC_NEVER_SYNCED &&
                syncState != FootballScoresSyncAdapter.SYNC_FIRST_SYNC_ERROR) {
            openMainActivity();
            return;
        }

        retrySync();

        setContentView(R.layout.activity_launch);
        syncStateBroadcastReceiver = new SyncStateBroadcastReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(syncStateBroadcastReceiver,
                new IntentFilter(FootballScoresSyncAdapter.ACTION_SYNC_STATE_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(syncStateBroadcastReceiver);
    }

    private void showErrorDialog() {
        FirstSyncErrorDialog dialog = new FirstSyncErrorDialog();
        dialog.show(getSupportFragmentManager(), FirstSyncErrorDialog.class.getSimpleName());
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void retrySync() {
        if (!isConnected()) {
            showErrorDialog();
            return;
        }

        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(FootballScores.getInstance().getAccount(),
                FootballScoresContract.AUTHORITY, settingsBundle);
    }

    @Override
    public void closeApp() {
        finish();
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    class SyncStateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int syncState = FootballScoresSyncAdapter.getSyncState(context);
            if (syncState == FootballScoresSyncAdapter.SYNC_FIRST_SYNC_ERROR)
                showErrorDialog();
            else
                openMainActivity();
        }
    }
}
