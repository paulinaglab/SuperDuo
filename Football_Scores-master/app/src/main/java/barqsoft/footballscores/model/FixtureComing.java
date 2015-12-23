package barqsoft.footballscores.model;

import java.util.Date;


/**
 * Created by Paulina on 2015-11-23.
 */
public class FixtureComing implements BaseFootballData {

    public Team homeTeam;
    public Team awayTeam;
    public Date date;
    public League league;
    public int matchday;


    public FixtureComing(Team homeTeam, Team awayTeam, Date date, League league, int matchday) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
        this.league = league;
        this.matchday = matchday;
    }

    @Override
    public int getType() {
        return TYPE_LEAGUE_FIXTURES_COMING;
    }
}
