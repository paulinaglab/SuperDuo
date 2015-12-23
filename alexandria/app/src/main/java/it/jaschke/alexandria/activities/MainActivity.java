package it.jaschke.alexandria.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.adapters.BookListAdapter;
import it.jaschke.alexandria.animation.FabSubmenuAnimation;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.widgets.CardCompatMarginItemDecoration;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener, View.OnClickListener, BookListAdapter.OnItemClickListener {

    private static final String FAB_MENU_OPEN_KEY = "fab_menu_open_key";
    private static final String SEARCH_TEXT_KEY = "search_text";

    private BookListAdapter bookListAdapter;
    private RecyclerView booksRecyclerView;
    private String searchingText;
    private View fabsDimView;
    private FloatingActionButton fabAddNewBook;
    private FloatingActionButton fabInsert;
    private FloatingActionButton fabReadCode;
    private boolean subFabsVisible = false;
    private Animator ongoingAnimation;
    private View emptyCatalogView;
    private View emptySearchResultView;

    private final int LOADER_ID = 10;

    private BroadcastReceiver messageReceiver;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);

        bookListAdapter = new BookListAdapter(this);
        bookListAdapter.setOnItemClickListener(this);

        booksRecyclerView = (RecyclerView) findViewById(R.id.list_of_books_recycler);
        booksRecyclerView.setAdapter(bookListAdapter);
        booksRecyclerView.setLayoutManager(
                new GridLayoutManager(this, getResources().getInteger(R.integer.catalog_column_count)));
        booksRecyclerView.addItemDecoration(
                new CardCompatMarginItemDecoration(
                        getResources().getDimensionPixelSize(
                                R.dimen.book_card_recycler_margin_decoration)));

        fabsDimView = findViewById(R.id.main_fabs_dim_view);
        fabsDimView.setOnClickListener(this);

        // Init FABs' listener
        fabAddNewBook = (FloatingActionButton) findViewById(R.id.fab_add_book);
        fabAddNewBook.setOnClickListener(this);

        fabInsert = (FloatingActionButton) findViewById(R.id.fab_insert_manually);
        fabInsert.setOnClickListener(this);
        fabReadCode = (FloatingActionButton) findViewById(R.id.fab_scan_code);
        fabReadCode.setOnClickListener(this);

        emptyCatalogView = findViewById(R.id.main_no_books_added_to_catalog);
        emptySearchResultView = findViewById(R.id.main_no_books_found);

        if (savedInstanceState != null) {
            setFabMenuOpen(savedInstanceState.getBoolean(FAB_MENU_OPEN_KEY));
            searchingText = savedInstanceState.getString(SEARCH_TEXT_KEY);
        }
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FAB_MENU_OPEN_KEY, subFabsVisible);
        outState.putString(SEARCH_TEXT_KEY, searchingText);
        super.onSaveInstanceState(outState);
    }

    private void updateList(String searchingText) {
        this.searchingText = searchingText;
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        if (!TextUtils.isEmpty(searchingText)) {
            searchView.setQuery(searchingText, true);
            searchView.setIconified(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    @Override
    public void onItemClicked(Uri uri) {
        // Opening new activity with uri of selected book.
        Intent intent = new Intent(this, BookDetailActivity.class)
                .setData(uri);
        startActivity(intent);
    }

    private void expandFabsSubmenu() {
        final Animator animator = FabSubmenuAnimation.getExpandingAnimator(
                fabAddNewBook, fabReadCode, fabInsert, fabsDimView);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (ongoingAnimation != null)
                    ongoingAnimation.cancel();
                ongoingAnimation = animator;
                setFabMenuOpen(true);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ongoingAnimation = null;
            }
        });

        animator.start();
    }

    private void collapseFabsSubmenu() {
        final Animator animator = FabSubmenuAnimation.getCollapsingAnimator(
                fabAddNewBook, fabReadCode, fabInsert, fabsDimView);
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                if (ongoingAnimation != null)
                    ongoingAnimation.cancel();
                ongoingAnimation = animator;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setFabMenuOpen(false);
                ongoingAnimation = null;
            }
        });

        animator.start();
    }

    private void setFabMenuOpen(boolean open) {
        if (open) {
            fabInsert.setVisibility(View.VISIBLE);
            fabReadCode.setVisibility(View.VISIBLE);
            fabsDimView.setVisibility(View.VISIBLE);
            subFabsVisible = true;
        } else {
            fabInsert.setVisibility(View.INVISIBLE);
            fabReadCode.setVisibility(View.INVISIBLE);
            fabsDimView.setVisibility(View.GONE);
            subFabsVisible = false;
        }
    }


    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(MESSAGE_KEY) != null) {
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() < 2) {
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        updateList(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        updateList(newText);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_book:
                if (subFabsVisible)
                    collapseFabsSubmenu();
                else
                    expandFabsSubmenu();
                break;
            case R.id.main_fabs_dim_view:
                collapseFabsSubmenu();
                break;
            case R.id.fab_insert_manually:
                startActivity(new Intent(this, InsertManuallyActivity.class));
                collapseFabsSubmenu();
                break;
            case R.id.fab_scan_code:
                startActivity(new Intent(this, ScannerActivity.class));
                collapseFabsSubmenu();
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String selection =
                AlexandriaContract.BookEntry.COLUMN_TITLE + " LIKE ? OR " +
                        AlexandriaContract.BookEntry.COLUMN_SUBTITLE + " LIKE ? ";

        if (!TextUtils.isEmpty(searchingText)) {
            String query = "%" + searchingText + "%";
            return new CursorLoader(
                    this,
                    AlexandriaContract.BookEntry.FULL_CONTENT_URI,
                    null,
                    selection,
                    new String[]{query, query},
                    AlexandriaContract.BookEntry.COLUMN_TITLE);
        } else
            return new CursorLoader(
                    this,
                    AlexandriaContract.BookEntry.FULL_CONTENT_URI,
                    null, null, null, AlexandriaContract.BookEntry.COLUMN_TITLE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bookListAdapter.swapCursor(data);
        if (data.getCount() <= 0) {
            if (TextUtils.isEmpty(searchingText)) {
                emptySearchResultView.setVisibility(View.GONE);
                emptyCatalogView.setVisibility(View.VISIBLE);
            } else {
                emptySearchResultView.setVisibility(View.VISIBLE);
                emptyCatalogView.setVisibility(View.GONE);
            }

            booksRecyclerView.setVisibility(View.GONE);
        } else {
            booksRecyclerView.setVisibility(View.VISIBLE);
            emptySearchResultView.setVisibility(View.GONE);
            emptyCatalogView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookListAdapter.swapCursor(null);
    }


}