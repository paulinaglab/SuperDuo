package barqsoft.footballscores.utils;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;
import barqsoft.footballscores.model.FixtureComing;
import barqsoft.footballscores.model.FixtureScore;

/**
 * Created by Paulina on 2015-12-10.
 */
public class WidgetHelper {

    public static void insertTeamFixtureScoreData(Context context, RemoteViews views, FixtureScore data) {
        if (data == null) {
            views.setViewVisibility(R.id.widget_team_score_empty_state, View.VISIBLE);
            views.setViewVisibility(R.id.widget_team_score_layout, View.GONE);
        } else {
            views.setViewVisibility(R.id.widget_team_score_empty_state, View.GONE);
            views.setViewVisibility(R.id.widget_team_score_layout, View.VISIBLE);

            views.setImageViewResource(R.id.widget_team_score_home_crest,
                    CrestsUtil.getCrestId(context, data.homeTeam.id));
            views.setImageViewResource(R.id.widget_team_score_away_crest,
                    CrestsUtil.getCrestId(context, data.awayTeam.id));

            // Content Descriptions for RemoteViews were only added in ICS MR1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                views.setContentDescription(R.id.widget_team_score_home_crest,
                        context.getString(R.string.description_crest, data.homeTeam.getName()));
                views.setContentDescription(R.id.widget_team_score_away_crest,
                        context.getString(R.string.description_crest, data.awayTeam.getName()));
            }

            views.setTextViewText(R.id.widget_team_score_home_name, data.homeTeam.getName());
            views.setTextViewText(R.id.widget_team_score_away_name, data.awayTeam.getName());

            views.setTextViewText(R.id.widget_team_score_score_text_view,
                    FootballScoresUtils.getScoreString(context, data.homeGoals, data.awayGoals));
        }
    }

    public static void insertTeamFixtureComingData(Context context, RemoteViews views, FixtureComing data) {
        if (data == null) {
            views.setViewVisibility(R.id.widget_team_coming_empty_state, View.VISIBLE);
            views.setViewVisibility(R.id.widget_team_coming_match_layout, View.GONE);
        } else {
            views.setViewVisibility(R.id.widget_team_coming_empty_state, View.GONE);
            views.setViewVisibility(R.id.widget_team_coming_match_layout, View.VISIBLE);

            views.setTextViewText(R.id.widget_team_coming_date_of_fixture,
                    CustomDateFormatter.getDayNameAndTime(context, data.date));

            views.setImageViewResource(R.id.widget_team_coming_home_crest,
                    CrestsUtil.getCrestId(context, data.homeTeam.id));
            views.setImageViewResource(R.id.widget_team_coming_away_crest,
                    CrestsUtil.getCrestId(context, data.awayTeam.id));

            // Content Descriptions for RemoteViews were only added in ICS MR1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                views.setContentDescription(R.id.widget_team_coming_home_crest,
                        context.getString(R.string.description_crest, data.homeTeam.getName()));
                views.setContentDescription(R.id.widget_team_coming_away_crest,
                        context.getString(R.string.description_crest, data.awayTeam.getName()));
            }

            views.setTextViewText(R.id.widget_team_coming_home_name, data.homeTeam.getName());
            views.setTextViewText(R.id.widget_team_coming_away_name, data.awayTeam.getName());
        }
    }
}
