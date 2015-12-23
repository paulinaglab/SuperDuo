package barqsoft.footballscores.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by Paulina on 2015-11-19.
 */
public class FootballScoresDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "football_scores.db";

    public FootballScoresDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_SOCCER_SEASON_TABLE =
                "CREATE TABLE " + FootballScoresContract.SoccerSeasonsEntry.TABLE_NAME + "(" +
                        FootballScoresContract.SoccerSeasonsEntry._ID + " INTEGER PRIMARY KEY, " +
                        FootballScoresContract.SoccerSeasonsEntry.CAPTION_COLUMN_NAME + " TEXT, " +
                        FootballScoresContract.SoccerSeasonsEntry.LEAGUE_COLUMN_NAME + " TEXT, " +
                        FootballScoresContract.SoccerSeasonsEntry.SELECTED_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.SoccerSeasonsEntry.YEAR_COLUMN_NAME + " INTEGER)";
        sqLiteDatabase.execSQL(SQL_CREATE_SOCCER_SEASON_TABLE);

        final String SQL_CREATE_TEAMS_TABLE =
                "CREATE TABLE " + FootballScoresContract.TeamsEntry.TABLE_NAME + "(" +
                        FootballScoresContract.TeamsEntry._ID + " INTEGER PRIMARY KEY, " +
                        FootballScoresContract.TeamsEntry.NAME_COLUMN_NAME + " TEXT, " +
                        FootballScoresContract.TeamsEntry.CODE_COLUMN_NAME + " TEXT, " +
                        FootballScoresContract.TeamsEntry.SELECTED_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.TeamsEntry.SHORT_NAME_COLUMN_NAME + " TEXT)";
        sqLiteDatabase.execSQL(SQL_CREATE_TEAMS_TABLE);

        final String SQL_CREATE_FIXTURES_TABLE =
                "CREATE TABLE " + FootballScoresContract.FixturesEntry.TABLE_NAME + "(" +
                        FootballScoresContract.FixturesEntry._ID + " INTEGER PRIMARY KEY, " +
                        FootballScoresContract.FixturesEntry.DATE_COLUMN_NAME + " TEXT, " +
                        FootballScoresContract.FixturesEntry.STATUS_COLUMN_NAME + " TEXT, " +
                        FootballScoresContract.FixturesEntry.HOME_GOALS_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.FixturesEntry.AWAY_GOALS_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.FixturesEntry.MATCHDAY_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.FixturesEntry.HOME_TEAM_ID_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.FixturesEntry.AWAY_TEAM_ID_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.FixturesEntry.SOCCER_SEASON_ID_COLUMN_NAME + " INTEGER, " +
                        "FOREIGN KEY (" + FootballScoresContract.FixturesEntry.HOME_TEAM_ID_COLUMN_NAME + ") REFERENCES " + FootballScoresContract.TeamsEntry.TABLE_NAME + "(" + FootballScoresContract.TeamsEntry._ID + "), " +
                        "FOREIGN KEY (" + FootballScoresContract.FixturesEntry.AWAY_TEAM_ID_COLUMN_NAME + ") REFERENCES " + FootballScoresContract.TeamsEntry.TABLE_NAME + "(" + FootballScoresContract.TeamsEntry._ID + "), " +
                        "FOREIGN KEY (" + FootballScoresContract.FixturesEntry.SOCCER_SEASON_ID_COLUMN_NAME + ") REFERENCES " + FootballScoresContract.SoccerSeasonsEntry.TABLE_NAME + "(" + FootballScoresContract.SoccerSeasonsEntry._ID + "))";
        sqLiteDatabase.execSQL(SQL_CREATE_FIXTURES_TABLE);

        final String SQL_CREATE_FOOTBALL_TABLE_ROWS_TABLE =
                "CREATE TABLE " + FootballScoresContract.FootballTableRowsEntry.TABLE_NAME + "(" +
                        FootballScoresContract.FootballTableRowsEntry._ID + " INTEGER PRIMARY KEY, " +
                        FootballScoresContract.FootballTableRowsEntry.GAMES_PLAYED_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.FootballTableRowsEntry.GOALS_LOST_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.FootballTableRowsEntry.GOALS_SCORED_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.FootballTableRowsEntry.POINTS_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.FootballTableRowsEntry.ORDER_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.FootballTableRowsEntry.TEAM_ID_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.FootballTableRowsEntry.LEAGUE_ID_COLUMN_NAME + " INTEGER, " +
                        "FOREIGN KEY (" + FootballScoresContract.FootballTableRowsEntry.LEAGUE_ID_COLUMN_NAME + ") REFERENCES " + FootballScoresContract.SoccerSeasonsEntry.TABLE_NAME + "(" + FootballScoresContract.SoccerSeasonsEntry._ID + ")," +
                        "FOREIGN KEY (" + FootballScoresContract.FootballTableRowsEntry.TEAM_ID_COLUMN_NAME + ") REFERENCES " + FootballScoresContract.TeamsEntry.TABLE_NAME + "(" + FootballScoresContract.TeamsEntry._ID + "))";
        sqLiteDatabase.execSQL(SQL_CREATE_FOOTBALL_TABLE_ROWS_TABLE);

        final String SQL_CREATE_LEAGUE_TEAM_TABLE =
                "CREATE TABLE " + FootballScoresContract.LeagueTeamsEntry.TABLE_NAME + "(" +
                        FootballScoresContract.LeagueTeamsEntry._ID + " INTEGER PRIMARY KEY, " +
                        FootballScoresContract.LeagueTeamsEntry.LEAGUE_ID_COLUMN_NAME + " INTEGER, " +
                        FootballScoresContract.LeagueTeamsEntry.TEAM_ID_COLUMN_NAME + " INTEGER, " +
                        "FOREIGN KEY (" + FootballScoresContract.LeagueTeamsEntry.LEAGUE_ID_COLUMN_NAME + ") REFERENCES " + FootballScoresContract.SoccerSeasonsEntry.TABLE_NAME + "(" + FootballScoresContract.SoccerSeasonsEntry._ID + "), " +
                        "FOREIGN KEY (" + FootballScoresContract.LeagueTeamsEntry.TEAM_ID_COLUMN_NAME + ") REFERENCES " + FootballScoresContract.TeamsEntry.TABLE_NAME + "(" + FootballScoresContract.TeamsEntry._ID + "));";
        sqLiteDatabase.execSQL(SQL_CREATE_LEAGUE_TEAM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                db.setForeignKeyConstraintsEnabled(true);
            } else {
                db.execSQL("PRAGMA foreign_keys=ON");
            }
        }
    }
}
