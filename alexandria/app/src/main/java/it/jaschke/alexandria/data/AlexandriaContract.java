package it.jaschke.alexandria.data;

/**
 * Created by saj on 22/12/14.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class AlexandriaContract {

    public static final String CONTENT_AUTHORITY = "it.jaschke.alexandria";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "books";
    public static final String PATH_AUTHORS = "authors";
    public static final String PATH_CATEGORIES = "categories";
    public static final String PATH_FULLBOOK = "fullbook";


    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKS).build();

        public static final Uri FULL_CONTENT_URI =
                CONTENT_URI.buildUpon().appendPath(PATH_FULLBOOK).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        // Table name
        public static final String TABLE_NAME = "books";

        // Columns declaration
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE_URL = "imgurl";
        public static final String COLUMN_SUBTITLE = "subtitle";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_AUTHORS = "authors";
        public static final String COLUMN_CATEGORIES = "categories";


        public static Uri buildBookUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFullBookUri(long id) {
            return ContentUris.withAppendedId(FULL_CONTENT_URI, id);
        }
    }


    public static final class AuthorEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AUTHORS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AUTHORS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AUTHORS;


        // Table name
        public static final String TABLE_NAME = "authors";

        // Columns declaration
        public static final String COLUMN_AUTHOR = "author";


        public static Uri buildAuthorUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class CategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORIES;


        // Table name
        public static final String TABLE_NAME = "categories";

        // Columns declaration
        public static final String COLUMN_CATEGORY = "category";


        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}