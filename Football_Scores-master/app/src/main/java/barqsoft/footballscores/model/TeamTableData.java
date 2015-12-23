package barqsoft.footballscores.model;

import java.util.List;


/**
 * Created by Paulina on 2015-11-23.
 */
public class TeamTableData extends LeagueTableData {

    public Team watchedTeam;


    public TeamTableData(League league, Team watchedTeam, List<FootballTableRow> footballTableRows) {
        super(league, footballTableRows);
        this.watchedTeam = watchedTeam;
    }

    @Override
    public int getType() {
        return TYPE_TEAM_TABLE;
    }
}
