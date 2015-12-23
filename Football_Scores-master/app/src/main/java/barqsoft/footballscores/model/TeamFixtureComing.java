package barqsoft.footballscores.model;

/**
 * Created by Paulina on 2015-11-23.
 */
public class TeamFixtureComing implements BaseFootballData {

    public Team watchedTeam;
    public FixtureComing fixtureComing;


    public TeamFixtureComing(FixtureComing fixtureComing, Team watchedTeam) {
        this.fixtureComing = fixtureComing;
        this.watchedTeam = watchedTeam;
    }

    @Override
    public int getType() {
        return TYPE_TEAM_FIXTURE_COMING;
    }
}
