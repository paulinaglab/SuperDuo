package barqsoft.footballscores.model;

import java.util.List;


/**
 * Created by Paulina on 2015-11-23.
 */
public class LeagueFixturesScores implements BaseFootballData {

    public League league;
    public List<BaseFootballData> fixturesData;


    public LeagueFixturesScores(League league, List<BaseFootballData> fixturesData) {
        this.league = league;
        this.fixturesData = fixturesData;
    }

    @Override
    public int getType() {
        return TYPE_LEAGUE_FIXTURES_SCORES;
    }
}
