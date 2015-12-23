package barqsoft.footballscores.widgets;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;
import barqsoft.footballscores.activities.MainActivity;
import barqsoft.footballscores.activities.TeamWidgetConfigActivity;
import barqsoft.footballscores.data.DataUtil;
import barqsoft.footballscores.model.Team;
import barqsoft.footballscores.model.TeamFixtureComing;
import barqsoft.footballscores.model.TeamFixtureScore;
import barqsoft.footballscores.utils.WidgetHelper;

/**
 * Created by Paulina on 2015-12-09.
 */
public class TeamNewsWidgetIntentService extends IntentService {


    public TeamNewsWidgetIntentService() {
        super(TeamNewsWidgetIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, TeamNewsWidgetProvider.class));


        for (int appWidgetId : appWidgetIds) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            int teamId;
            String teamIdStr = prefs.getString(TeamWidgetConfigActivity.WIDGET_PREFIX + appWidgetId, null);
            if (teamIdStr == null || !teamIdStr.startsWith(TeamWidgetConfigActivity.TEAM_PREFIX))
                continue;
            else
                teamId = Integer.parseInt(
                        teamIdStr.substring(TeamWidgetConfigActivity.TEAM_PREFIX.length()));

            Team team = DataUtil.loadTeam(this, teamId);
            TeamFixtureComing teamFixtureComing = DataUtil.loadTeamComingFixture(this, teamId);
            TeamFixtureScore teamFixtureScore = DataUtil.loadTeamFixtureScore(this, teamId);

            int layoutId = R.layout.widget_team_news;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            views.setTextViewText(R.id.widget_team_name_title, team.getName());

            if (teamFixtureScore != null)
                WidgetHelper.insertTeamFixtureScoreData(this, views, teamFixtureScore.fixtureScore);
            else
                WidgetHelper.insertTeamFixtureScoreData(this, views, null);

            if (teamFixtureComing != null)
                WidgetHelper.insertTeamFixtureComingData(this, views, teamFixtureComing.fixtureComing);
            else
                WidgetHelper.insertTeamFixtureComingData(this, views, null);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
