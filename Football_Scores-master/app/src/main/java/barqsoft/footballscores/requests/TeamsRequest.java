package barqsoft.footballscores.requests;

import android.net.Uri;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Created by Paulina on 2015-11-19.
 */
public class TeamsRequest extends FootballApiJsonObjectRequest {

    public static final String TEAMS_PATH = "teams";

    public TeamsRequest(long soccerSeasonId,
                        Response.Listener<JSONObject> listener,
                        Response.ErrorListener errorListener) {
        super(buildUrl(soccerSeasonId), listener, errorListener);
    }

    public static String buildUrl(long soccerSeasonId) {
        return Uri.parse(SoccerSeasonsRequest.buildUrl())
                .buildUpon()
                .appendPath(Long.toString(soccerSeasonId))
                .appendPath(TEAMS_PATH)
                .build()
                .toString();
    }
}
