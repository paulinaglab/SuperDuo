package barqsoft.footballscores.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.model.BaseFootballData;
import barqsoft.footballscores.model.League;
import barqsoft.footballscores.model.Team;

/**
 * Created by Paulina on 2015-12-03.
 */
public abstract class CustomizationBaseLoader extends AsyncTaskLoader<List<BaseFootballData>> {
    public CustomizationBaseLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    public List<League> loadLeagues() {
        Cursor cursor = getContext().getContentResolver().query(
                FootballScoresContract.SoccerSeasonsEntry.CONTENT_URI,
                null,
                null,
                null,
                FootballScoresContract.SoccerSeasonsEntry.CAPTION_COLUMN_NAME
        );

        List<League> leagues = new ArrayList<>();

        while (cursor.moveToNext()) {
            leagues.add(DataUtil.parseLeague(cursor));
        }
        cursor.close();

        return leagues;
    }

    public List<Team> loadTeams(int leagueId) {
        Cursor cursor = getContext().getContentResolver().query(
                FootballScoresContract.TeamsEntry.buildLeagueTeamUri(leagueId),
                null,
                null,
                null,
                null
        );

        List<Team> teams = new ArrayList<>();
        while (cursor.moveToNext()) {
            teams.add(DataUtil.parseTeam(cursor));
        }
        cursor.close();

        return teams;
    }
}
