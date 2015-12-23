package it.jaschke.alexandria.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import it.jaschke.alexandria.Keys;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.fragments.BookDetailFragment;
import it.jaschke.alexandria.fragments.ConfirmBookDeletionDialog;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.utils.ColorUtils;

/**
 * Created by Paulina on 2015-10-23.
 */
public class BookDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ConfirmBookDeletionDialog.Listener {

    private static final int LOADER_ID = 1;
    private long bookEan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);

        View backgroundView = findViewById(R.id.translucent_activity_background);
        backgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bookEan = ContentUris.parseId(getIntent().getData());

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(Keys.SELECTED_BOOK_URI, getIntent().getData());

            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.book_detail_container, fragment)
                    .commit();
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                getIntent().getData(),
                new String[]{AlexandriaContract.BookEntry.COLUMN_COLOR},
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()){
            int colorColumnIndex = data.getColumnIndex(AlexandriaContract.BookEntry.COLUMN_COLOR);
            if (!data.isNull(colorColumnIndex)) {
                // ActionBar
                int actionBarColor =
                        ColorUtils.getColorWithTranslateBrightness(data.getInt(colorColumnIndex), -30);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(actionBarColor));

                // Status bar
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (getResources().getBoolean(R.bool.details_translucent_view)) {
                        getWindow().setStatusBarColor(Color.TRANSPARENT);
                    } else {
                        getWindow().setStatusBarColor(
                                ColorUtils.getColorWithTranslateBrightness(actionBarColor, -20));
                    }
                }
            }
        } else {
            finish();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void deleteClicked() {
        Intent bookIntent = new Intent(this, BookService.class);
        bookIntent.putExtra(BookService.EAN, bookEan);
        bookIntent.setAction(BookService.DELETE_BOOK);
        startService(bookIntent);
        finish();
    }

    @Override
    public void cancelClicked() {

    }
}
