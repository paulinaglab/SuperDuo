package barqsoft.footballscores.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import barqsoft.footballscores.R;
import barqsoft.footballscores.fragments.MainScreenFragment;
import barqsoft.footballscores.syncadapters.FootballScoresSyncAdapter;

public class MainActivity extends AppCompatActivity implements MainScreenFragment.OnScrollListener {

    private View appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int syncState = FootballScoresSyncAdapter.getSyncState(this);
        if (syncState == FootballScoresSyncAdapter.SYNC_NEVER_SYNCED
                || syncState == FootballScoresSyncAdapter.SYNC_FIRST_SYNC_ERROR) {
            Intent intent = new Intent(this, LaunchActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        appBarLayout = findViewById(R.id.main_appbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_customize: {
                Intent intent = new Intent(this, CustomizeActivity.class);
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        appBarLayout.setTranslationY(appBarLayout.getTranslationY() - dy);
    }
}
