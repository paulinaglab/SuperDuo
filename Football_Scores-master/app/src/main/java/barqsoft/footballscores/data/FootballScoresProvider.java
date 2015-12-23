package barqsoft.footballscores.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;

import barqsoft.footballscores.syncadapters.FootballScoresSyncAdapter;

/**
 * Created by Paulina on 2015-11-19.
 */
public class FootballScoresProvider extends ContentProvider {

    private static final int FIXTURES_CODE = 100;
    private static final int FULL_FIXTURES_CODE = 150;
    private static final int LEAGUE_INCOMING_FIXTURES_CODE = 151;
    private static final int LEAGUE_RECENT_FIXTURES_CODE = 152;
    private static final int TEAM_INCOMING_FIXTURES_CODE = 153;
    private static final int TEAM_RECENT_FIXTURES_CODE = 154;
    private static final int TEAMS_CODE = 200;
    private static final int TEAMS_FOR_LEAGUE_CODE = 201;
    private static final int SOCCER_SEASONS_CODE = 300;
    private static final int FOOTBALL_TABLE_ROW_CODE = 400;
    private static final int LEAGUE_FULL_TABLE_ROW_CODE = 401;
    private static final int TEAM_FULL_TABLE_ROW_CODE = 402;
    private static final int LEAGUE_TEAMS_CODE = 500;
    private static final int SYNC_STATE_CODE = 600;
    private static final String LEAGUE_INCOMING_FULL_FIXTURES_QUERY;
    private static final String LEAGUE_RECENT_FULL_FIXTURES_QUERY;
    private static final String FULL_FIXTURES_QUERY;
    private static final String FULL_TABLE_ROWS_QUERY;
    private static final String LEAGUE_FULL_TABLE_ROWS_QUERY;
    private static final String TEAM_FULL_TABLE_ROWS_QUERY;
    private static final String TEAM_INCOMING_FIXTURES_QUERY;
    private static final String TEAM_RECENT_FIXTURES_QUERY;
    private static final String TEAMS_FOR_LEAGUE_QUERY;
    private FootballScoresDbHelper dbHelper;
    private SharedPreferences preferences;
    private UriMatcher sUriMatcher = buildUriMatcher();

    static {
        FULL_FIXTURES_QUERY = "SELECT f." + FootballScoresContract.FixturesEntry._ID
                + ", f." + FootballScoresContract.FixturesEntry.MATCHDAY_COLUMN_NAME
                + ", f." + FootballScoresContract.FixturesEntry.HOME_GOALS_COLUMN_NAME
                + ", f." + FootballScoresContract.FixturesEntry.AWAY_GOALS_COLUMN_NAME
                + ", f." + FootballScoresContract.FixturesEntry.DATE_COLUMN_NAME
                + ", f." + FootballScoresContract.FixturesEntry.AWAY_TEAM_ID_COLUMN_NAME
                + ", f." + FootballScoresContract.FixturesEntry.HOME_TEAM_ID_COLUMN_NAME
                + ", f." + FootballScoresContract.FixturesEntry.SOCCER_SEASON_ID_COLUMN_NAME
                + ", at." + FootballScoresContract.TeamsEntry.NAME_COLUMN_NAME + " AS " + FootballScoresContract.FixturesEntry.FULL_FIXTURE_AWAY_TEAM_NAME
                + ", at." + FootballScoresContract.TeamsEntry.SHORT_NAME_COLUMN_NAME + " AS " + FootballScoresContract.FixturesEntry.FULL_FIXTURE_AWAY_TEAM_SHORT_NAME
                + ", at." + FootballScoresContract.TeamsEntry.CODE_COLUMN_NAME + " AS " + FootballScoresContract.FixturesEntry.FULL_FIXTURE_AWAY_TEAM_CODE
                + ", at." + FootballScoresContract.TeamsEntry.SELECTED_COLUMN_NAME + " AS " + FootballScoresContract.FixturesEntry.FULL_FIXTURE_AWAY_TEAM_SELECTED
                + ", ht." + FootballScoresContract.TeamsEntry.NAME_COLUMN_NAME + " AS " + FootballScoresContract.FixturesEntry.FULL_FIXTURE_HOME_TEAM_NAME
                + ", ht." + FootballScoresContract.TeamsEntry.SHORT_NAME_COLUMN_NAME + " AS " + FootballScoresContract.FixturesEntry.FULL_FIXTURE_HOME_TEAM_SHORT_NAME
                + ", ht." + FootballScoresContract.TeamsEntry.CODE_COLUMN_NAME + " AS " + FootballScoresContract.FixturesEntry.FULL_FIXTURE_HOME_TEAM_CODE
                + ", ht." + FootballScoresContract.TeamsEntry.SELECTED_COLUMN_NAME + " AS " + FootballScoresContract.FixturesEntry.FULL_FIXTURE_HOME_TEAM_SELECTED
                + ", ss." + FootballScoresContract.SoccerSeasonsEntry.CAPTION_COLUMN_NAME + " AS " + FootballScoresContract.FixturesEntry.FULL_FIXTURE_LEAGUE_NAME
                + " FROM " + FootballScoresContract.FixturesEntry.TABLE_NAME + " f JOIN " + FootballScoresContract.TeamsEntry.TABLE_NAME + " at ON "
                + "f." + FootballScoresContract.FixturesEntry.AWAY_TEAM_ID_COLUMN_NAME + "=at." + FootballScoresContract.TeamsEntry._ID
                + " JOIN " + FootballScoresContract.TeamsEntry.TABLE_NAME + " ht ON " + " f." + FootballScoresContract.FixturesEntry.HOME_TEAM_ID_COLUMN_NAME
                + "=ht." + FootballScoresContract.TeamsEntry._ID + " JOIN " + FootballScoresContract.SoccerSeasonsEntry.TABLE_NAME
                + " ss ON ss." + FootballScoresContract.SoccerSeasonsEntry._ID + "=f." + FootballScoresContract.FixturesEntry.SOCCER_SEASON_ID_COLUMN_NAME;

        FULL_TABLE_ROWS_QUERY = "SELECT tr." + FootballScoresContract.FootballTableRowsEntry._ID
                + ", tr." + FootballScoresContract.FootballTableRowsEntry.GOALS_LOST_COLUMN_NAME
                + ", tr." + FootballScoresContract.FootballTableRowsEntry.GOALS_SCORED_COLUMN_NAME
                + ", tr." + FootballScoresContract.FootballTableRowsEntry.POINTS_COLUMN_NAME
                + ", tr." + FootballScoresContract.FootballTableRowsEntry.GAMES_PLAYED_COLUMN_NAME
                + ", tr." + FootballScoresContract.FootballTableRowsEntry.ORDER_COLUMN_NAME
                + ", tr." + FootballScoresContract.FootballTableRowsEntry.TEAM_ID_COLUMN_NAME
                + ", tr." + FootballScoresContract.FootballTableRowsEntry.LEAGUE_ID_COLUMN_NAME
                + ", t." + FootballScoresContract.TeamsEntry.CODE_COLUMN_NAME + " AS " + FootballScoresContract.FootballTableRowsEntry.FULL_TABLE_ROW_TEAM_CODE
                + ", t." + FootballScoresContract.TeamsEntry.NAME_COLUMN_NAME + " AS " + FootballScoresContract.FootballTableRowsEntry.FULL_TABLE_ROW_TEAM_NAME
                + ", t." + FootballScoresContract.TeamsEntry.SHORT_NAME_COLUMN_NAME + " AS " + FootballScoresContract.FootballTableRowsEntry.FULL_TABLE_ROW_TEAM_SHORT_NAME
                + ", t." + FootballScoresContract.TeamsEntry.SELECTED_COLUMN_NAME + " AS " + FootballScoresContract.FootballTableRowsEntry.FULL_TABLE_ROW_TEAM_SELECTED
                + " FROM " + FootballScoresContract.FootballTableRowsEntry.TABLE_NAME + " tr JOIN " + FootballScoresContract.TeamsEntry.TABLE_NAME + " t ON "
                + "tr." + FootballScoresContract.FootballTableRowsEntry.TEAM_ID_COLUMN_NAME + "=t." + FootballScoresContract.TeamsEntry._ID;

        TEAMS_FOR_LEAGUE_QUERY = "SELECT t." + FootballScoresContract.TeamsEntry._ID
                + ", t." + FootballScoresContract.TeamsEntry.NAME_COLUMN_NAME
                + ", t." + FootballScoresContract.TeamsEntry.CODE_COLUMN_NAME
                + ", t." + FootballScoresContract.TeamsEntry.SHORT_NAME_COLUMN_NAME
                + ", t." + FootballScoresContract.TeamsEntry.SELECTED_COLUMN_NAME
                + " FROM " + FootballScoresContract.TeamsEntry.TABLE_NAME
                + " t JOIN " + FootballScoresContract.LeagueTeamsEntry.TABLE_NAME + " lte ON t."
                + FootballScoresContract.TeamsEntry._ID + "=lte." + FootballScoresContract.LeagueTeamsEntry.TEAM_ID_COLUMN_NAME
                + " JOIN " + FootballScoresContract.SoccerSeasonsEntry.TABLE_NAME + " sse ON sse."
                + FootballScoresContract.SoccerSeasonsEntry._ID + "=lte." + FootballScoresContract.LeagueTeamsEntry.LEAGUE_ID_COLUMN_NAME
                + " WHERE sse." + FootballScoresContract.SoccerSeasonsEntry._ID + "=? ORDER BY " + FootballScoresContract.TeamsEntry.SHORT_NAME_COLUMN_NAME;

        LEAGUE_FULL_TABLE_ROWS_QUERY = FULL_TABLE_ROWS_QUERY + " WHERE tr." + FootballScoresContract.FootballTableRowsEntry.LEAGUE_ID_COLUMN_NAME + "=?";
        TEAM_FULL_TABLE_ROWS_QUERY = FULL_TABLE_ROWS_QUERY
                + " WHERE tr." + FootballScoresContract.FootballTableRowsEntry.LEAGUE_ID_COLUMN_NAME
                + " IN (SELECT DISTINCT tr2." + FootballScoresContract.FootballTableRowsEntry.LEAGUE_ID_COLUMN_NAME
                + " FROM " + FootballScoresContract.FootballTableRowsEntry.TABLE_NAME + " tr2 WHERE tr2."
                + FootballScoresContract.FootballTableRowsEntry.TEAM_ID_COLUMN_NAME + "=?)";

        LEAGUE_INCOMING_FULL_FIXTURES_QUERY = FULL_FIXTURES_QUERY + " WHERE f." + FootballScoresContract.FixturesEntry.HOME_GOALS_COLUMN_NAME + "= -1 AND " + FootballScoresContract.FixturesEntry.SOCCER_SEASON_ID_COLUMN_NAME + "=? ORDER BY f." + FootballScoresContract.FixturesEntry.DATE_COLUMN_NAME;
        LEAGUE_RECENT_FULL_FIXTURES_QUERY = FULL_FIXTURES_QUERY + " WHERE f." + FootballScoresContract.FixturesEntry.HOME_GOALS_COLUMN_NAME + "<> -1 AND " + FootballScoresContract.FixturesEntry.SOCCER_SEASON_ID_COLUMN_NAME + "=? ORDER BY f." + FootballScoresContract.FixturesEntry.DATE_COLUMN_NAME + " DESC";

        TEAM_INCOMING_FIXTURES_QUERY = FULL_FIXTURES_QUERY + " WHERE f." + FootballScoresContract.FixturesEntry.HOME_GOALS_COLUMN_NAME + "= -1 AND (" + FootballScoresContract.FixturesEntry.HOME_TEAM_ID_COLUMN_NAME + "=? OR " + FootballScoresContract.FixturesEntry.AWAY_TEAM_ID_COLUMN_NAME + "=?)  ORDER BY f." + FootballScoresContract.FixturesEntry.DATE_COLUMN_NAME + " LIMIT 1";
        TEAM_RECENT_FIXTURES_QUERY = FULL_FIXTURES_QUERY + " WHERE f." + FootballScoresContract.FixturesEntry.HOME_GOALS_COLUMN_NAME + "<> -1 AND (" + FootballScoresContract.FixturesEntry.HOME_TEAM_ID_COLUMN_NAME + "=? OR " + FootballScoresContract.FixturesEntry.AWAY_TEAM_ID_COLUMN_NAME + "=?) ORDER BY f." + FootballScoresContract.FixturesEntry.DATE_COLUMN_NAME + " DESC LIMIT 1";
    }

    @Override
    public boolean onCreate() {
        dbHelper = new FootballScoresDbHelper(getContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        int code = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (code) {
            case SOCCER_SEASONS_CODE:
                retCursor = dbHelper.getReadableDatabase().query(FootballScoresContract.SoccerSeasonsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TEAMS_CODE:
                retCursor = dbHelper.getReadableDatabase().query(FootballScoresContract.TeamsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FIXTURES_CODE:
                retCursor = dbHelper.getReadableDatabase().query(FootballScoresContract.FixturesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FULL_FIXTURES_CODE:
                retCursor = dbHelper.getReadableDatabase().rawQuery(FULL_FIXTURES_QUERY, null);
                break;
            case LEAGUE_RECENT_FIXTURES_CODE:
                retCursor = dbHelper.getReadableDatabase().rawQuery(LEAGUE_RECENT_FULL_FIXTURES_QUERY, new String[]{uri.getLastPathSegment()});
                break;
            case LEAGUE_INCOMING_FIXTURES_CODE:
                retCursor = dbHelper.getReadableDatabase().rawQuery(LEAGUE_INCOMING_FULL_FIXTURES_QUERY, new String[]{uri.getLastPathSegment()});
                break;
            case TEAM_INCOMING_FIXTURES_CODE:
                retCursor = dbHelper.getReadableDatabase().rawQuery(TEAM_INCOMING_FIXTURES_QUERY,
                        new String[]{uri.getLastPathSegment(), uri.getLastPathSegment()});
                break;
            case TEAM_RECENT_FIXTURES_CODE:
                retCursor = dbHelper.getReadableDatabase().rawQuery(TEAM_RECENT_FIXTURES_QUERY,
                        new String[]{uri.getLastPathSegment(), uri.getLastPathSegment()});
                break;
            case FOOTBALL_TABLE_ROW_CODE:
                retCursor = dbHelper.getReadableDatabase().query(FootballScoresContract.FootballTableRowsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TEAM_FULL_TABLE_ROW_CODE:
                retCursor = dbHelper.getReadableDatabase().rawQuery(
                        TEAM_FULL_TABLE_ROWS_QUERY, new String[]{uri.getLastPathSegment()});
                break;
            case LEAGUE_FULL_TABLE_ROW_CODE:
                retCursor = dbHelper.getReadableDatabase().rawQuery(
                        LEAGUE_FULL_TABLE_ROWS_QUERY, new String[]{uri.getLastPathSegment()});
                break;
            case LEAGUE_TEAMS_CODE:
                retCursor = dbHelper.getReadableDatabase().query(FootballScoresContract.LeagueTeamsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TEAMS_FOR_LEAGUE_CODE:
                retCursor = dbHelper.getReadableDatabase().rawQuery(
                        TEAMS_FOR_LEAGUE_QUERY, new String[]{uri.getLastPathSegment()}
                );
                break;
            case SYNC_STATE_CODE:
                MatrixCursor mc = new MatrixCursor(new String[]{FootballScoresContract.SyncStateEntry.SYNC_STATE_COLUMN_NAME});
                int syncState = preferences.getInt(
                        FootballScoresContract.SyncStateEntry.SYNC_STATE_PREFERENCE_NAME,
                        FootballScoresSyncAdapter.SYNC_NEVER_SYNCED);
                mc.addRow(new Object[]{syncState});
                retCursor = mc;
                break;
            default:
                throw new RuntimeException("Uri not supported");
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case SOCCER_SEASONS_CODE:
                return FootballScoresContract.SoccerSeasonsEntry.CONTENT_TYPE;
            case TEAMS_CODE:
            case TEAMS_FOR_LEAGUE_CODE:
                return FootballScoresContract.TeamsEntry.CONTENT_TYPE;
            case FIXTURES_CODE:
                return FootballScoresContract.FixturesEntry.CONTENT_TYPE;
            case FULL_FIXTURES_CODE:
            case LEAGUE_INCOMING_FIXTURES_CODE:
            case LEAGUE_RECENT_FIXTURES_CODE:
            case TEAM_RECENT_FIXTURES_CODE:
            case TEAM_INCOMING_FIXTURES_CODE:
                return FootballScoresContract.FixturesEntry.FULL_FIXTURES_CONTENT_TYPE;
            case FOOTBALL_TABLE_ROW_CODE:
                return FootballScoresContract.FootballTableRowsEntry.CONTENT_TYPE;
            case LEAGUE_FULL_TABLE_ROW_CODE:
            case TEAM_FULL_TABLE_ROW_CODE:
                return FootballScoresContract.FootballTableRowsEntry.FULL_TABLE_ROW_CONTENT_TYPE;
            case LEAGUE_TEAMS_CODE:
                return FootballScoresContract.LeagueTeamsEntry.CONTENT_TYPE;
            default:
                throw new RuntimeException("Unsupported Uri");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri retUri = null;
        switch (sUriMatcher.match(uri)) {
            case SOCCER_SEASONS_CODE:
                retUri = insert(FootballScoresContract.SoccerSeasonsEntry.TABLE_NAME,
                        FootballScoresContract.SoccerSeasonsEntry.CONTENT_URI, contentValues);
                break;
            case TEAMS_CODE:
                retUri = insert(FootballScoresContract.TeamsEntry.TABLE_NAME,
                        FootballScoresContract.TeamsEntry.CONTENT_URI, contentValues);
                break;
            case FIXTURES_CODE:
                retUri = insert(FootballScoresContract.FixturesEntry.TABLE_NAME,
                        FootballScoresContract.FixturesEntry.CONTENT_URI, contentValues);
                break;
            case FOOTBALL_TABLE_ROW_CODE:
                retUri = insert(FootballScoresContract.FootballTableRowsEntry.TABLE_NAME,
                        FootballScoresContract.FootballTableRowsEntry.CONTENT_URI, contentValues);
                break;
            case LEAGUE_TEAMS_CODE:
                retUri = insert(FootballScoresContract.LeagueTeamsEntry.TABLE_NAME,
                        FootballScoresContract.LeagueTeamsEntry.CONTENT_URI, contentValues);
                break;
            case SYNC_STATE_CODE:
                int syncState = contentValues.getAsInteger(FootballScoresContract.SyncStateEntry.SYNC_STATE_COLUMN_NAME);
                preferences
                        .edit()
                        .putInt(FootballScoresContract.SyncStateEntry.SYNC_STATE_PREFERENCE_NAME,
                                syncState)
                        .commit();
                retUri = FootballScoresContract.SyncStateEntry.CONTENT_URI;
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    private Uri insert(String tableName, Uri contentUri, ContentValues contentValues) {
        long _id = dbHelper.getWritableDatabase().insertWithOnConflict(
                tableName,
                null,
                contentValues,
                SQLiteDatabase.CONFLICT_IGNORE);
        return ContentUris.withAppendedId(contentUri, _id);
    }

    @Override
    public int delete(Uri uri,
                      String selection,
                      String[] selectionArgs) {
        int count;
        switch (sUriMatcher.match(uri)) {
            case SOCCER_SEASONS_CODE:
                count = dbHelper.getWritableDatabase().delete(
                        FootballScoresContract.SoccerSeasonsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TEAMS_CODE:
                count = dbHelper.getWritableDatabase().delete(
                        FootballScoresContract.TeamsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FIXTURES_CODE:
                count = dbHelper.getWritableDatabase().delete(
                        FootballScoresContract.FixturesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FOOTBALL_TABLE_ROW_CODE:
                count = dbHelper.getWritableDatabase().delete(
                        FootballScoresContract.FootballTableRowsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LEAGUE_TEAMS_CODE:
                count = dbHelper.getWritableDatabase().delete(
                        FootballScoresContract.LeagueTeamsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new RuntimeException("Uri not supported");
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri,
                      ContentValues contentValues,
                      String selection,
                      String[] selectionArgs) {
        int count;
        switch (sUriMatcher.match(uri)) {
            case SOCCER_SEASONS_CODE:
                count = dbHelper.getWritableDatabase().update(
                        FootballScoresContract.SoccerSeasonsEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case TEAMS_CODE:
                count = dbHelper.getWritableDatabase().update(
                        FootballScoresContract.TeamsEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case FIXTURES_CODE:
                count = dbHelper.getWritableDatabase().update(
                        FootballScoresContract.FixturesEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case FOOTBALL_TABLE_ROW_CODE:
                count = dbHelper.getWritableDatabase().update(
                        FootballScoresContract.FootballTableRowsEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            case TEAMS_FOR_LEAGUE_CODE:
                count = dbHelper.getWritableDatabase().update(
                        FootballScoresContract.LeagueTeamsEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new RuntimeException("Uri not supported.");
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.SOCCER_SEASONS_PATH, SOCCER_SEASONS_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.TEAMS_PATH, TEAMS_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.TEAMS_PATH + "/" + FootballScoresContract.SoccerSeasonsEntry.TABLE_NAME + "/#", TEAMS_FOR_LEAGUE_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.FIXTURES_PATH, FIXTURES_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.FIXTURES_PATH + "/" + FootballScoresContract.FULL_FIXTURES_PATH, FULL_FIXTURES_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.FIXTURES_PATH + "/" + FootballScoresContract.FULL_FIXTURES_PATH + "/" + FootballScoresContract.INCOMING_PATH + "/" + FootballScoresContract.SOCCER_SEASONS_PATH + "/#", LEAGUE_INCOMING_FIXTURES_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.FIXTURES_PATH + "/" + FootballScoresContract.FULL_FIXTURES_PATH + "/" + FootballScoresContract.RECENT_PATH + "/" + FootballScoresContract.SOCCER_SEASONS_PATH + "/#", LEAGUE_RECENT_FIXTURES_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.FOOTBALL_TABLE_ROW_PATH, FOOTBALL_TABLE_ROW_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.FOOTBALL_TABLE_ROW_PATH + "/" + FootballScoresContract.SOCCER_SEASONS_PATH + "/#", LEAGUE_FULL_TABLE_ROW_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.FOOTBALL_TABLE_ROW_PATH + "/" + FootballScoresContract.TEAMS_PATH + "/#", TEAM_FULL_TABLE_ROW_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.FIXTURES_PATH + "/" + FootballScoresContract.FULL_FIXTURES_PATH + "/" + FootballScoresContract.INCOMING_PATH + "/" + FootballScoresContract.TEAMS_PATH + "/#", TEAM_INCOMING_FIXTURES_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.FIXTURES_PATH + "/" + FootballScoresContract.FULL_FIXTURES_PATH + "/" + FootballScoresContract.RECENT_PATH + "/" + FootballScoresContract.TEAMS_PATH + "/#", TEAM_RECENT_FIXTURES_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.LEAGUE_TEAMS_PATH, LEAGUE_TEAMS_CODE);
        uriMatcher.addURI(FootballScoresContract.AUTHORITY, FootballScoresContract.SYNC_STATE_PATH, SYNC_STATE_CODE);

        return uriMatcher;
    }
}
