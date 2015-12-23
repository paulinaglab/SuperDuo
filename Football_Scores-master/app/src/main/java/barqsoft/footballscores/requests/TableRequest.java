package barqsoft.footballscores.requests;

import android.net.Uri;

import com.android.volley.Response;

import org.json.JSONObject;


public class TableRequest extends FootballApiJsonObjectRequest {

    private static final String LEAGUE_TABLE_PATH = "leagueTable";

    public TableRequest(long soccerSeasonId,
                        Response.Listener<JSONObject> responseListener,
                        Response.ErrorListener errorListener) {
        super(buildUrl(soccerSeasonId), responseListener, errorListener);
    }

    public static String buildUrl(long soccerSeasonId) {
        return Uri.parse(SoccerSeasonsRequest.buildUrl())
                .buildUpon()
                .appendPath(Long.toString(soccerSeasonId))
                .appendPath(LEAGUE_TABLE_PATH)
                .build()
                .toString();
    }
}
