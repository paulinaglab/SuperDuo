package barqsoft.footballscores.syncadapters;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.util.Log;

import com.android.volley.ServerError;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import barqsoft.footballscores.FootballScores;
import barqsoft.footballscores.data.FootballScoresContract;
import barqsoft.footballscores.requests.FixturesRequest;
import barqsoft.footballscores.requests.SoccerSeasonsRequest;
import barqsoft.footballscores.requests.TableRequest;
import barqsoft.footballscores.requests.TeamsRequest;

/**
 * Created by Paulina on 2015-11-19.
 */
public class FootballScoresSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACTION_SYNC_STATE_CHANGED = "barqsoft.footballscores.SYNC_FINISHED_ACTION";

    private static final String LINKS_KEY = "_links";
    private static final String SELF_KEY = "self";
    private static final String CAPTION_KEY = "caption";
    private static final String LEAGUE_KEY = "league";
    private static final String YEAR_KEY = "year";
    private static final String HREF_KEY = "href";
    private static final String NAME_KEY = "name";
    private static final String CODE_KEY = "code";
    private static final String SHORT_NAME_KEY = "shortName";
    private static final String TEAMS_KEY = "teams";
    private static final String STANDING_KEY = "standing";
    private static final String FIXTURES = "fixtures";
    private static final String MATCHDAY = "matchday";
    private static final String STATUS = "status";
    private static final String RESULT = "result";
    private static final String HOME_GOALS = "goalsHomeTeam";
    private static final String AWAY_GOALS = "goalsAwayTeam";
    private static final String DATE = "date";
    private static final String CHAMPIONS_LEAGUE_ID = "CL";
    private static final String HOME_TEAM = "homeTeam";
    private static final String AWAY_TEAM = "awayTeam";
    private static final String SOCCER_SEASON = "soccerseason";
    private static final String PLAYED_GAMES_KEY = "playedGames";
    private static final String GOALS_KEY = "goals";
    private static final String GOALS_AGAINST_KEY = "goalsAgainst";
    private static final String RANK_KEY = "position";
    private static final String POINTS_KEY = "points";
    private static final String TEAM_KEY = "team";

    private static final int DAYS_TO_SYNC = 6;

    private static final int TOO_MANY_REQUESTS_CODE = 429;
    private static final int SECONDS_DELAY_ON_TOO_MANY_REQUESTS = 360;

    public static final int SYNC_NEVER_SYNCED = 0;
    public static final int SYNC_OK = 1;
    public static final int SYNC_ERROR = 3;
    public static final int SYNC_FIRST_SYNC_ERROR = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SYNC_NEVER_SYNCED, SYNC_OK, SYNC_ERROR, SYNC_FIRST_SYNC_ERROR})
    public @interface SyncState {
    }

    public FootballScoresSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle bundle,
                              String authority,
                              ContentProviderClient contentProviderClient,
                              SyncResult syncResult) {
        int syncState = getSyncState(getContext());
        try {
            Log.d(getClass().getSimpleName(), "syncing");
            ArrayList<ContentProviderOperation> batchUpdate = new ArrayList<>();
            batchUpdate.addAll(synchronizeSeasonsAndTeams(contentProviderClient));
            batchUpdate.addAll(synchronizeFixtures());
            contentProviderClient.applyBatch(batchUpdate);
            saveSyncState(getContext(), SYNC_OK);
        } catch (Exception e) {
            if (syncState == SYNC_NEVER_SYNCED || syncState == SYNC_FIRST_SYNC_ERROR) {
                saveSyncState(getContext(), SYNC_FIRST_SYNC_ERROR);
            } else {
                saveSyncState(getContext(), SYNC_ERROR);
            }
            if (e.getCause() != null && e.getCause() instanceof ServerError) {
                ServerError serverError = (ServerError) e.getCause();
                if (serverError.networkResponse != null) {
                    if (serverError.networkResponse.statusCode == TOO_MANY_REQUESTS_CODE) {
                        syncResult.delayUntil = SECONDS_DELAY_ON_TOO_MANY_REQUESTS;
                        syncResult.stats.numIoExceptions++;
                        Log.d("Sync error", "Sync error, too many requests");
                    }
                }
            }
            e.printStackTrace();
        }
        getContext().sendBroadcast(new Intent(ACTION_SYNC_STATE_CHANGED));
    }

    private List<ContentProviderOperation> synchronizeSeasonsAndTeams(ContentProviderClient contentProviderClient)
            throws ExecutionException, InterruptedException, RemoteException, JSONException {
        List<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        RequestFuture<JSONArray> requestFuture = RequestFuture.newFuture();
        SoccerSeasonsRequest soccerSeasonsRequest = new SoccerSeasonsRequest(requestFuture, requestFuture);
        requestFuture.setRequest(FootballScores.getInstance().getRequestQueue().add(soccerSeasonsRequest));
        JSONArray soccerSeasons = requestFuture.get();
        for (int i = 0; i < soccerSeasons.length(); i++) {
            long leagueId = parseId(soccerSeasons.getJSONObject(i));
            ContentValues seasonsContentValues = parseSeason(soccerSeasons.getJSONObject(i));
            if (isSeasonInDatabase(contentProviderClient, leagueId)) {
                contentProviderOperations.add(
                        ContentProviderOperation
                                .newUpdate(FootballScoresContract.SoccerSeasonsEntry.CONTENT_URI)
                                .withValues(seasonsContentValues)
                                .withSelection(FootballScoresContract.SoccerSeasonsEntry._ID + "=?",
                                        new String[]{Long.toString(leagueId)}).build());
            } else {
                contentProviderOperations.add(
                        ContentProviderOperation
                                .newInsert(FootballScoresContract.SoccerSeasonsEntry.CONTENT_URI)
                                .withValues(seasonsContentValues)
                                .build()
                );
            }

            contentProviderOperations.addAll(synchronizeTeams(contentProviderClient, leagueId));
            if (!soccerSeasons.getJSONObject(i).getString(LEAGUE_KEY).equals(CHAMPIONS_LEAGUE_ID)) {
                contentProviderOperations.addAll(synchronizeTables(leagueId));
            }
        }
        return contentProviderOperations;
    }

    private List<ContentProviderOperation> synchronizeTeams(ContentProviderClient contentProviderClient, long leagueId)
            throws ExecutionException, InterruptedException, JSONException, RemoteException {
        List<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        TeamsRequest teamsRequest = new TeamsRequest(
                leagueId,
                future,
                future);
        future.setRequest(FootballScores.getInstance().getRequestQueue().add(teamsRequest));
        JSONObject teams = future.get();
        JSONArray teamsArray = teams.getJSONArray(TEAMS_KEY);

        contentProviderOperations.add(
                ContentProviderOperation
                        .newDelete(FootballScoresContract.LeagueTeamsEntry.CONTENT_URI)
                        .withSelection(FootballScoresContract.LeagueTeamsEntry.LEAGUE_ID_COLUMN_NAME + "=?",
                                new String[]{Long.toString(leagueId)})
                        .build());

        for (int j = 0; j < teamsArray.length(); j++) {
            ContentValues teamContentValues = parseTeam(teamsArray.getJSONObject(j));
            contentProviderOperations.add(
                    addOrUpdateTeam(
                            contentProviderClient,
                            teamContentValues.getAsLong(FootballScoresContract.TeamsEntry._ID),
                            teamContentValues));

            ContentValues contentValues = new ContentValues();
            contentValues.put(FootballScoresContract.LeagueTeamsEntry.LEAGUE_ID_COLUMN_NAME, leagueId);
            contentValues.put(FootballScoresContract.LeagueTeamsEntry.TEAM_ID_COLUMN_NAME, teamContentValues.getAsLong(FootballScoresContract.TeamsEntry._ID));
            contentProviderOperations.add(
                    ContentProviderOperation
                            .newInsert(FootballScoresContract.LeagueTeamsEntry.CONTENT_URI)
                            .withValues(contentValues)
                            .build()
            );
        }


        return contentProviderOperations;
    }

    private List<ContentProviderOperation> synchronizeTables(long leagueId)
            throws ExecutionException, InterruptedException, JSONException, RemoteException {
        List<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        TableRequest tableRequest = new TableRequest(
                leagueId,
                future,
                future);
        future.setRequest(FootballScores.getInstance().getRequestQueue().add(tableRequest));
        JSONObject tableObject = future.get();
        JSONArray rowsArray = tableObject.getJSONArray(STANDING_KEY);
        List<ContentValues> tableRows = new ArrayList<>();
        for (int i = 0; i < rowsArray.length(); i++) {
            tableRows.add(parseTableRow(rowsArray.getJSONObject(i), leagueId));
        }
        contentProviderOperations.addAll(updateTable(tableRows, leagueId));
        return contentProviderOperations;
    }

    private List<ContentProviderOperation> synchronizeFixtures()
            throws ExecutionException, InterruptedException, RemoteException, JSONException {
        List<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        List<ContentValues> fixtures = fetchFixtures(FixturesRequest.PAST);
        fixtures.addAll(fetchFixtures(FixturesRequest.INCOMING));

        contentProviderOperations.add(
                ContentProviderOperation.newDelete(FootballScoresContract.FixturesEntry.CONTENT_URI)
                        .build());
        for (ContentValues contentValues : fixtures) {
            contentProviderOperations.add(
                    addFixture(contentValues));
        }
        return contentProviderOperations;
    }

    private List<ContentValues> fetchFixtures(@FixturesRequest.TimeFrame int timeFrame)
            throws ExecutionException, InterruptedException, JSONException {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        FixturesRequest fixturesRequest = new FixturesRequest(timeFrame, DAYS_TO_SYNC, future,
                future);
        future.setRequest(FootballScores.getInstance().getRequestQueue().add(fixturesRequest));
        JSONObject fixturesObject = future.get();
        JSONArray fixturesArray = fixturesObject.getJSONArray(FIXTURES);

        List<ContentValues> contentValuesList = new ArrayList<>();
        for (int i = 0; i < fixturesArray.length(); i++) {
            JSONObject fixtureObject = fixturesArray.getJSONObject(i);
            ContentValues contentValues = parseFixture(fixtureObject);
            contentValuesList.add(contentValues);
        }
        return contentValuesList;
    }

    private boolean isSeasonInDatabase(ContentProviderClient contentProviderClient, long leagueId)
            throws RemoteException {
        Cursor cursor = contentProviderClient.query(FootballScoresContract.SoccerSeasonsEntry.CONTENT_URI,
                new String[]{FootballScoresContract.SoccerSeasonsEntry._ID},
                FootballScoresContract.SoccerSeasonsEntry._ID + "=?",
                new String[]{Long.toString(leagueId)},
                null);
        boolean present = false;
        if (cursor.moveToFirst()) {
            present = true;
        }
        cursor.close();
        return present;
    }

    private List<ContentProviderOperation> updateTable(List<ContentValues> contentValues,
                                                       long leagueId) {
        List<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        contentProviderOperations.add(
                ContentProviderOperation
                        .newDelete(FootballScoresContract.FootballTableRowsEntry.CONTENT_URI)
                        .withSelection(
                                FootballScoresContract.FootballTableRowsEntry.LEAGUE_ID_COLUMN_NAME + "=?",
                                new String[]{Long.toString(leagueId)})
                        .build());
        for (ContentValues cv : contentValues) {
            contentProviderOperations.add(
                    ContentProviderOperation
                            .newInsert(FootballScoresContract.FootballTableRowsEntry.CONTENT_URI)
                            .withValues(cv)
                            .build());
        }
        return contentProviderOperations;
    }

    private ContentProviderOperation addFixture(ContentValues contentValues) {
        return ContentProviderOperation
                .newInsert(FootballScoresContract.FixturesEntry.CONTENT_URI)
                .withValues(contentValues)
                .build();
    }

    private ContentProviderOperation addOrUpdateTeam(ContentProviderClient contentProviderClient,
                                                     long teamId,
                                                     ContentValues contentValues) throws RemoteException {
        ContentProviderOperation contentProviderOperation;
        Cursor cursor = contentProviderClient.query(
                FootballScoresContract.TeamsEntry.CONTENT_URI,
                new String[]{FootballScoresContract.TeamsEntry._ID},
                FootballScoresContract.TeamsEntry._ID + "=?",
                new String[]{Long.toString(teamId)},
                null);
        if (cursor.moveToFirst()) {
            contentProviderOperation = ContentProviderOperation
                    .newUpdate(FootballScoresContract.TeamsEntry.CONTENT_URI)
                    .withValues(contentValues)
                    .withSelection(
                            FootballScoresContract.TeamsEntry._ID + "=?",
                            new String[]{Long.toString(teamId)}).build();
        } else {
            contentProviderOperation = ContentProviderOperation
                    .newInsert(FootballScoresContract.TeamsEntry.CONTENT_URI)
                    .withValues(contentValues).build();
        }
        cursor.close();
        return contentProviderOperation;
    }

    private long parseId(JSONObject jsonObject) throws JSONException {
        String selfLink = jsonObject.getJSONObject(LINKS_KEY).getJSONObject(SELF_KEY).getString(HREF_KEY);
        return Long.parseLong(Uri.parse(selfLink).getLastPathSegment());
    }

    private ContentValues parseSeason(JSONObject jsonObject) throws JSONException {
        ContentValues contentValues = new ContentValues();
        long _id = parseId(jsonObject);

        contentValues.put(FootballScoresContract.SoccerSeasonsEntry._ID, _id);
        contentValues.put(FootballScoresContract.SoccerSeasonsEntry.CAPTION_COLUMN_NAME, jsonObject.getString(CAPTION_KEY));
        contentValues.put(FootballScoresContract.SoccerSeasonsEntry.LEAGUE_COLUMN_NAME, jsonObject.getString(LEAGUE_KEY));
        contentValues.put(FootballScoresContract.SoccerSeasonsEntry.YEAR_COLUMN_NAME, jsonObject.getString(YEAR_KEY));

        return contentValues;
    }

    private ContentValues parseTeam(JSONObject teamObject) throws JSONException {
        ContentValues contentValues = new ContentValues();
        long _id = parseId(teamObject);

        contentValues.put(FootballScoresContract.TeamsEntry._ID, _id);
        contentValues.put(FootballScoresContract.TeamsEntry.CODE_COLUMN_NAME, teamObject.optString(CODE_KEY));
        contentValues.put(FootballScoresContract.TeamsEntry.NAME_COLUMN_NAME, teamObject.optString(NAME_KEY));
        String shortName = teamObject.optString(SHORT_NAME_KEY);
        if (shortName.equals("null"))
            shortName = "";
        contentValues.put(FootballScoresContract.TeamsEntry.SHORT_NAME_COLUMN_NAME, shortName);

        return contentValues;
    }

    private ContentValues parseFixture(JSONObject fixtureObject) throws JSONException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FootballScoresContract.FixturesEntry._ID, parseId(fixtureObject));
        contentValues.put(FootballScoresContract.FixturesEntry.MATCHDAY_COLUMN_NAME, fixtureObject.optInt(MATCHDAY));
        contentValues.put(FootballScoresContract.FixturesEntry.STATUS_COLUMN_NAME, fixtureObject.optString(STATUS));

        JSONObject resultObject = fixtureObject.getJSONObject(RESULT);
        contentValues.put(FootballScoresContract.FixturesEntry.AWAY_GOALS_COLUMN_NAME, resultObject.optInt(AWAY_GOALS, -1));
        contentValues.put(FootballScoresContract.FixturesEntry.HOME_GOALS_COLUMN_NAME, resultObject.optInt(HOME_GOALS, -1));
        contentValues.put(FootballScoresContract.FixturesEntry.DATE_COLUMN_NAME, fixtureObject.getString(DATE));

        JSONObject linksObject = fixtureObject.getJSONObject(LINKS_KEY);
        long homeId = Long.parseLong(Uri.parse(linksObject.getJSONObject(HOME_TEAM).getString(HREF_KEY)).getLastPathSegment());
        contentValues.put(FootballScoresContract.FixturesEntry.HOME_TEAM_ID_COLUMN_NAME, homeId);
        long awayId = Long.parseLong(Uri.parse(linksObject.getJSONObject(AWAY_TEAM).getString(HREF_KEY)).getLastPathSegment());
        contentValues.put(FootballScoresContract.FixturesEntry.AWAY_TEAM_ID_COLUMN_NAME, awayId);
        long seasonId = Long.parseLong(Uri.parse(linksObject.getJSONObject(SOCCER_SEASON).getString(HREF_KEY)).getLastPathSegment());
        contentValues.put(FootballScoresContract.FixturesEntry.SOCCER_SEASON_ID_COLUMN_NAME, seasonId);
        return contentValues;
    }

    private ContentValues parseTableRow(JSONObject rowObject, long leagueId) throws JSONException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FootballScoresContract.FootballTableRowsEntry.GAMES_PLAYED_COLUMN_NAME, rowObject.getInt(PLAYED_GAMES_KEY));
        contentValues.put(FootballScoresContract.FootballTableRowsEntry.LEAGUE_ID_COLUMN_NAME, leagueId);
        contentValues.put(FootballScoresContract.FootballTableRowsEntry.GOALS_LOST_COLUMN_NAME, rowObject.getInt(GOALS_AGAINST_KEY));
        contentValues.put(FootballScoresContract.FootballTableRowsEntry.GOALS_SCORED_COLUMN_NAME, rowObject.getInt(GOALS_KEY));
        contentValues.put(FootballScoresContract.FootballTableRowsEntry.ORDER_COLUMN_NAME, rowObject.getInt(RANK_KEY));

        String selfLink = rowObject.getJSONObject(LINKS_KEY).getJSONObject(TEAM_KEY).getString(HREF_KEY);
        long teamId = Long.parseLong(Uri.parse(selfLink).getLastPathSegment());
        contentValues.put(FootballScoresContract.FootballTableRowsEntry.TEAM_ID_COLUMN_NAME, teamId);

        contentValues.put(FootballScoresContract.FootballTableRowsEntry.POINTS_COLUMN_NAME, rowObject.getInt(POINTS_KEY));

        return contentValues;
    }

    @SyncState
    public static int getSyncState(Context context) {
        Cursor c = context.getContentResolver()
                .query(FootballScoresContract.SyncStateEntry.CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        int idx = c.getColumnIndex(FootballScoresContract.SyncStateEntry.SYNC_STATE_COLUMN_NAME);
        int result = c.getInt(idx);

        c.close();

        return result;
    }

    public static void saveSyncState(Context context, @SyncState int syncState) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FootballScoresContract.SyncStateEntry.SYNC_STATE_COLUMN_NAME, syncState);
        context.getContentResolver().insert(FootballScoresContract.SyncStateEntry.CONTENT_URI, contentValues);
        Log.d("SyncStateChange", "New state:" + getSyncState(context));
    }
}

