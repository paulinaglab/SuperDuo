package it.jaschke.alexandria.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import it.jaschke.alexandria.Book;
import it.jaschke.alexandria.Keys;
import it.jaschke.alexandria.data.AlexandriaContract;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class BookService extends IntentService {

    public static final String INSERT_BOOK = "it.jaschke.alexandria.services.action.INSERT_BOOK";
    public static final String DELETE_BOOK = "it.jaschke.alexandria.services.action.DELETE_BOOK";

    public static final String EAN = "it.jaschke.alexandria.services.extra.EAN";

    public BookService() {
        super("Alexandria");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (INSERT_BOOK.equals(action)) {
                Book book = (Book) intent.getSerializableExtra(Keys.BOOK_POJO_EXTRA);
                insertBook(book);
            } else if (DELETE_BOOK.equals(action)) {
                long ean = intent.getLongExtra(EAN, 0);
                deleteBook(ean);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void deleteBook(long ean) {
        if (ean != 0) {
            getContentResolver().delete(
                    AlexandriaContract.BookEntry.buildFullBookUri(ean),
                    AlexandriaContract.BookEntry._ID + "=?",
                    new String[]{Long.toString(ean)});
        }
    }

    /**
     * Handle action insertBook in the provided background thread with the provided
     * parameters.
     */
    private void insertBook(Book book) {
        writeBackBook(book);
        writeBackAuthors(book);
        writeBackCategories(book);
    }

    private void writeBackBook(Book book) {
        ContentValues values = new ContentValues();
        values.put(AlexandriaContract.BookEntry._ID, book.ean);
        values.put(AlexandriaContract.BookEntry.COLUMN_TITLE, book.title);
        values.put(AlexandriaContract.BookEntry.COLUMN_IMAGE_URL, book.imageUrl);
        values.put(AlexandriaContract.BookEntry.COLUMN_SUBTITLE, book.subtitle);
        values.put(AlexandriaContract.BookEntry.COLUMN_DESCRIPTION, book.description);
        values.put(AlexandriaContract.BookEntry.COLUMN_COLOR, book.color);
        getContentResolver().insert(AlexandriaContract.BookEntry.FULL_CONTENT_URI, values);
    }

    private void writeBackAuthors(Book book) {
        for (String author : book.authors) {
            ContentValues values = new ContentValues();
            values.put(AlexandriaContract.AuthorEntry._ID, book.ean);
            values.put(AlexandriaContract.AuthorEntry.COLUMN_AUTHOR, author);
            getContentResolver().insert(AlexandriaContract.AuthorEntry.CONTENT_URI, values);
        }
    }

    private void writeBackCategories(Book book) {
        for (String category : book.categories) {
            ContentValues values = new ContentValues();
            values.put(AlexandriaContract.CategoryEntry._ID, book.ean);
            values.put(AlexandriaContract.CategoryEntry.COLUMN_CATEGORY, category);
            getContentResolver().insert(AlexandriaContract.CategoryEntry.CONTENT_URI, values);
        }
    }
}