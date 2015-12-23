package it.jaschke.alexandria;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Paulina on 2015-10-29.
 */
public class Alexandria extends Application {

    private static Alexandria instance;
    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        requestQueue = Volley.newRequestQueue(this);
    }

    public static Alexandria getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
