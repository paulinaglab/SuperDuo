package barqsoft.footballscores.model;

import java.util.List;


/**
 * Created by Paulina on 2015-11-23.
 */
public class LeagueTableData implements BaseFootballData {

    public League league;
    public List<FootballTableRow> footballTableRows;


    public LeagueTableData(League league, List<FootballTableRow> footballTableRows) {
        this.league = league;
        this.footballTableRows = footballTableRows;
    }

    @Override
    public int getType() {
        return TYPE_LEAGUE_TABLE;
    }
}
