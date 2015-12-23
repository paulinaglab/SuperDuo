package it.jaschke.alexandria;

import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.jaschke.alexandria.data.AlexandriaContract;

/**
 * Created by Paulina on 2015-10-29.
 */
public class Book implements Serializable {

    public long ean;
    public String title;
    public String subtitle;
    public String description;
    public String imageUrl;
    public List<String> authors;
    public List<String> categories;
    public int color;

    public Book() {
        authors = new ArrayList<>();
        categories = new ArrayList<>();
    }

    public Book(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            ean = cursor.getLong(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID));
            title = optString(cursor, AlexandriaContract.BookEntry.COLUMN_TITLE);
            subtitle = optString(cursor, AlexandriaContract.BookEntry.COLUMN_SUBTITLE);
            description = optString(cursor, AlexandriaContract.BookEntry.COLUMN_DESCRIPTION);
            imageUrl = optString(cursor, AlexandriaContract.BookEntry.COLUMN_IMAGE_URL);
            String authorsStr = optString(cursor, AlexandriaContract.BookEntry.COLUMN_AUTHORS);
            authors = Arrays.asList(authorsStr.split(","));
            String categoriesStr = optString(cursor, AlexandriaContract.BookEntry.COLUMN_CATEGORIES);
            categories = Arrays.asList(categoriesStr.split(","));
            int colorColumn = cursor.getColumnIndex(AlexandriaContract.BookEntry.COLUMN_COLOR);
            if (!cursor.isNull(colorColumn))
                color = cursor.getInt(colorColumn);
        }
    }

    private String optString(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1)
            return cursor.getString(columnIndex) != null ? cursor.getString(columnIndex) : "";
        else
            return "";
    }

}
