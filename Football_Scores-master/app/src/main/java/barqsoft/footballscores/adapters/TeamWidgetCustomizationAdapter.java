package barqsoft.footballscores.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.model.BaseFootballData;
import barqsoft.footballscores.model.HeaderData;
import barqsoft.footballscores.model.Team;
import barqsoft.footballscores.utils.CrestsUtil;

/**
 * Created by Paulina on 2015-12-11.
 */
public class TeamWidgetCustomizationAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_TEAM = 2;
    private Context context;
    private List<BaseFootballData> data;
    private OnItemClickListener listener;

    public TeamWidgetCustomizationAdapter(Context context,
                                          List<BaseFootballData> data,
                                          OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER: {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.header_list_item, parent, false);
                return new HeaderHolder(view);
            }
            case VIEW_TYPE_TEAM: {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.team_widget_customization_list_item, parent, false);
                final TeamHolder holder = new TeamHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.teamClicked((Team) data.get(holder.getAdapterPosition()));
                    }
                });
                return holder;
            }
            default:
                throw new RuntimeException("View type not supported.");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_HEADER: {
                HeaderData headerData = (HeaderData) data.get(position);
                HeaderHolder headerHolder = (HeaderHolder) holder;
                headerHolder.text.setText(headerData.header);
                break;
            }
            case VIEW_TYPE_TEAM: {
                Team team = (Team) data.get(position);
                TeamHolder teamHolder = (TeamHolder) holder;
                teamHolder.teamName.setText(team.getName());
                teamHolder.crest.setImageResource(CrestsUtil.getCrestId(context, team.id));
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (data.get(position).getType()) {
            case BaseFootballData.TYPE_HEADER:
                return VIEW_TYPE_HEADER;
            case BaseFootballData.TYPE_TEAM:
                return VIEW_TYPE_TEAM;
            default:
                throw new RuntimeException("View not supported.");
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder {

        TextView text;

        public HeaderHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.header_text);
        }
    }

    class TeamHolder extends RecyclerView.ViewHolder {

        ImageView crest;
        TextView teamName;

        public TeamHolder(View itemView) {
            super(itemView);
            crest = (ImageView) itemView.findViewById(R.id.team_list_item_crest);
            teamName = (TextView) itemView.findViewById(R.id.team_list_item_text);
        }
    }

    public interface OnItemClickListener {
        void teamClicked(Team team);
    }
}
