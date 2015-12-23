package it.jaschke.alexandria.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import it.jaschke.alexandria.Alexandria;
import it.jaschke.alexandria.R;

/**
 * Created by saj on 22/12/14.
 */
public class AlexandriaDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "alexandria.db";

    public AlexandriaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + AlexandriaContract.BookEntry.TABLE_NAME + " (" +
                AlexandriaContract.BookEntry._ID + " INTEGER PRIMARY KEY," +
                AlexandriaContract.BookEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                AlexandriaContract.BookEntry.COLUMN_SUBTITLE + " TEXT," +
                AlexandriaContract.BookEntry.COLUMN_DESCRIPTION + " TEXT," +
                AlexandriaContract.BookEntry.COLUMN_COLOR + " INTEGER," +
                AlexandriaContract.BookEntry.COLUMN_IMAGE_URL + " TEXT, " +
                "UNIQUE (" + AlexandriaContract.BookEntry._ID + ") ON CONFLICT IGNORE)";

        final String SQL_CREATE_AUTHOR_TABLE = "CREATE TABLE " + AlexandriaContract.AuthorEntry.TABLE_NAME + " (" +
                AlexandriaContract.AuthorEntry._ID + " INTEGER," +
                AlexandriaContract.AuthorEntry.COLUMN_AUTHOR + " TEXT," +
                " FOREIGN KEY (" + AlexandriaContract.AuthorEntry._ID + ") REFERENCES " +
                AlexandriaContract.BookEntry.TABLE_NAME + " (" + AlexandriaContract.BookEntry._ID + "))";

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + AlexandriaContract.CategoryEntry.TABLE_NAME + " (" +
                AlexandriaContract.CategoryEntry._ID + " INTEGER," +
                AlexandriaContract.CategoryEntry.COLUMN_CATEGORY + " TEXT," +
                " FOREIGN KEY (" + AlexandriaContract.CategoryEntry._ID + ") REFERENCES " +
                AlexandriaContract.BookEntry.TABLE_NAME + " (" + AlexandriaContract.BookEntry._ID + "))";


        Log.d("sql-statments", SQL_CREATE_BOOK_TABLE);
        Log.d("sql-statments", SQL_CREATE_AUTHOR_TABLE);
        Log.d("sql-statments", SQL_CREATE_CATEGORY_TABLE);

        db.execSQL(SQL_CREATE_BOOK_TABLE);
        db.execSQL(SQL_CREATE_AUTHOR_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("ALTER TABLE " + AlexandriaContract.BookEntry.TABLE_NAME + " ADD COLUMN " +
                    AlexandriaContract.BookEntry.COLUMN_COLOR +
                    " DEFAULT " + Alexandria.getInstance().getResources().getColor(R.color.book_color_pebble));
        }
    }

}
