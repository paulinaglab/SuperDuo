package barqsoft.footballscores.widgets;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;
import barqsoft.footballscores.activities.LeagueWidgetConfigActivity;
import barqsoft.footballscores.activities.MainActivity;
import barqsoft.footballscores.activities.TeamWidgetConfigActivity;
import barqsoft.footballscores.data.DataUtil;
import barqsoft.footballscores.model.League;

/**
 * Created by Paulina on 2015-12-10.
 */
public class LeagueNewsWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_league_news);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String leagueIdStr =
                    preferences.getString(TeamWidgetConfigActivity.WIDGET_PREFIX + appWidgetId, null);

            if (leagueIdStr == null ||
                    !leagueIdStr.startsWith(LeagueWidgetConfigActivity.LEAGUE_PREFIX))
                continue;

            int leagueId = Integer.parseInt(leagueIdStr.substring(LeagueWidgetConfigActivity.LEAGUE_PREFIX.length()));
            League league = DataUtil.loadLeague(context, leagueId);
            setLeague(views, league);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            views.setEmptyView(R.id.widget_league_news_list, android.R.id.empty);

            setRemoteViewsAdapter(context, views, appWidgetId);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_league_news_list);
    }

    public static void setRemoteViewsAdapter(Context context,
                                             @NonNull final RemoteViews views,
                                             int appWidgetId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views, appWidgetId);
        } else {
            setRemoteAdapterV11(context, views, appWidgetId);
        }
    }

    public static void setLeague(RemoteViews views, League league) {
        views.setTextViewText(R.id.widget_league_news_title, league.caption);
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views, int widgetId) {
        Intent intent = new Intent(context, LeagueNewsWidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.setAction(TeamWidgetConfigActivity.WIDGET_PREFIX + widgetId);
        views.setRemoteAdapter(R.id.widget_league_news_list, intent);
    }


    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views, int widgetId) {
        Intent intent = new Intent(context, LeagueNewsWidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.setAction(TeamWidgetConfigActivity.WIDGET_PREFIX + widgetId);
        views.setRemoteAdapter(0, R.id.widget_league_news_list, intent);
    }
}
