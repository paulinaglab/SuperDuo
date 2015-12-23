package barqsoft.footballscores.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Paulina on 2015-11-19.
 */
public class FootballScoresContract {

    public static final String AUTHORITY = "barqsoft.footballscores.Authority";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String SOCCER_SEASONS_PATH = "soccerSeasons";
    public static final String TEAMS_PATH = "teams";
    public static final String FIXTURES_PATH = "fixtures";
    public static final String FOOTBALL_TABLE_ROW_PATH = "footballTableRow";
    public static final String FULL_FIXTURES_PATH = "fullFixtures";
    public static final String FULL_TABLE_ROW_PATH = "fullTableRow";
    public static final String INCOMING_PATH = "incoming";
    public static final String RECENT_PATH = "recent";
    public static final String LEAGUE_TEAMS_PATH = "leagueTeam";
    public static final String SYNC_STATE_PATH = "syncState";

    public static class SoccerSeasonsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(SOCCER_SEASONS_PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + SOCCER_SEASONS_PATH;

        public static final String TABLE_NAME = "soccerSeasons";
        public static final String YEAR_COLUMN_NAME = "year";
        public static final String LEAGUE_COLUMN_NAME = "league";
        public static final String CAPTION_COLUMN_NAME = "caption";
        public static final String SELECTED_COLUMN_NAME = "selected";
    }

    public static class TeamsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(TEAMS_PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + TEAMS_PATH;

        public static final String TABLE_NAME = "teams";
        public static final String NAME_COLUMN_NAME = "name";
        public static final String CODE_COLUMN_NAME = "code";
        public static final String SHORT_NAME_COLUMN_NAME = "shortName";
        public static final String SELECTED_COLUMN_NAME = "selected";

        public static Uri buildLeagueTeamUri(int leagueId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(SOCCER_SEASONS_PATH)
                    .appendPath(Integer.toString(leagueId))
                    .build();
        }
    }

    public static class FixturesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(FIXTURES_PATH).build();
        public static final Uri FULL_FIXTURES_URI = CONTENT_URI.buildUpon().appendPath(FULL_FIXTURES_PATH).build();
        public static final Uri INCOMING_FIXTURES_URI = FULL_FIXTURES_URI.buildUpon().appendPath(INCOMING_PATH).build();
        public static final Uri INCOMING_TEAM_FIXTURES_URI = INCOMING_FIXTURES_URI.buildUpon().appendPath(TEAMS_PATH).build();
        public static final Uri RECENT_FIXTURES_URI = FULL_FIXTURES_URI.buildUpon().appendPath(RECENT_PATH).build();
        public static final Uri RECENT_TEAM_FIXTURES_URI = RECENT_FIXTURES_URI.buildUpon().appendPath(TEAMS_PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + FIXTURES_PATH;
        public static final String FULL_FIXTURES_CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + FULL_FIXTURES_PATH;

        public static final String TABLE_NAME = "fixtures";
        public static final String STATUS_COLUMN_NAME = "status";
        public static final String HOME_GOALS_COLUMN_NAME = "homeGoals";
        public static final String AWAY_GOALS_COLUMN_NAME = "awayGoals";
        public static final String HOME_TEAM_ID_COLUMN_NAME = "homeTeamId";
        public static final String AWAY_TEAM_ID_COLUMN_NAME = "awayTeamId";
        public static final String MATCHDAY_COLUMN_NAME = "matchday";
        public static final String DATE_COLUMN_NAME = "date";
        public static final String SOCCER_SEASON_ID_COLUMN_NAME = "soccerSeasonId";
        public static final String FULL_FIXTURE_HOME_TEAM_CODE = "homeTeamCode";
        public static final String FULL_FIXTURE_HOME_TEAM_SHORT_NAME = "homeTeamShortName";
        public static final String FULL_FIXTURE_HOME_TEAM_NAME = "homeTeamName";
        public static final String FULL_FIXTURE_AWAY_TEAM_NAME = "awayTeamName";
        public static final String FULL_FIXTURE_AWAY_TEAM_CODE = "awayTeamCode";
        public static final String FULL_FIXTURE_AWAY_TEAM_SHORT_NAME = "awayTeamShortName";
        public static final String FULL_FIXTURE_LEAGUE_NAME = "leagueName";
        public static final String FULL_FIXTURE_AWAY_TEAM_SELECTED = "awayTeamSelected";
        public static final String FULL_FIXTURE_HOME_TEAM_SELECTED = "homeTeamSelected";

        public static Uri buildLeagueIncomingFixturesUri(int leagueId) {
            return FULL_FIXTURES_URI
                    .buildUpon()
                    .appendPath(INCOMING_PATH)
                    .appendPath(SOCCER_SEASONS_PATH)
                    .appendPath(Integer.toString(leagueId))
                    .build();
        }

        public static Uri buildLeagueRecentFixturesUri(int leagueId) {
            return FULL_FIXTURES_URI
                    .buildUpon()
                    .appendPath(RECENT_PATH)
                    .appendPath(SOCCER_SEASONS_PATH)
                    .appendPath(Integer.toString(leagueId))
                    .build();
        }

        public static Uri buildTeamIncomingFixturesUri(int teamId) {
            return FULL_FIXTURES_URI
                    .buildUpon()
                    .appendPath(INCOMING_PATH)
                    .appendPath(TEAMS_PATH)
                    .appendPath(Integer.toString(teamId))
                    .build();
        }

        public static Uri buildTeamRecentFixturesUri(int teamId) {
            return FULL_FIXTURES_URI
                    .buildUpon()
                    .appendPath(RECENT_PATH)
                    .appendPath(TEAMS_PATH)
                    .appendPath(Integer.toString(teamId))
                    .build();
        }
    }

    public static class FootballTableRowsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(FOOTBALL_TABLE_ROW_PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + FOOTBALL_TABLE_ROW_PATH;
        public static final String FULL_TABLE_ROW_CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + FULL_TABLE_ROW_PATH;

        public static final String TABLE_NAME = "football_table_rows";
        public static final String LEAGUE_ID_COLUMN_NAME = "leagueId";
        public static final String TEAM_ID_COLUMN_NAME = "teamId";
        public static final String ORDER_COLUMN_NAME = "rank";
        public static final String GAMES_PLAYED_COLUMN_NAME = "gamesPlayed";
        public static final String POINTS_COLUMN_NAME = "points";
        public static final String GOALS_SCORED_COLUMN_NAME = "goals";
        public static final String GOALS_LOST_COLUMN_NAME = "goalsLost";
        public static final String FULL_TABLE_ROW_TEAM_NAME = "teamName";
        public static final String FULL_TABLE_ROW_TEAM_CODE = "teamCode";
        public static final String FULL_TABLE_ROW_TEAM_SHORT_NAME = "teamShortName";
        public static final String FULL_TABLE_ROW_TEAM_SELECTED = "teamSelected";


        public static Uri buildTableForLeagueUri(long leagueId) {
            return CONTENT_URI.buildUpon().appendPath(SOCCER_SEASONS_PATH).appendPath(Long.toString(leagueId)).build();
        }

        public static Uri buildTableForTeamUri(long teamId) {
            return CONTENT_URI.buildUpon().appendPath(TEAMS_PATH).appendPath(Long.toString(teamId)).build();
        }
    }

    public static class LeagueTeamsEntry implements BaseColumns {
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + LEAGUE_TEAMS_PATH;
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(LEAGUE_TEAMS_PATH).build();

        public static final String TABLE_NAME = "league_team";
        public static final String TEAM_ID_COLUMN_NAME = "team_id";
        public static final String LEAGUE_ID_COLUMN_NAME = "league_id";
    }

    public static class SyncStateEntry {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(SYNC_STATE_PATH).build();

        public static final String SYNC_STATE_PREFERENCE_NAME = "sync_state_preference_name";
        public static final String SYNC_STATE_COLUMN_NAME = "sync_state";
    }

}

