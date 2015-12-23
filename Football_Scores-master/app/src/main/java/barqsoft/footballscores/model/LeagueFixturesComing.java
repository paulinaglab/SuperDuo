package barqsoft.footballscores.model;

import java.util.List;


/**
 * Created by Paulina on 2015-11-23.
 */
public class LeagueFixturesComing implements BaseFootballData {

    public List<BaseFootballData> fixturesData;
    public League league;

    public LeagueFixturesComing(League league, List<BaseFootballData> fixturesData) {
        this.fixturesData = fixturesData;
        this.league = league;
    }

    @Override
    public int getType() {
        return TYPE_LEAGUE_FIXTURES_COMING;
    }
}
