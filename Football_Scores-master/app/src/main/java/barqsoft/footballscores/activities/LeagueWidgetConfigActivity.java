package barqsoft.footballscores.activities;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.adapters.LeagueWidgetCustomizationAdapter;
import barqsoft.footballscores.data.LeaguesLoader;
import barqsoft.footballscores.model.BaseFootballData;
import barqsoft.footballscores.model.League;
import barqsoft.footballscores.widgets.LeagueNewsWidgetProvider;

/**
 * Created by Paulina on 2015-12-11.
 */
public class LeagueWidgetConfigActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<BaseFootballData>>,
        LeagueWidgetCustomizationAdapter.OnItemClickListener {

    public static final String LEAGUE_PREFIX = "league_";

    private LeagueWidgetCustomizationAdapter adapter;
    private List<BaseFootballData> data;

    private int widgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tip from developer.android.com - if user cancels widget won't be added
        if (savedInstanceState == null) {
            setResult(RESULT_CANCELED);
        }

        widgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        setContentView(R.layout.activity_league_widget_config);


        data = new ArrayList<>();
        adapter = new LeagueWidgetCustomizationAdapter(this, data, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.league_widget_customization_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<BaseFootballData>> onCreateLoader(int id, Bundle args) {
        return new LeaguesLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<BaseFootballData>> loader, List<BaseFootballData> data) {
        this.data.clear();
        this.data.addAll(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<BaseFootballData>> loader) {

    }

    private void saveSelectedLeague(int leagueId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().
                putString(TeamWidgetConfigActivity.WIDGET_PREFIX + widgetId,
                        LEAGUE_PREFIX + leagueId)
                .commit();
    }

    private void saveResultAndFinish(League league) {
        //Widget config activity is obligated to return widgetId given as intent extra.
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);
        finish();

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_league_news);
        views.setEmptyView(R.id.widget_league_news_list, android.R.id.empty);
        LeagueNewsWidgetProvider.setRemoteViewsAdapter(this, views, widgetId);
        LeagueNewsWidgetProvider.setLeague(views, league);
        AppWidgetManager.getInstance(this).updateAppWidget(widgetId, views);
    }

    @Override
    public void leagueClicked(League league) {
        saveSelectedLeague(league.id);
        saveResultAndFinish(league);
    }
}
