package barqsoft.footballscores.model;

import java.util.Date;


/**
 * Created by Paulina on 2015-11-23.
 */
public class FixtureScore extends FixtureComing {

    public int homeGoals;
    public int awayGoals;


    public FixtureScore(Team homeTeam, Team awayTeam, Date date, League league,
                        int matchday, int homeGoals, int awayGoals) {

        super(homeTeam, awayTeam, date, league, matchday);
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
    }

    @Override
    public int getType() {
        return TYPE_LEAGUE_FIXTURES_SCORES;
    }
}
