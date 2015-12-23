package barqsoft.footballscores.activities;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.adapters.CustomizeAdapter;
import barqsoft.footballscores.data.CustomizationLoader;
import barqsoft.footballscores.model.BaseFootballData;

/**
 * Created by Paulina on 2015-12-02.
 */
public class CustomizeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BaseFootballData>> {

    private List<BaseFootballData> teamsAndLeagues;
    private CustomizeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // Recycler initialization
        teamsAndLeagues = new ArrayList<>();
        adapter = new CustomizeAdapter(this, teamsAndLeagues);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.customize_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(0, null, this);
    }


    @Override
    public Loader<List<BaseFootballData>> onCreateLoader(int id, Bundle args) {
        return new CustomizationLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<BaseFootballData>> loader, List<BaseFootballData> data) {
        teamsAndLeagues.clear();
        teamsAndLeagues.addAll(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<BaseFootballData>> loader) {

    }
}
