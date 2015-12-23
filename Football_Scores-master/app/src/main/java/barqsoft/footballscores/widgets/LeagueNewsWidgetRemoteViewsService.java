package barqsoft.footballscores.widgets;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.activities.LeagueWidgetConfigActivity;
import barqsoft.footballscores.activities.TeamWidgetConfigActivity;
import barqsoft.footballscores.data.DataUtil;
import barqsoft.footballscores.model.BaseFootballData;
import barqsoft.footballscores.model.FixtureComing;
import barqsoft.footballscores.model.FixtureScore;
import barqsoft.footballscores.utils.WidgetHelper;

/**
 * Created by Paulina on 2015-12-10.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LeagueNewsWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LeagueNewsWidgetRemoteViewsService.this);
        String leagueIdStr = prefs.getString(
                TeamWidgetConfigActivity.WIDGET_PREFIX + widgetId,
                LeagueWidgetConfigActivity.LEAGUE_PREFIX + 394);

        int leagueId = Integer.parseInt(
                leagueIdStr.substring(LeagueWidgetConfigActivity.LEAGUE_PREFIX.length()));
        return new LeagueFixturesFactory(leagueId);
    }

    class LeagueFixturesFactory implements RemoteViewsFactory {
        private static final int VIEW_TYPE_SCORE = 0;
        private static final int VIEW_TYPE_COMING = 1;

        private List<BaseFootballData> dataList;
        private int leagueId;

        public LeagueFixturesFactory(int leagueId) {
            this.leagueId = leagueId;
        }

        @Override
        public void onCreate() {
            // Nothing to do
        }

        @Override
        public void onDataSetChanged() {
            long identityToken = Binder.clearCallingIdentity();
            dataList = new ArrayList<>();
            List<FixtureComing> coming =
                    DataUtil.loadLeagueFixturesComing(LeagueNewsWidgetRemoteViewsService.this, leagueId);
            List<FixtureScore> scores =
                    DataUtil.loadLeagueFixtureScores(LeagueNewsWidgetRemoteViewsService.this, leagueId);

            dataList.addAll(coming);
            dataList.addAll(scores);
            Collections.sort(dataList, new Comparator<BaseFootballData>() {
                @Override
                public int compare(BaseFootballData lhs, BaseFootballData rhs) {
                    Calendar calendar = Calendar.getInstance();
                    long now = calendar.getTimeInMillis();
                    FixtureComing lhsFixtureComing = (FixtureComing) lhs;
                    FixtureComing rhsFixtureComing = (FixtureComing) rhs;
                    long lhsNowDelta = Math.abs(lhsFixtureComing.date.getTime() - now);
                    long rhsNowDelta = Math.abs(rhsFixtureComing.date.getTime() - now);
                    if (lhsNowDelta != rhsNowDelta)
                        return (int) (lhsNowDelta - rhsNowDelta);
                    else
                        return lhsFixtureComing.homeTeam.id - rhsFixtureComing.homeTeam.id;
                }
            });
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            // Nothing to do
        }

        @Override
        public int getCount() {
            return dataList == null ? 0 : dataList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_SCORE: {
                    RemoteViews views = new RemoteViews(getPackageName(),
                            R.layout.widget_score_list_item);

                    WidgetHelper.insertTeamFixtureScoreData(
                            LeagueNewsWidgetRemoteViewsService.this,
                            views,
                            (FixtureScore) dataList.get(position));

                    return views;
                }

                case VIEW_TYPE_COMING: {
                    RemoteViews views = new RemoteViews(getPackageName(),
                            R.layout.widget_coming_list_item);

                    WidgetHelper.insertTeamFixtureComingData(
                            LeagueNewsWidgetRemoteViewsService.this,
                            views,
                            (FixtureComing) dataList.get(position));

                    return views;
                }

                default:
                    throw new UnsupportedOperationException(
                            "Displaying this type of data is not supported!");
            }
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public int getItemViewType(int position) {
            int itemType = dataList.get(position).getType();
            switch (itemType) {
                case BaseFootballData.TYPE_LEAGUE_FIXTURES_SCORES:
                    return VIEW_TYPE_SCORE;
                case BaseFootballData.TYPE_LEAGUE_FIXTURES_COMING:
                    return VIEW_TYPE_COMING;
                default:
                    throw new UnsupportedOperationException(
                            "Displaying this type of data is not supported! " + itemType);
            }
        }
    }
}
