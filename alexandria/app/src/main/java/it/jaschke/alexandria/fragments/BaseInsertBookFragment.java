package it.jaschke.alexandria.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import it.jaschke.alexandria.Alexandria;
import it.jaschke.alexandria.Book;
import it.jaschke.alexandria.Keys;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.activities.Snackbarable;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.requests.BookRequest;
import it.jaschke.alexandria.services.BookService;

/**
 * Created by Paulina on 2015-10-30.
 */
public abstract class BaseInsertBookFragment extends Fragment
        implements Response.ErrorListener, Response.Listener<Book> {

    private static final String RECEIVED_BOOK_KEY = "received_book_key";

    protected BookRequest bookRequest;

    private Book book;

    private Snackbarable snackbarable;
    private Snackbar snackbar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            snackbarable = (Snackbarable) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement Snackbarable");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            book = (Book) savedInstanceState.getSerializable(RECEIVED_BOOK_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(RECEIVED_BOOK_KEY, book);
        super.onSaveInstanceState(outState);
    }

    protected void showBookByEan(String eanStr) {
        long ean = Long.parseLong(eanStr);
        boolean bookInCatalog = isBookInCatalog(ean);
        if (book == null || book.ean != ean) {
            if (bookInCatalog) {
                // Book is already in catalog.
                loadBookFromDatabase(ean);
                displayReceivedBookData();
            } else {
                // Give user possibility to add this book to catalog.
                sendBookRequest(eanStr);
            }
        } else {
            // Display book from savedInstanceState bundle.
            displayReceivedBookData();
        }
        setAddButtonAvailable(!bookInCatalog);
    }

    public boolean isBookInCatalog(long ean) {
        Cursor cursor = getContext().getContentResolver().query(
                AlexandriaContract.BookEntry.buildBookUri(ean),
                null, null, null, null);
        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    private void sendBookRequest(String eanStr) {
        bookRequest = new BookRequest(eanStr, this, this);
        Alexandria.getInstance().getRequestQueue().add(bookRequest);
    }

    public void insertBookToDatabase(Book book) {
        Intent intent = new Intent(getContext(), BookService.class);
        intent.putExtra(Keys.BOOK_POJO_EXTRA, book);
        intent.setAction(BookService.INSERT_BOOK);
        getContext().startService(intent);
        showBookAddedConfirmation();
    }

    private void loadBookFromDatabase(long ean) {
        Cursor cursor = getContext().getContentResolver().query(
                AlexandriaContract.BookEntry.buildFullBookUri(ean),
                null, null, null, null);
        cursor.moveToFirst();
        book = new Book(cursor);
        cursor.close();
    }

    private void showBookAddedConfirmation() {
        Toast.makeText(getContext(), R.string.insert_book_confirmation, Toast.LENGTH_SHORT).show();
    }

    private void showNetworkErrorSnackbar() {
        snackbar = Snackbar.make(
                snackbarable.getSnackbarParent(),
                R.string.insert_book_network_error,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.insert_book_network_error_action,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBookByEan(getEan());
                    }
                });
        snackbar.show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d(getClass().getSimpleName(), "Book request failed");
        if (error instanceof NoConnectionError || error instanceof TimeoutError) {
            displayNetworkError();
        } else {
            displayBookNotFoundInAPI();
        }
    }

    @Override
    public void onResponse(Book response) {
        book = response;
        displayReceivedBookData();
    }

    public Book getBook() {
        return book;
    }

    protected void setBook(Book book){
        this.book = book;
    }

    protected void resetBook() {
        book = null;
    }

    protected Snackbar getSnackbar() {
        return snackbar;
    }

    protected void displayNetworkError() {
        showNetworkErrorSnackbar();
    }

    protected void displayReceivedBookData() {
        setResultBookViewVisible(true);
    }


    protected abstract void setAddButtonAvailable(boolean available);

    protected abstract void setResultBookViewVisible(boolean visible);

    protected abstract void displayBookNotFoundInAPI();

    protected abstract String getEan();
}
