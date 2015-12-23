package barqsoft.footballscores;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.SyncRequest;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Paulina on 2015-11-19.
 */
public class FootballScores extends Application {

    public static final SimpleDateFormat DATE_DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
    public static final SimpleDateFormat API_DATE_FORMAT;

    public static final int SYNC_INTERVAL = 3600;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private static FootballScores sInstance;
    private RequestQueue mRequestQueue;

    public static FootballScores getInstance() {
        return sInstance;
    }

    static {
        API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        API_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static final String ACCOUNT_NAME = "barqsoft.footballscores.Account";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mRequestQueue = Volley.newRequestQueue(this);
        getAccount();
    }

    public Account getAccount() {
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        String accountType = getString(R.string.account_type);
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length == 0) {
            Account account = new Account(ACCOUNT_NAME, accountType);
            if (accountManager.addAccountExplicitly(account, null, null)) {
                onAccountCreated();
                return account;
            } else {
                throw new RuntimeException("Error on add account");
            }
        } else {
            return accounts[0];
        }
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    private void onAccountCreated() {
        //Sync periodically
        String authority = getString(R.string.authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(SYNC_INTERVAL, SYNC_FLEXTIME).
                    setSyncAdapter(getAccount(), authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(getAccount(),
                    authority, new Bundle(), SYNC_INTERVAL);
        }
        //Sync automatically
        ContentResolver.setSyncAutomatically(getAccount(), getString(R.string.authority), true);

        //Sync now, because it's first start
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(FootballScores.getInstance().getAccount(),
                getString(R.string.authority), settingsBundle);
    }
}
