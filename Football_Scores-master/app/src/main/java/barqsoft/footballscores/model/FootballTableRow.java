package barqsoft.footballscores.model;


/**
 * Created by Paulina on 2015-11-23.
 */
public class FootballTableRow {

    public Team team;
    public League league;
    public int order;
    public int matchesPlayed;
    public int goalsLost;
    public int goalsScored;
    public int points;


    public FootballTableRow(League league, Team team, int order, int matchesPlayed,
                            int goalsLost, int goalsScored, int points) {
        this.league = league;
        this.team = team;
        this.order = order;
        this.matchesPlayed = matchesPlayed;
        this.goalsLost = goalsLost;
        this.goalsScored = goalsScored;
        this.points = points;
    }
}
