package barqsoft.footballscores.model;

/**
 * Created by Paulina on 2015-11-23.
 */
public class TeamFixtureScore implements BaseFootballData {

    public Team watchedTeam;
    public FixtureScore fixtureScore;

    public TeamFixtureScore(FixtureScore fixtureScore, Team watchedTeam) {
        this.fixtureScore = fixtureScore;
        this.watchedTeam = watchedTeam;
    }

    @Override
    public int getType() {
        return BaseFootballData.TYPE_TEAM_FIXTURE_SCORE;
    }
}
