package barqsoft.footballscores.adapters;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import barqsoft.footballscores.FootballScores;
import barqsoft.footballscores.R;
import barqsoft.footballscores.model.BaseFootballData;
import barqsoft.footballscores.model.FixtureComing;
import barqsoft.footballscores.model.FixtureScore;
import barqsoft.footballscores.model.FootballTableRow;
import barqsoft.footballscores.model.HeaderData;
import barqsoft.footballscores.model.LeagueFixturesComing;
import barqsoft.footballscores.model.LeagueFixturesScores;
import barqsoft.footballscores.model.LeagueTableData;
import barqsoft.footballscores.model.TeamFixtureComing;
import barqsoft.footballscores.model.TeamFixtureScore;
import barqsoft.footballscores.model.TeamTableData;
import barqsoft.footballscores.utils.CrestsUtil;
import barqsoft.footballscores.utils.CustomDateFormatter;
import barqsoft.footballscores.utils.FootballScoresUtils;


/**
 * Created by Paulina on 2015-11-23.
 */
public class MainScreenAdapter extends RecyclerView.Adapter {

    // Columns in football table (layout)
    static final int COLUMN_ORDER_INDEX = 0;
    static final int COLUMN_CREST_INDEX = 1;
    static final int COLUMN_NAME_INDEX = 2;
    static final int COLUMN_MATCHES_PLAYED_INDEX = 3;
    static final int COLUMN_GOALS_SCORED_INDEX = 4;
    static final int COLUMN_GOALS_LOST_INDEX = 5;
    static final int COLUMN_POINTS_INDEX = 6;

    // Views in row_fixture_score_layout.xml
    static final int FIXTURE_SCORE_ROW_HOME_NAME_INDEX = 0;
    static final int FIXTURE_SCORE_ROW_HOME_CREST_INDEX = 1;
    static final int FIXTURE_SCORE_ROW_SCORE_INDEX = 2;
    static final int FIXTURE_SCORE_ROW_AWAY_CREST_INDEX = 3;
    static final int FIXTURE_SCORE_ROW_AWAY_NAME_INDEX = 4;

    // Views in row_coming_fixture_layout.xml
    static final int COMING_FIXTURE_ROW_HOME_NAME_INDEX = 0;
    static final int COMING_FIXTURE_ROW_HOME_CREST_INDEX = 1;
    static final int COMING_FIXTURE_ROW_AWAY_CREST_INDEX = 3;
    static final int COMING_FIXTURE_ROW_AWAY_NAME_INDEX = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            VIEW_TYPE_HEADER,
            VIEW_TYPE_TEAM_FIXTURES_SCORE,
            VIEW_TYPE_TEAM_FIXTURES_COMING,
            VIEW_TYPE_TEAM_TABLE,
            VIEW_TYPE_LEAGUE_FIXTURES_COMING,
            VIEW_TYPE_LEAGUE_TABLE,
            VIEW_TYPE_LEAGUE_FIXTURE_SCORES})
    public @interface ViewType {
    }

    // View's context header
    public static final int VIEW_TYPE_HEADER = 0;
    // Watched team items
    public static final int VIEW_TYPE_TEAM_FIXTURES_SCORE = 1;
    public static final int VIEW_TYPE_TEAM_FIXTURES_COMING = 2;
    public static final int VIEW_TYPE_TEAM_TABLE = 3;
    // Watched league items
    public static final int VIEW_TYPE_LEAGUE_FIXTURES_COMING = 4;
    public static final int VIEW_TYPE_LEAGUE_TABLE = 5;
    public static final int VIEW_TYPE_LEAGUE_FIXTURE_SCORES = 6;

    private List<BaseFootballData> data;
    private Context context;
    private OnCardMenuClickListener cardMenuClickListener;

    public MainScreenAdapter(Context context, List<BaseFootballData> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        switch (type) {
            // Header
            case VIEW_TYPE_HEADER: {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.group_header_item, parent, false);
                // Setting full span for headers
                StaggeredGridLayoutManager.LayoutParams layoutParams =
                        new StaggeredGridLayoutManager.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);
                view.setLayoutParams(layoutParams);

                return new HeaderHolder(view);
            }

            // Teams:
            case VIEW_TYPE_TEAM_FIXTURES_COMING: {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.card_team_fixture_coming, parent, false);
                return new TeamFixtureComingHolder(view);
            }

            case VIEW_TYPE_TEAM_FIXTURES_SCORE: {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.card_team_fixture_score, parent, false);
                return new TeamFixtureScoreHolder(view);
            }

            case VIEW_TYPE_TEAM_TABLE: {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.card_team_table_short, parent, false);
                return new TeamTableShortHolder(view);
            }

            // Leagues:
            case VIEW_TYPE_LEAGUE_FIXTURE_SCORES: {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.card_league_fixtures_scores, parent, false);
                return new LeagueFixturesScoresHolder(view);
            }

            case VIEW_TYPE_LEAGUE_FIXTURES_COMING: {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.card_league_fixtures_coming, parent, false);
                return new LeagueFixturesComingHolder(view);
            }

            case VIEW_TYPE_LEAGUE_TABLE: {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.card_league_table, parent, false);
                return new LeagueTableHolder(view);
            }

            default:
                throw new RuntimeException("Unknown view type: " + type);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);

        switch (type) {
            case VIEW_TYPE_HEADER: {
                HeaderData itemData = (HeaderData) data.get(position);
                ((HeaderHolder) viewHolder).headerTextView.setText(itemData.header);
                break;
            }
            case VIEW_TYPE_TEAM_FIXTURES_SCORE: {
                FixtureScore itemData = ((TeamFixtureScore) data.get(position)).fixtureScore;
                TeamFixtureScoreHolder holder = (TeamFixtureScoreHolder) viewHolder;

                String titleConnective = context.getResources().getString(R.string.match_card_title_connective);
                holder.cardTitle.setText(itemData.league.caption +
                        titleConnective +
                        context.getResources().getString(R.string.cards_matchday_label, itemData.matchday) +
                        titleConnective +
                        CustomDateFormatter.getDayName(context, itemData.date));

                holder.score.setText(
                        FootballScoresUtils.getScoreString(context, itemData.homeGoals, itemData.awayGoals));
                holder.homeName.setText(itemData.homeTeam.getName());
                holder.awayName.setText(itemData.awayTeam.getName());
                holder.homeCrest.setImageDrawable(
                        context.getResources().getDrawable(
                                CrestsUtil.getCrestId(context, itemData.homeTeam.id)));
                holder.awayCrest.setImageDrawable(
                        context.getResources().getDrawable(
                                CrestsUtil.getCrestId(context, itemData.awayTeam.id)));
                holder.homeCrest.setContentDescription(
                        context.getString(R.string.description_crest, itemData.homeTeam.getName()));
                holder.awayCrest.setContentDescription(
                        context.getString(R.string.description_crest, itemData.awayTeam.getName()));
                break;
            }
            case VIEW_TYPE_TEAM_FIXTURES_COMING: {
                FixtureComing itemData = ((TeamFixtureComing) data.get(position)).fixtureComing;
                TeamFixtureComingHolder holder = (TeamFixtureComingHolder) viewHolder;

                String titleConnective = context.getResources().getString(R.string.match_card_title_connective);
                holder.cardTitle.setText(itemData.league.caption +
                        titleConnective +
                        context.getResources().getString(R.string.cards_matchday_label, itemData.matchday));


                holder.dateOfFixture.setText(CustomDateFormatter.getDayNameAndTime(context, itemData.date));
                holder.homeName.setText(itemData.homeTeam.getName());
                holder.awayName.setText(itemData.awayTeam.getName());
                holder.homeCrest.setImageDrawable(
                        context.getResources().getDrawable(
                                CrestsUtil.getCrestId(context, itemData.homeTeam.id)));
                holder.awayCrest.setImageDrawable(
                        context.getResources().getDrawable(
                                CrestsUtil.getCrestId(context, itemData.awayTeam.id)));
                holder.homeCrest.setContentDescription(
                        context.getString(R.string.description_crest, itemData.homeTeam.getName()));
                holder.awayCrest.setContentDescription(
                        context.getString(R.string.description_crest, itemData.awayTeam.getName()));

                break;
            }
            case VIEW_TYPE_TEAM_TABLE: {
                TeamTableData itemData = (TeamTableData) data.get(position);
                TeamTableShortHolder holder = (TeamTableShortHolder) viewHolder;

                String titleConnective = context.getResources().getString(R.string.match_card_title_connective);
                String tableLabel = context.getResources().getString(R.string.cards_table_label);
                holder.cardTitle.setText(itemData.league.caption + titleConnective + tableLabel);


                // Check watched team position - is it first or last?

                int watchedTeamIndex = -1;
                int lastIndex = itemData.footballTableRows.size() - 1;
                for (int i = 0; i <= lastIndex; i++) {
                    if (itemData.watchedTeam.equals(itemData.footballTableRows.get(i).team)) {
                        watchedTeamIndex = i;
                        break;
                    }
                }

                if (watchedTeamIndex == -1) {
                    throw new RuntimeException("Watched team not found in table!");
                }

                if (watchedTeamIndex == 0) {
                    // Watched team is FIRST in the table.
                    bindTableRow(holder.watchedTeamRow, itemData.footballTableRows.get(0));
                    bindTableRow(holder.postRow, itemData.footballTableRows.get(1));
                    bindTableRow(holder.postOptRow, itemData.footballTableRows.get(2));
                    holder.preOptRow.setVisibility(View.GONE);
                    holder.preRow.setVisibility(View.GONE);
                    holder.postRow.setVisibility(View.VISIBLE);
                    holder.postOptRow.setVisibility(View.VISIBLE);

                } else if (watchedTeamIndex == itemData.footballTableRows.size() - 1) {
                    // Watched team is LAST in the table.
                    bindTableRow(holder.preOptRow, itemData.footballTableRows.get(lastIndex - 2));
                    bindTableRow(holder.preRow, itemData.footballTableRows.get(lastIndex - 1));
                    bindTableRow(holder.watchedTeamRow, itemData.footballTableRows.get(lastIndex));
                    holder.preOptRow.setVisibility(View.VISIBLE);
                    holder.preRow.setVisibility(View.VISIBLE);
                    holder.postRow.setVisibility(View.GONE);
                    holder.postOptRow.setVisibility(View.GONE);

                } else {
                    // Watched team is INSIDE the table.
                    bindTableRow(holder.preRow, itemData.footballTableRows.get(0));
                    bindTableRow(holder.watchedTeamRow, itemData.footballTableRows.get(1));
                    bindTableRow(holder.postRow, itemData.footballTableRows.get(2));
                    holder.preOptRow.setVisibility(View.GONE);
                    holder.preRow.setVisibility(View.VISIBLE);
                    holder.postRow.setVisibility(View.VISIBLE);
                    holder.postOptRow.setVisibility(View.GONE);
                }
                break;
            }
            case VIEW_TYPE_LEAGUE_FIXTURES_COMING: {
                LeagueFixturesComing itemData = (LeagueFixturesComing) data.get(position);
                LeagueFixturesComingHolder holder = (LeagueFixturesComingHolder) viewHolder;

                String titleConnective = context.getResources().getString(R.string.match_card_title_connective);
                String comingLabel = context.getResources().getString(R.string.cards_coming_label);
                holder.cardTitle.setText(itemData.league.caption + titleConnective + comingLabel);

                holder.rowsContainer.removeAllViews();
                for (int i = 0; i < itemData.fixturesData.size(); i++) {
                    int layoutResId;
                    switch (itemData.fixturesData.get(i).getType()) {
                        case BaseFootballData.TYPE_HEADER:
                            layoutResId = R.layout.row_coming_fixture_date_layout;
                            break;
                        case BaseFootballData.TYPE_LEAGUE_FIXTURES_COMING:
                            layoutResId = R.layout.row_coming_fixture_layout;
                            break;
                        default:
                            throw new RuntimeException("Unsupported view type: " + type);
                    }
                    // Inflate row
                    LayoutInflater.from(context).inflate(
                            layoutResId, holder.rowsContainer, true);
                    // Bind
                    bindComingFixtureRow(
                            holder.rowsContainer.getChildAt(i),
                            itemData.fixturesData.get(i));
                }
                break;
            }
            case VIEW_TYPE_LEAGUE_TABLE: {
                LeagueTableData itemData = (LeagueTableData) data.get(position);
                LeagueTableHolder holder = (LeagueTableHolder) viewHolder;

                String titleConnective = context.getResources().getString(R.string.match_card_title_connective);
                String tableLabel = context.getResources().getString(R.string.cards_table_label);
                holder.cardTitle.setText(itemData.league.caption + titleConnective + tableLabel);

                int deltaRows = holder.rowsContainer.getChildCount() - itemData.footballTableRows.size();
                if (deltaRows > 0) {
                    holder.rowsContainer.removeViews(holder.rowsContainer.getChildCount() - deltaRows, deltaRows);
                } else if (deltaRows < 0) {
                    for (int i = 0; i < Math.abs(deltaRows); i++) {
                        // Inflate row
                        LayoutInflater.from(context).inflate(
                                R.layout.league_table_row_layout, holder.rowsContainer, true);
                    }
                }
                for (int i = 0; i < itemData.footballTableRows.size(); i++) {
                    // Bind
                    bindTableRow(
                            (TableRow) holder.rowsContainer.getChildAt(i),
                            itemData.footballTableRows.get(i));
                }
                break;
            }
            case VIEW_TYPE_LEAGUE_FIXTURE_SCORES: {
                LeagueFixturesScores itemData = (LeagueFixturesScores) data.get(position);
                LeagueFixturesScoresHolder holder = (LeagueFixturesScoresHolder) viewHolder;

                String titleConnective = context.getResources().getString(R.string.match_card_title_connective);
                String scoresLabel = context.getResources().getString(R.string.cards_scores_label);
                holder.cardTitle.setText(itemData.league.caption + titleConnective + scoresLabel);

                holder.rowsContainer.removeAllViews();
                for (int i = 0; i < itemData.fixturesData.size(); i++) {
                    int layoutResId;
                    switch (itemData.fixturesData.get(i).getType()) {
                        case BaseFootballData.TYPE_HEADER:
                            layoutResId = R.layout.row_fixture_score_date_layout;
                            break;
                        case BaseFootballData.TYPE_LEAGUE_FIXTURES_SCORES:
                            layoutResId = R.layout.row_fixture_score_layout;
                            break;
                        default:
                            throw new RuntimeException("Unsupported view type: " + type);
                    }
                    // Inflate row
                    LayoutInflater.from(context).inflate(
                            layoutResId, holder.rowsContainer, true);
                    // Bind
                    bindFixtureScoreRow(
                            holder.rowsContainer.getChildAt(i),
                            itemData.fixturesData.get(i));
                }
                break;
            }
        }
    }

    private void bindTableRow(TableRow tableRow, FootballTableRow tableRowData) {
        ((TextView) tableRow.getChildAt(COLUMN_ORDER_INDEX))
                .setText(context.getResources().getString(R.string.cards_table_order_format, tableRowData.order));
        ((ImageView) tableRow.getChildAt(COLUMN_CREST_INDEX))
                .setImageDrawable(context.getResources().getDrawable(
                        CrestsUtil.getCrestId(context, tableRowData.team.id)));
        tableRow.getChildAt(COLUMN_CREST_INDEX).setContentDescription(
                context.getString(R.string.description_crest, tableRowData.team.getName()));
        ((TextView) tableRow.getChildAt(COLUMN_NAME_INDEX))
                .setText(tableRowData.team.getName());
        ((TextView) tableRow.getChildAt(COLUMN_MATCHES_PLAYED_INDEX))
                .setText(Integer.toString(tableRowData.matchesPlayed));
        ((TextView) tableRow.getChildAt(COLUMN_GOALS_SCORED_INDEX))
                .setText(Integer.toString(tableRowData.goalsScored));
        ((TextView) tableRow.getChildAt(COLUMN_GOALS_LOST_INDEX))
                .setText(Integer.toString(tableRowData.goalsLost));
        ((TextView) tableRow.getChildAt(COLUMN_POINTS_INDEX))
                .setText(Integer.toString(tableRowData.points));
    }

    private void bindComingFixtureRow(View view, BaseFootballData data) {
        switch (data.getType()) {
            case BaseFootballData.TYPE_HEADER:
                HeaderData headerData = (HeaderData) data;
                TextView headerView = (TextView) view;

                // Format this date to 'Day-name, hh:mm'
                try {
                    Date date = FootballScores.DATE_DEFAULT_FORMAT.parse(headerData.header);
                    headerView.setText(CustomDateFormatter.getDayNameAndTime(context, date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                break;

            case BaseFootballData.TYPE_LEAGUE_FIXTURES_COMING:
                FixtureComing fixtureComing = (FixtureComing) data;
                ViewGroup fixtureLayout = (ViewGroup) view;

                ((TextView) fixtureLayout.getChildAt(COMING_FIXTURE_ROW_HOME_NAME_INDEX)).setText(
                        fixtureComing.homeTeam.getName());

                ((ImageView) fixtureLayout.getChildAt(COMING_FIXTURE_ROW_HOME_CREST_INDEX)).setImageDrawable(
                        context.getResources().getDrawable(
                                CrestsUtil.getCrestId(context, fixtureComing.homeTeam.id)));
                fixtureLayout.getChildAt(COMING_FIXTURE_ROW_HOME_CREST_INDEX).setContentDescription(
                        context.getString(R.string.description_crest, ((FixtureComing) data).homeTeam.getName()));

                ((ImageView) fixtureLayout.getChildAt(COMING_FIXTURE_ROW_AWAY_CREST_INDEX)).setImageDrawable(
                        context.getResources().getDrawable(
                                CrestsUtil.getCrestId(context, fixtureComing.awayTeam.id)));
                fixtureLayout.getChildAt(COMING_FIXTURE_ROW_AWAY_CREST_INDEX).setContentDescription(
                        context.getString(R.string.description_crest, ((FixtureComing) data).awayTeam.getName()));

                ((TextView) fixtureLayout.getChildAt(COMING_FIXTURE_ROW_AWAY_NAME_INDEX)).setText(
                        fixtureComing.awayTeam.getName());

                break;
        }
    }

    private void bindFixtureScoreRow(View view, BaseFootballData data) {
        switch (data.getType()) {
            case BaseFootballData.TYPE_HEADER:
                HeaderData headerData = (HeaderData) data;
                TextView headerView = (TextView) view;

                // Format this date to 'Day-name'
                try {
                    Date date = FootballScores.DATE_DEFAULT_FORMAT.parse(headerData.header);
                    headerView.setText(CustomDateFormatter.getDayName(context, date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                break;

            case BaseFootballData.TYPE_LEAGUE_FIXTURES_SCORES:
                FixtureScore fixtureScore = (FixtureScore) data;
                ViewGroup fixtureLayout = (ViewGroup) view;

                ((TextView) fixtureLayout.getChildAt(FIXTURE_SCORE_ROW_HOME_NAME_INDEX)).setText(
                        fixtureScore.homeTeam.getName());

                ((ImageView) fixtureLayout.getChildAt(FIXTURE_SCORE_ROW_HOME_CREST_INDEX)).setImageDrawable(
                        context.getResources().getDrawable(
                                CrestsUtil.getCrestId(context, fixtureScore.homeTeam.id)));
                fixtureLayout.getChildAt(FIXTURE_SCORE_ROW_HOME_CREST_INDEX).setContentDescription(
                        context.getString(R.string.description_crest, ((FixtureComing) data).homeTeam.getName()));

                ((TextView) fixtureLayout.getChildAt(FIXTURE_SCORE_ROW_SCORE_INDEX)).setText(
                        FootballScoresUtils.getScoreString(context, fixtureScore.homeGoals, fixtureScore.awayGoals));

                ((ImageView) fixtureLayout.getChildAt(FIXTURE_SCORE_ROW_AWAY_CREST_INDEX)).setImageDrawable(
                        context.getResources().getDrawable(
                                CrestsUtil.getCrestId(context, fixtureScore.awayTeam.id)));
                fixtureLayout.getChildAt(FIXTURE_SCORE_ROW_AWAY_CREST_INDEX).setContentDescription(
                        context.getString(R.string.description_crest, ((FixtureComing) data).awayTeam.getName()));

                ((TextView) fixtureLayout.getChildAt(FIXTURE_SCORE_ROW_AWAY_NAME_INDEX)).setText(
                        fixtureScore.awayTeam.getName());

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        BaseFootballData item = data.get(position);
        switch (item.getType()) {
            case BaseFootballData.TYPE_HEADER:
                return VIEW_TYPE_HEADER;
            case BaseFootballData.TYPE_TEAM_FIXTURE_SCORE:
                return VIEW_TYPE_TEAM_FIXTURES_SCORE;
            case BaseFootballData.TYPE_TEAM_FIXTURE_COMING:
                return VIEW_TYPE_TEAM_FIXTURES_COMING;
            case BaseFootballData.TYPE_TEAM_TABLE:
                return VIEW_TYPE_TEAM_TABLE;
            case BaseFootballData.TYPE_LEAGUE_FIXTURES_COMING:
                return VIEW_TYPE_LEAGUE_FIXTURES_COMING;
            case BaseFootballData.TYPE_LEAGUE_TABLE:
                return VIEW_TYPE_LEAGUE_TABLE;
            case BaseFootballData.TYPE_LEAGUE_FIXTURES_SCORES:
                return VIEW_TYPE_LEAGUE_FIXTURE_SCORES;
            default:
                throw new RuntimeException("View not supported");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<BaseFootballData> data) {
        this.data = data;
    }


    public interface OnCardMenuClickListener {

        void openMenu(@MainScreenAdapter.ViewType int viewType, BaseFootballData data);

    }

    public void setOnCardMenuClickListener(OnCardMenuClickListener cardMenuClickListener) {
        this.cardMenuClickListener = cardMenuClickListener;
    }

    //
    //  HOLDERS
    //

    class HeaderHolder extends RecyclerView.ViewHolder {

        TextView headerTextView;

        public HeaderHolder(View itemView) {
            super(itemView);
            headerTextView = (TextView) itemView.findViewById(R.id.group_header);
        }
    }

    abstract class TitleBarCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView cardTitle;
        ImageButton menuButton;

        public TitleBarCardHolder(View itemView) {
            super(itemView);
            cardTitle = (TextView) itemView.findViewById(R.id.card_title_bar_text_view);
            menuButton = (ImageButton) itemView.findViewById(R.id.card_title_bar_menu_button);
            menuButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == menuButton.getId()) {
                openMenu();
            }
        }

        protected void openMenu() {
            if (cardMenuClickListener != null) {
                int itemPos = getAdapterPosition();
                //noinspection ResourceType
                cardMenuClickListener.openMenu(getItemViewType(), data.get(itemPos));
            }
        }
    }

    class TeamFixtureComingHolder extends TitleBarCardHolder {

        TextView dateOfFixture;
        ImageView homeCrest;
        TextView homeName;
        ImageView awayCrest;
        TextView awayName;

        public TeamFixtureComingHolder(View itemView) {
            super(itemView);
            dateOfFixture = (TextView) itemView.findViewById(R.id.ctfc_date_of_fixture);
            homeCrest = (ImageView) itemView.findViewById(R.id.ctfc_home_crest);
            homeName = (TextView) itemView.findViewById(R.id.ctfc_home_name);
            awayCrest = (ImageView) itemView.findViewById(R.id.ctfc_away_crest);
            awayName = (TextView) itemView.findViewById(R.id.ctfc_away_name);
        }
    }

    class TeamFixtureScoreHolder extends TitleBarCardHolder {

        TextView score;
        ImageView homeCrest;
        TextView homeName;
        ImageView awayCrest;
        TextView awayName;

        public TeamFixtureScoreHolder(View itemView) {
            super(itemView);
            score = (TextView) itemView.findViewById(R.id.ctfs_score_text_view);
            homeCrest = (ImageView) itemView.findViewById(R.id.ctfs_home_crest);
            homeName = (TextView) itemView.findViewById(R.id.ctfs_home_name);
            awayCrest = (ImageView) itemView.findViewById(R.id.ctfs_away_crest);
            awayName = (TextView) itemView.findViewById(R.id.ctfs_away_name);
        }
    }

    class TeamTableShortHolder extends TitleBarCardHolder {

        TableRow preOptRow;
        TableRow preRow;
        TableRow watchedTeamRow;
        TableRow postRow;
        TableRow postOptRow;

        public TeamTableShortHolder(View itemView) {
            super(itemView);
            preOptRow = (TableRow) itemView.findViewById(R.id.table_row_pre_optional);
            preRow = (TableRow) itemView.findViewById(R.id.table_row_pre);
            watchedTeamRow = (TableRow) itemView.findViewById(R.id.watched_team_row);
            postRow = (TableRow) itemView.findViewById(R.id.table_row_post);
            postOptRow = (TableRow) itemView.findViewById(R.id.table_row_post_optional);
            // For now, menu is not supported:
            menuButton.setVisibility(View.INVISIBLE);
        }
    }

    class LeagueFixturesComingHolder extends TitleBarCardHolder {

        LinearLayout rowsContainer;

        public LeagueFixturesComingHolder(View itemView) {
            super(itemView);
            rowsContainer = (LinearLayout) itemView.findViewById(R.id.rows_container);
            // For now, menu is not supported:
            menuButton.setVisibility(View.INVISIBLE);
        }
    }

    class LeagueFixturesScoresHolder extends TitleBarCardHolder {

        LinearLayout rowsContainer;

        public LeagueFixturesScoresHolder(View itemView) {
            super(itemView);
            rowsContainer = (LinearLayout) itemView.findViewById(R.id.rows_container);
            // For now, menu is not supported:
            menuButton.setVisibility(View.INVISIBLE);
        }
    }

    class LeagueTableHolder extends TitleBarCardHolder {

        TableLayout rowsContainer;

        public LeagueTableHolder(View itemView) {
            super(itemView);
            rowsContainer = (TableLayout) itemView.findViewById(R.id.table_rows_container);
            // For now, menu is not supported:
            menuButton.setVisibility(View.INVISIBLE);
        }
    }
}
