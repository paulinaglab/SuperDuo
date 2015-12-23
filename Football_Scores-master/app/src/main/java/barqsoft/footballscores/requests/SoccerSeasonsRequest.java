package barqsoft.footballscores.requests;

/**
 * Created by Paulina on 2015-11-19.
 */

import android.net.Uri;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;

public class SoccerSeasonsRequest extends FootballApiRequest {

    public static final String SOCCER_SEASONS_PATH = "soccerseasons";

    private Response.Listener<JSONArray> mListener;

    public SoccerSeasonsRequest(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(buildUrl(), errorListener);
        mListener = listener;
    }

    public static String buildUrl() {
        return Uri.parse(buildBaseUrl())
                .buildUpon()
                .appendPath(SOCCER_SEASONS_PATH)
                .build()
                .toString();
    }

    @Override
    protected void deliverResponse(String response) {
        try {
            mListener.onResponse(new JSONArray(response));
        } catch (JSONException e) {
            getErrorListener().onErrorResponse(new VolleyError(e));
            e.printStackTrace();
        }
    }
}

