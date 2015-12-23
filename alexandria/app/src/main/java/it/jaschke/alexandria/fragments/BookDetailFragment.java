package it.jaschke.alexandria.fragments;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.jaschke.alexandria.Keys;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.widgets.BookInfoView;

public class BookDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 10;

    private Cursor cursor;

    private Uri bookUri;
    private long bookEan;

    private BookInfoView bookInfoView;
    private TextView descriptionView;
    private TextView descriptionLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            bookUri = arguments.getParcelable(Keys.SELECTED_BOOK_URI);
            bookEan = ContentUris.parseId(bookUri);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        View rootView = inflater.inflate(R.layout.fragment_book_detail, container, false);

        bookInfoView = (BookInfoView) rootView.findViewById(R.id.details_book_info_view);
        descriptionView = (TextView) rootView.findViewById(R.id.details_description_text_view);
        descriptionLabel = (TextView) rootView.findViewById(R.id.details_description_label);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent intent = createShareMovieIntent();
                if (intent != null)
                    startActivity(intent);
                return true;
            case R.id.action_delete:
                new ConfirmBookDeletionDialog().show(getFragmentManager(),
                        ConfirmBookDeletionDialog.class.getSimpleName());
                return true;
            case R.id.action_pick_color:
                PickColorDialogFragment fragment = new PickColorDialogFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelable(Keys.SELECTED_BOOK_URI, bookUri);
                fragment.setArguments(arguments);
                fragment.show(getActivity().getSupportFragmentManager(), PickColorDialogFragment.TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareMovieIntent() {
        if (cursor != null && cursor.moveToFirst()) {
            String bookTitle = cursor.getString(
                    cursor.getColumnIndex(AlexandriaContract.BookEntry.COLUMN_TITLE));

            Intent shareIntent = ShareCompat.IntentBuilder
                    .from(getActivity())
                    .setText(getString(R.string.intent_share_text) + bookTitle + "\n" +
                            getString(R.string.intent_share_hashtag))
                    .setType("text/plain")
                    .createChooserIntent();
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            return shareIntent;
        } else
            return null;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(bookEan),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        cursor = data;
        if (data != null && data.moveToFirst()) {
            bookInfoView.fillWithData(data);
            int colorColumnIndex = data.getColumnIndex(AlexandriaContract.BookEntry.COLUMN_COLOR);
            if (!data.isNull(colorColumnIndex))
                bookInfoView.setBackgroundColor(data.getInt(colorColumnIndex));
            descriptionView.setText(data.getString(
                    data.getColumnIndex(AlexandriaContract.BookEntry.COLUMN_DESCRIPTION)));
            if (descriptionView.getText().toString().isEmpty()) {
                descriptionView.setVisibility(View.GONE);
                descriptionLabel.setVisibility(View.GONE);
            } else {
                descriptionView.setVisibility(View.VISIBLE);
                descriptionLabel.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        cursor = null;
    }

}