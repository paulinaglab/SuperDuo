package barqsoft.footballscores.data;

import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import barqsoft.footballscores.FootballScores;
import barqsoft.footballscores.model.FixtureComing;
import barqsoft.footballscores.model.FixtureScore;
import barqsoft.footballscores.model.League;
import barqsoft.footballscores.model.Team;
import barqsoft.footballscores.model.TeamFixtureComing;
import barqsoft.footballscores.model.TeamFixtureScore;

/**
 * Created by Paulina on 2015-12-09.
 */
public class DataUtil {

    public static Team loadTeam(Context context, int teamId) {
        Cursor cursor = context.getContentResolver().query(
                FootballScoresContract.TeamsEntry.CONTENT_URI,
                null,
                FootballScoresContract.TeamsEntry._ID + "=?",
                new String[]{Integer.toString(teamId)},
                null);
        if (cursor.moveToFirst())
            return parseTeam(cursor);
        else
            return null;
    }

    public static Team parseTeam(Cursor cursor) {
        return parseTeam(cursor,
                FootballScoresContract.TeamsEntry._ID,
                FootballScoresContract.TeamsEntry.NAME_COLUMN_NAME,
                FootballScoresContract.TeamsEntry.CODE_COLUMN_NAME,
                FootballScoresContract.TeamsEntry.SHORT_NAME_COLUMN_NAME,
                FootballScoresContract.TeamsEntry.SELECTED_COLUMN_NAME);
    }

    public static Team parseHomeTeam(Cursor cursor) {
        return parseTeam(cursor,
                FootballScoresContract.FixturesEntry.HOME_TEAM_ID_COLUMN_NAME,
                FootballScoresContract.FixturesEntry.FULL_FIXTURE_HOME_TEAM_NAME,
                FootballScoresContract.FixturesEntry.FULL_FIXTURE_HOME_TEAM_CODE,
                FootballScoresContract.FixturesEntry.FULL_FIXTURE_HOME_TEAM_SHORT_NAME,
                FootballScoresContract.FixturesEntry.FULL_FIXTURE_HOME_TEAM_SELECTED);
    }

    public static Team parseAwayTeam(Cursor cursor) {
        return parseTeam(cursor,
                FootballScoresContract.FixturesEntry.AWAY_TEAM_ID_COLUMN_NAME,
                FootballScoresContract.FixturesEntry.FULL_FIXTURE_AWAY_TEAM_NAME,
                FootballScoresContract.FixturesEntry.FULL_FIXTURE_AWAY_TEAM_CODE,
                FootballScoresContract.FixturesEntry.FULL_FIXTURE_AWAY_TEAM_SHORT_NAME,
                FootballScoresContract.FixturesEntry.FULL_FIXTURE_AWAY_TEAM_SELECTED);
    }

    private static Team parseTeam(Cursor cursor,
                                  String idColName,
                                  String nameColName,
                                  String codeColName,
                                  String shortNameColName,
                                  String selectedColName) {
        int teamId = cursor.getInt(cursor.getColumnIndex(idColName));
        String name = cursor.getString(cursor.getColumnIndex(nameColName));
        String code = cursor.getString(cursor.getColumnIndex(codeColName));
        String shortName = cursor.getString(cursor.getColumnIndex(shortNameColName));
        boolean selected = cursor.getInt(cursor.getColumnIndex(selectedColName)) != 0;
        return new Team(teamId, name, code, shortName, selected);
    }

    public static League loadLeague(Context context, int leagueId) {
        Cursor leagueCursor = context.getContentResolver().query(
                FootballScoresContract.SoccerSeasonsEntry.CONTENT_URI,
                null,
                FootballScoresContract.SoccerSeasonsEntry._ID + "=?",
                new String[]{Integer.toString(leagueId)},
                null);

        League league = null;
        if (leagueCursor.moveToFirst()) {
            league = DataUtil.parseLeague(leagueCursor);
        }
        leagueCursor.close();
        return league;
    }

    public static League parseLeague(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(FootballScoresContract.SoccerSeasonsEntry._ID));
        int year = cursor.getInt(cursor.getColumnIndex(FootballScoresContract.SoccerSeasonsEntry.YEAR_COLUMN_NAME));
        String leagueCode = cursor.getString(cursor.getColumnIndex(FootballScoresContract.SoccerSeasonsEntry.LEAGUE_COLUMN_NAME));
        String caption = cursor.getString(cursor.getColumnIndex(FootballScoresContract.SoccerSeasonsEntry.CAPTION_COLUMN_NAME));
        boolean selected = cursor.getInt(cursor.getColumnIndex(FootballScoresContract.SoccerSeasonsEntry.SELECTED_COLUMN_NAME)) != 0;
        return new League(id, year, leagueCode, caption, selected);
    }

    public static TeamFixtureComing loadTeamComingFixture(Context context, int teamId) {
        Cursor c = context.getContentResolver().query(
                FootballScoresContract.FixturesEntry.buildTeamIncomingFixturesUri(teamId),
                null, null, null, null);

        TeamFixtureComing teamFixtureComing = null;
        if (c.moveToFirst()) {
            FixtureComing fixtureComing = parseFixtureComing(context, c);
            teamFixtureComing = new TeamFixtureComing(fixtureComing,
                    fixtureComing.homeTeam.id == teamId ? fixtureComing.homeTeam : fixtureComing.awayTeam);
        }
        c.close();

        return teamFixtureComing;
    }

    public static TeamFixtureScore loadTeamFixtureScore(Context context, int teamId) {
        Cursor c = context.getContentResolver().query(
                FootballScoresContract.FixturesEntry.buildTeamRecentFixturesUri(teamId),
                null, null, null, null);

        TeamFixtureScore teamFixtureScore = null;
        if (c.moveToFirst()) {
            FixtureScore fixtureScore = parseFixtureScore(context, c);
            teamFixtureScore = new TeamFixtureScore(fixtureScore,
                    fixtureScore.homeTeam.id == teamId ? fixtureScore.homeTeam : fixtureScore.awayTeam);
        }
        c.close();

        return teamFixtureScore;
    }

    public static List<FixtureComing> loadLeagueFixturesComing(Context context, int leagueId) {
        Cursor c = context.getContentResolver().query(
                FootballScoresContract.FixturesEntry.buildLeagueIncomingFixturesUri(leagueId),
                null, null, null, null);

        List<FixtureComing> list = new ArrayList<>();

        while (c.moveToNext())
            list.add(parseFixtureComing(context, c));

        c.close();
        return list;
    }

    public static List<FixtureScore> loadLeagueFixtureScores(Context context, int leagueId) {
        Cursor c = context.getContentResolver().query(
                FootballScoresContract.FixturesEntry.buildLeagueRecentFixturesUri(leagueId),
                null, null, null, null);

        List<FixtureScore> list = new ArrayList<>();

        while (c.moveToNext())
            list.add(parseFixtureScore(context, c));

        c.close();
        return list;
    }

    private static FixtureComing parseFixtureComing(Context context, Cursor c) {
        Team home = parseHomeTeam(c);
        Team away = parseAwayTeam(c);

        League league = loadLeague(context, c.getInt(c.getColumnIndex(FootballScoresContract.FixturesEntry.SOCCER_SEASON_ID_COLUMN_NAME)));
        int matchday = c.getInt(c.getColumnIndex(FootballScoresContract.FixturesEntry.MATCHDAY_COLUMN_NAME));
        Date date;
        try {
            date = FootballScores.API_DATE_FORMAT.parse(c.getString(c.getColumnIndex(FootballScoresContract.FixturesEntry.DATE_COLUMN_NAME)));
        } catch (ParseException e) {
            c.close();
            return null;
        }

        return new FixtureComing(home, away, date, league, matchday);
    }

    private static FixtureScore parseFixtureScore(Context context, Cursor c) {
        Team home = parseHomeTeam(c);
        Team away = parseAwayTeam(c);

        League league = loadLeague(context, c.getInt(c.getColumnIndex(FootballScoresContract.FixturesEntry.SOCCER_SEASON_ID_COLUMN_NAME)));
        int matchday = c.getInt(c.getColumnIndex(FootballScoresContract.FixturesEntry.MATCHDAY_COLUMN_NAME));
        Date date;
        try {
            date = FootballScores.API_DATE_FORMAT.parse(c.getString(c.getColumnIndex(FootballScoresContract.FixturesEntry.DATE_COLUMN_NAME)));
        } catch (ParseException e) {
            c.close();
            return null;
        }
        int homeGoals = c.getInt(c.getColumnIndex(FootballScoresContract.FixturesEntry.HOME_GOALS_COLUMN_NAME));
        int awayGoals = c.getInt(c.getColumnIndex(FootballScoresContract.FixturesEntry.AWAY_GOALS_COLUMN_NAME));
        return new FixtureScore(home, away, date, league, matchday, homeGoals, awayGoals);
    }
}
