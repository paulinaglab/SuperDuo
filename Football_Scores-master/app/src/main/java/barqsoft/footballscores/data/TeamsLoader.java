package barqsoft.footballscores.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.model.BaseFootballData;
import barqsoft.footballscores.model.HeaderData;
import barqsoft.footballscores.model.League;
import barqsoft.footballscores.model.Team;

/**
 * Created by Paulina on 2015-12-11.
 */
public class TeamsLoader extends CustomizationBaseLoader {
    public TeamsLoader(Context context) {
        super(context);
    }

    @Override
    public List<BaseFootballData> loadInBackground() {
        List<BaseFootballData> baseFootballData = new ArrayList<>();
        List<League> leagues = loadLeagues();

        for (League league : leagues) {
            List<Team> teams = loadTeams(league.id);
            if (teams.size() > 0)
                baseFootballData.add(new HeaderData(league.caption));

            baseFootballData.addAll(teams);
        }

        return baseFootballData;
    }
}
