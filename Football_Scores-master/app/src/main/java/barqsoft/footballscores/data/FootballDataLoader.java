package barqsoft.footballscores.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import barqsoft.footballscores.FootballScores;
import barqsoft.footballscores.model.BaseFootballData;
import barqsoft.footballscores.model.FixtureComing;
import barqsoft.footballscores.model.FixtureScore;
import barqsoft.footballscores.model.FootballTableRow;
import barqsoft.footballscores.model.HeaderData;
import barqsoft.footballscores.model.League;
import barqsoft.footballscores.model.LeagueFixturesComing;
import barqsoft.footballscores.model.LeagueFixturesScores;
import barqsoft.footballscores.model.LeagueTableData;
import barqsoft.footballscores.model.Team;
import barqsoft.footballscores.model.TeamFixtureComing;
import barqsoft.footballscores.model.TeamFixtureScore;
import barqsoft.footballscores.model.TeamTableData;
import barqsoft.footballscores.syncadapters.FootballScoresSyncAdapter;

public class FootballDataLoader extends AsyncTaskLoader<List<BaseFootballData>> {

    public static final SimpleDateFormat DATE_ONLY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd, '10:00'");

    private static final int MAX_DATES_FORWARD = 3;
    private static final int MAX_DATES_BACKWARDS = 3;

    private BroadcastReceiver syncBroadcastReceiver;

    public FootballDataLoader(Context context) {
        super(context);
    }

    @Override
    public List<BaseFootballData> loadInBackground() {
        List<BaseFootballData> data = new ArrayList<>();

        Cursor selectedTeamsCursor = getContext().getContentResolver().query(
                FootballScoresContract.TeamsEntry.CONTENT_URI,
                new String[]{FootballScoresContract.TeamsEntry._ID},
                FootballScoresContract.TeamsEntry.SELECTED_COLUMN_NAME + "=?",
                new String[]{Integer.toString(1)},
                null
        );
        while (selectedTeamsCursor.moveToNext())
            data.addAll(loadTeamData(selectedTeamsCursor.getInt(selectedTeamsCursor.getColumnIndex(
                    FootballScoresContract.TeamsEntry._ID))));

        selectedTeamsCursor.close();

        Cursor selectedLeaguesCursor = getContext().getContentResolver().query(
                FootballScoresContract.SoccerSeasonsEntry.CONTENT_URI,
                new String[]{FootballScoresContract.SoccerSeasonsEntry._ID},
                FootballScoresContract.SoccerSeasonsEntry.SELECTED_COLUMN_NAME + "=?",
                new String[]{Integer.toString(1)},
                null);


        while (selectedLeaguesCursor.moveToNext())
            data.addAll(loadLeagueData(selectedLeaguesCursor
                    .getInt(selectedLeaguesCursor.getColumnIndex(
                            FootballScoresContract.SoccerSeasonsEntry._ID))));

        selectedLeaguesCursor.close();

        return data;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        if (syncBroadcastReceiver == null)
            syncBroadcastReceiver = new SyncFinishedReceiver();
    }

    private List<BaseFootballData> loadTeamData(int teamId) {
        List<BaseFootballData> data = new ArrayList<>();

        Team team = DataUtil.loadTeam(getContext(), teamId);
        if (team == null)
            return data;

        List<FootballTableRow> table = loadTable(
                FootballScoresContract.FootballTableRowsEntry.buildTableForTeamUri(team.id), teamId);
        List<BaseFootballData> fixtureComing = loadIncomingFixtures(
                FootballScoresContract.FixturesEntry.buildTeamIncomingFixturesUri(team.id));
        List<BaseFootballData> fixtureScores = loadRecentFixtures(
                FootballScoresContract.FixturesEntry.buildTeamRecentFixturesUri(team.id));

        if (table.size() > 0 || fixtureComing.size() > 0 || fixtureScores.size() > 0)
            data.add(new HeaderData(team.name));

        if (fixtureScores.size() > 0) {
            TeamFixtureScore tfs = new TeamFixtureScore((FixtureScore) fixtureScores.get(1), team);
            data.add(tfs);
        }

        if (fixtureComing.size() > 0) {
            TeamFixtureComing tfc = new TeamFixtureComing((FixtureComing) fixtureComing.get(1), team);
            data.add(tfc);
        }

        if (table.size() > 0) {
            int idx = -1;
            for (FootballTableRow ftr : table) {
                if (ftr.team.equals(team)) {
                    idx = table.indexOf(ftr);
                    break;
                }
            }
            if (idx != -1) {
                if (idx > 0 && idx < table.size() - 1) {
                    table = table.subList(idx - 1, idx + 2);
                } else if (idx == 0) {
                    table = table.subList(0, idx + 3);
                } else {
                    table = table.subList(idx - 2, idx + 1);
                }
                TeamTableData ttd = new TeamTableData(table.get(0).league, team, table);
                data.add(ttd);
            }
        }


        return data;
    }

    private List<BaseFootballData> loadLeagueData(int leagueId) {
        List<BaseFootballData> baseFootballData = new ArrayList<>();

        League league = DataUtil.loadLeague(getContext(), leagueId);
        if (league == null)
            return baseFootballData;

        List<FootballTableRow> table =
                loadTable(FootballScoresContract.FootballTableRowsEntry.buildTableForLeagueUri(league.id), -1);
        List<BaseFootballData> incomingFixturesData = loadIncomingFixtures(
                FootballScoresContract.FixturesEntry.buildLeagueIncomingFixturesUri(league.id));
        List<BaseFootballData> recentFixturesData = loadRecentFixtures(
                FootballScoresContract.FixturesEntry.buildLeagueRecentFixturesUri(league.id));

        if (table.size() > 0 || incomingFixturesData.size() > 0 || recentFixturesData.size() > 0) {
            baseFootballData.add(new HeaderData(league.caption));
        }

        if (table.size() > 0) {
            LeagueTableData ltd = new LeagueTableData(league, table);
            baseFootballData.add(ltd);
        }

        if (recentFixturesData.size() > 0) {
            LeagueFixturesScores lfs = new LeagueFixturesScores(league, recentFixturesData);
            baseFootballData.add(lfs);
        }

        if (incomingFixturesData.size() > 0) {
            LeagueFixturesComing lfc = new LeagueFixturesComing(league, incomingFixturesData);
            baseFootballData.add(lfc);
        }

        return baseFootballData;
    }

    private List<FootballTableRow> loadTable(Uri tableUri, int selectedTeamId) {
        Cursor tableCursor = getContext().getContentResolver().query(
                tableUri,
                null,
                null,
                null,
                null);

        List<FootballTableRow> table = new ArrayList<>();
        while (tableCursor.moveToNext()) {
            table.add(loadTableRow(tableCursor, selectedTeamId));
        }

        tableCursor.close();
        return table;
    }

    private FootballTableRow loadTableRow(Cursor tableCursor, int selectedTeamId) {
        int teamId = tableCursor.getInt(tableCursor.getColumnIndex(FootballScoresContract.FootballTableRowsEntry.TEAM_ID_COLUMN_NAME));
        String teamName = tableCursor.getString(tableCursor.getColumnIndex(FootballScoresContract.FootballTableRowsEntry.FULL_TABLE_ROW_TEAM_NAME));
        String shortName = tableCursor.getString(tableCursor.getColumnIndex(FootballScoresContract.FootballTableRowsEntry.FULL_TABLE_ROW_TEAM_SHORT_NAME));
        String teamCode = tableCursor.getString(tableCursor.getColumnIndex(FootballScoresContract.FootballTableRowsEntry.FULL_TABLE_ROW_TEAM_CODE));
        Team team = new Team(teamId, teamName, teamCode, shortName, teamId == selectedTeamId);

        int order = tableCursor.getInt(tableCursor.getColumnIndex(FootballScoresContract.FootballTableRowsEntry.ORDER_COLUMN_NAME));
        int matchesPlayed = tableCursor.getInt(tableCursor.getColumnIndex(FootballScoresContract.FootballTableRowsEntry.GAMES_PLAYED_COLUMN_NAME));
        int goalsScored = tableCursor.getInt(tableCursor.getColumnIndex(FootballScoresContract.FootballTableRowsEntry.GOALS_SCORED_COLUMN_NAME));
        int goalsLost = tableCursor.getInt(tableCursor.getColumnIndex(FootballScoresContract.FootballTableRowsEntry.GOALS_LOST_COLUMN_NAME));
        int points = tableCursor.getInt(tableCursor.getColumnIndex(FootballScoresContract.FootballTableRowsEntry.POINTS_COLUMN_NAME));

        League league = DataUtil.loadLeague(getContext(), tableCursor.getInt(tableCursor.getColumnIndex(FootballScoresContract.FootballTableRowsEntry.LEAGUE_ID_COLUMN_NAME)));

        return new FootballTableRow(league, team, order, matchesPlayed, goalsLost, goalsScored, points);
    }

    private List<BaseFootballData> loadIncomingFixtures(Uri uri) {
        Cursor incomingFixturesCursor = getContext().getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);

        Map<String, List<FixtureComing>> incomingFixturesMap = new TreeMap<>();
        while (incomingFixturesCursor.moveToNext()) {
            Team homeTeam = DataUtil.parseHomeTeam(incomingFixturesCursor);
            Team awayTeam = DataUtil.parseAwayTeam(incomingFixturesCursor);

            String fixtureDate = incomingFixturesCursor.getString(incomingFixturesCursor.getColumnIndex(FootballScoresContract.FixturesEntry.DATE_COLUMN_NAME));

            Date date = null;
            try {
                date = FootballScores.API_DATE_FORMAT.parse(fixtureDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int matchDay = incomingFixturesCursor.getInt(incomingFixturesCursor.getColumnIndex(FootballScoresContract.FixturesEntry.MATCHDAY_COLUMN_NAME));

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(date.getTime());
            String dateString = FootballScores.DATE_DEFAULT_FORMAT.format(new Date(c.getTimeInMillis()));
            List<FixtureComing> fixturesComing = incomingFixturesMap.get(FootballScores.DATE_DEFAULT_FORMAT.format(date));
            if (fixturesComing == null) {
                fixturesComing = new ArrayList<>();
                incomingFixturesMap.put(dateString, fixturesComing);
            }
            League league = DataUtil.loadLeague(getContext(), incomingFixturesCursor.getInt(incomingFixturesCursor.getColumnIndex(FootballScoresContract.FixturesEntry.SOCCER_SEASON_ID_COLUMN_NAME)));
            fixturesComing.add(new FixtureComing(homeTeam, awayTeam, date, league, matchDay));
        }

        List<BaseFootballData> incomingFixturesData = new ArrayList<>();
        List<String> keySet = new ArrayList<>(incomingFixturesMap.keySet());

        if (keySet.size() > MAX_DATES_FORWARD)
            keySet = keySet.subList(0, MAX_DATES_FORWARD);

        for (String date : keySet) {
            incomingFixturesData.add(new HeaderData(date));
            List<FixtureComing> fixturesComing = incomingFixturesMap.get(date);
            for (FixtureComing fc : fixturesComing)
                incomingFixturesData.add(fc);
        }
        incomingFixturesCursor.close();

        return incomingFixturesData;
    }

    private List<BaseFootballData> loadRecentFixtures(Uri uri) {
        Cursor recentFixturesCursor = getContext().getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);

        Map<String, List<FixtureScore>> recentFixturesMap = new TreeMap<>();
        while (recentFixturesCursor.moveToNext()) {
            Team homeTeam = DataUtil.parseHomeTeam(recentFixturesCursor);
            Team awayTeam = DataUtil.parseAwayTeam(recentFixturesCursor);

            String fixtureDate = recentFixturesCursor.getString(recentFixturesCursor.getColumnIndex(FootballScoresContract.FixturesEntry.DATE_COLUMN_NAME));

            Date date = null;
            try {
                date = FootballScores.API_DATE_FORMAT.parse(fixtureDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int matchDay = recentFixturesCursor.getInt(recentFixturesCursor.getColumnIndex(FootballScoresContract.FixturesEntry.MATCHDAY_COLUMN_NAME));

            String dateString = DATE_ONLY_DATE_FORMAT.format(date);
            List<FixtureScore> fixturesScore = recentFixturesMap.get(dateString);
            if (fixturesScore == null) {
                fixturesScore = new ArrayList<>();
                recentFixturesMap.put(dateString, fixturesScore);
            }
            League league = DataUtil.loadLeague(getContext(), recentFixturesCursor.getInt(recentFixturesCursor.getColumnIndex(FootballScoresContract.FixturesEntry.SOCCER_SEASON_ID_COLUMN_NAME)));
            int homeGoals = recentFixturesCursor.getInt(recentFixturesCursor.getColumnIndex(FootballScoresContract.FixturesEntry.HOME_GOALS_COLUMN_NAME));
            int awayGoals = recentFixturesCursor.getInt(recentFixturesCursor.getColumnIndex(FootballScoresContract.FixturesEntry.AWAY_GOALS_COLUMN_NAME));
            fixturesScore.add(new FixtureScore(homeTeam, awayTeam, date, league, matchDay, homeGoals, awayGoals));
        }

        List<BaseFootballData> recentFixturesData = new ArrayList<>();
        List<String> keySet = new ArrayList<>(recentFixturesMap.keySet());
        Collections.reverse(keySet);
        if (keySet.size() > MAX_DATES_BACKWARDS)
            keySet = keySet.subList(0, MAX_DATES_BACKWARDS);

        for (String date : keySet) {
            recentFixturesData.add(new HeaderData(date));
            List<FixtureScore> fixturesScores = recentFixturesMap.get(date);
            for (FixtureComing fc : fixturesScores)
                recentFixturesData.add(fc);
        }
        recentFixturesCursor.close();

        return recentFixturesData;
    }

    @Override
    protected void onReset() {
        super.onReset();
        getContext().unregisterReceiver(syncBroadcastReceiver);
    }

    class SyncFinishedReceiver extends BroadcastReceiver {

        SyncFinishedReceiver() {
            getContext().registerReceiver(this, new IntentFilter(FootballScoresSyncAdapter.ACTION_SYNC_STATE_CHANGED));
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            FootballDataLoader.this.onContentChanged();
        }
    }
}
