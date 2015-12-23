package barqsoft.footballscores.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.FootballScoresContract;
import barqsoft.footballscores.model.BaseFootballData;
import barqsoft.footballscores.model.HeaderData;
import barqsoft.footballscores.model.League;
import barqsoft.footballscores.model.Team;
import barqsoft.footballscores.utils.CrestsUtil;

/**
 * Created by Paulina on 2015-12-02.
 */
public class CustomizeAdapter extends RecyclerView.Adapter {

    // Item types:
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_LEAGUE = 1;
    public static final int VIEW_TYPE_TEAM = 2;

    private List<BaseFootballData> data;
    private Context context;


    public CustomizeAdapter(Context context, List<BaseFootballData> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER: {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.header_list_item, parent, false);
                return new HeaderHolder(view);
            }
            case VIEW_TYPE_LEAGUE: {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.league_list_item, parent, false);
                final LeagueHolder holder = new LeagueHolder(view);
                holder.checkBox.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                League league = (League) data.get(holder.getAdapterPosition());
                                league.selected = !league.selected;
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(FootballScoresContract.SoccerSeasonsEntry.SELECTED_COLUMN_NAME,
                                        league.selected);
                                context.getContentResolver().update(
                                        FootballScoresContract.SoccerSeasonsEntry.CONTENT_URI,
                                        contentValues,
                                        FootballScoresContract.SoccerSeasonsEntry._ID + "=?",
                                        new String[]{Integer.toString(league.id)});
                            }
                        }
                );
                return holder;
            }
            case VIEW_TYPE_TEAM: {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.team_list_item, parent, false);
                final TeamHolder holder = new TeamHolder(view);
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Team team = (Team) data.get(holder.getAdapterPosition());
                        boolean newValue = !team.selected;
                        for (int i = 0; i < data.size(); i++) {
                            BaseFootballData bsd = data.get(i);
                            if (bsd.equals(team)) {
                                ((Team) bsd).selected = newValue;
                                notifyItemChanged(i);
                            }
                        }
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(FootballScoresContract.TeamsEntry.SELECTED_COLUMN_NAME, newValue);
                        context.getContentResolver().update(
                                FootballScoresContract.TeamsEntry.CONTENT_URI,
                                contentValues,
                                FootballScoresContract.TeamsEntry._ID + "=?",
                                new String[]{Integer.toString(team.id)});
                    }
                });
                return holder;
            }

            default:
                throw new RuntimeException("Unknown view type: " + viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        BaseFootballData item = data.get(position);
        switch (item.getType()) {
            case BaseFootballData.TYPE_HEADER:
                return VIEW_TYPE_HEADER;
            case BaseFootballData.TYPE_LEAGUE:
                return VIEW_TYPE_LEAGUE;
            case BaseFootballData.TYPE_TEAM:
                return VIEW_TYPE_TEAM;
            default:
                throw new RuntimeException("View not supported");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        switch (type) {
            case VIEW_TYPE_HEADER: {
                HeaderData itemData = (HeaderData) data.get(position);
                ((HeaderHolder) holder).text.setText(itemData.header);
                break;
            }
            case VIEW_TYPE_LEAGUE: {
                League itemData = (League) data.get(position);
                LeagueHolder leagueHolder = (LeagueHolder) holder;
                leagueHolder.checkBox.setText(itemData.caption);
                leagueHolder.checkBox.setChecked(itemData.selected);
                break;
            }
            case VIEW_TYPE_TEAM: {
                Team itemData = (Team) data.get(position);
                TeamHolder teamHolder = (TeamHolder) holder;
                teamHolder.crest.setImageResource(CrestsUtil.getCrestId(context, itemData.id));
                teamHolder.crest.setContentDescription(
                                context.getString(R.string.description_crest, itemData.getName()));
                teamHolder.checkBox.setText(itemData.getName());
                teamHolder.checkBox.setChecked(itemData.selected);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class HeaderHolder extends RecyclerView.ViewHolder {

        TextView text;

        public HeaderHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.header_text);
        }
    }

    class LeagueHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;

        public LeagueHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.league_list_item_check_box);
        }
    }

    class TeamHolder extends RecyclerView.ViewHolder {

        ImageView crest;
        CheckBox checkBox;

        public TeamHolder(View itemView) {
            super(itemView);
            crest = (ImageView) itemView.findViewById(R.id.team_list_item_crest);
            checkBox = (CheckBox) itemView.findViewById(R.id.team_list_item_check_box);
        }
    }

}
