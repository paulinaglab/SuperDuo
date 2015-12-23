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

import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.adapters.TeamWidgetCustomizationAdapter;
import barqsoft.footballscores.data.TeamsLoader;
import barqsoft.footballscores.model.BaseFootballData;
import barqsoft.footballscores.model.Team;
import barqsoft.footballscores.widgets.TeamNewsWidgetIntentService;

/**
 * Created by Paulina on 2015-12-11.
 */
public class TeamWidgetConfigActivity extends AppCompatActivity implements
        TeamWidgetCustomizationAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<BaseFootballData>> {

    public static final String TEAM_PREFIX = "team_";
    public static final String WIDGET_PREFIX = "widget_";

    private TeamWidgetCustomizationAdapter adapter;
    List<BaseFootballData> data;

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

        setContentView(R.layout.activity_team_widget_config);

        data = new ArrayList<>();
        adapter = new TeamWidgetCustomizationAdapter(this, data, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.team_widget_customization_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportLoaderManager().initLoader(0, null, this);
    }


    private void saveSelectedTeam(int teamId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(WIDGET_PREFIX + widgetId, TEAM_PREFIX + teamId).commit();
    }

    private void saveResultAndFinish() {
        //Widget config activity is obligated to return widgetId given as intent extra.
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    @Override
    public void teamClicked(Team team) {
        saveSelectedTeam(team.id);

        startService(new Intent(this, TeamNewsWidgetIntentService.class));  //updates widget views

        saveResultAndFinish();
    }

    @Override
    public Loader<List<BaseFootballData>> onCreateLoader(int id, Bundle args) {
        return new TeamsLoader(this);
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
}
