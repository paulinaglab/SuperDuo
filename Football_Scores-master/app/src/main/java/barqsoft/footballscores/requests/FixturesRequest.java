package barqsoft.footballscores.requests;

import android.net.Uri;
import android.support.annotation.IntDef;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Created by Paulina on 2015-11-19.
 */
public class FixturesRequest extends FootballApiJsonObjectRequest {

    public static final String FIXTURES_PATH = "fixtures";
    public static final String TIME_FRAME_PARAM = "timeFrame";
    public static final String NEXT_DAYS_PARAM_VALUE = "n";
    public static final String PREVIOUS_DAYS_PARAM_VALUE = "p";

    public FixturesRequest(@TimeFrame int timeFrame,
                           int days,
                           Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener) {
        super(buildUrl(timeFrame, days), listener, errorListener);
    }

    public static final String buildUrl(@TimeFrame int timeFrame, int days) {
        Uri.Builder builder = Uri.parse(buildBaseUrl())
                .buildUpon()
                .appendPath(FIXTURES_PATH);
        if (timeFrame == INCOMING)
            builder.appendQueryParameter(TIME_FRAME_PARAM, NEXT_DAYS_PARAM_VALUE + days);
        else
            builder.appendQueryParameter(TIME_FRAME_PARAM, PREVIOUS_DAYS_PARAM_VALUE + days);
        return builder.build().toString();
    }

    @IntDef({INCOMING, PAST})
    public @interface TimeFrame {
    }

    public static final int INCOMING = 0;
    public static final int PAST = 1;
}
