package barqsoft.footballscores.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.model.BaseFootballData;
import barqsoft.footballscores.model.League;

/**
 * Created by Paulina on 2015-12-11.
 */
public class LeagueWidgetCustomizationAdapter extends RecyclerView.Adapter<LeagueWidgetCustomizationAdapter.LeagueHolder> {

    private Context context;
    private List<BaseFootballData> data;
    private OnItemClickListener listener;

    public LeagueWidgetCustomizationAdapter(Context context,
                                            List<BaseFootballData> data,
                                            OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public LeagueHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.league_widget_customization_list_item, parent, false);
        final LeagueHolder leagueHolder = new LeagueHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.leagueClicked((League) data.get(leagueHolder.getAdapterPosition()));
            }
        });
        return leagueHolder;
    }

    @Override
    public void onBindViewHolder(LeagueHolder holder, int position) {
        holder.text.setText(((League) data.get(position)).caption);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class LeagueHolder extends RecyclerView.ViewHolder {

        TextView text;

        public LeagueHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.league_widget_customization_text);
        }
    }

    public interface OnItemClickListener {
        void leagueClicked(League league);
    }
}
