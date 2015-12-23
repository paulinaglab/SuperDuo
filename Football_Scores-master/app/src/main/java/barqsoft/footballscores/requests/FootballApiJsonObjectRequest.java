package barqsoft.footballscores.requests;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Paulina on 2015-11-19.
 */
public abstract class FootballApiJsonObjectRequest extends FootballApiRequest {

    private Response.Listener<JSONObject> mListener;

    public FootballApiJsonObjectRequest(String url,
                                        Response.Listener<JSONObject> responseListener,
                                        Response.ErrorListener errorListener) {
        super(url, errorListener);
        mListener = responseListener;
    }

    @Override
    protected void deliverResponse(String response) {
        try {
            mListener.onResponse(new JSONObject(response));
        } catch (JSONException e) {
            getErrorListener().onErrorResponse(new VolleyError(e));
            e.printStackTrace();
        }
    }
}
