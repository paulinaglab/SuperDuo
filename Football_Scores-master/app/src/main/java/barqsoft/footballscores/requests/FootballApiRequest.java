package barqsoft.footballscores.requests;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import barqsoft.footballscores.ApiKey;

/**
 * Created by Paulina on 2015-11-19.
 */
public abstract class FootballApiRequest extends Request<String> {
    public static final String API_KEY_HEADER_NAME = "X-Auth-Token";
    public static final String API_AUTHORUTY = "api.football-data.org";
    public static final String API_SCHEME = "http";
    public static final String API_VERSION = "v1";

    private Map<String, String> mHeaders;

    public FootballApiRequest(String url,
                              Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mHeaders = new HashMap<>();
        mHeaders.put(API_KEY_HEADER_NAME, ApiKey.API_KEY);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new String(response.data), null);
        } catch (Exception e) {
            return Response.error(new VolleyError(e));
        }
    }

    public static String buildBaseUrl() {
        return new Uri.Builder().scheme(API_SCHEME)
                .authority(API_AUTHORUTY)
                .appendPath(API_VERSION)
                .build()
                .toString();
    }
}
