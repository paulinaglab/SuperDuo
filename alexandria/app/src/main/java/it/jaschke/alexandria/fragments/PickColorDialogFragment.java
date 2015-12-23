package it.jaschke.alexandria.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.jaschke.alexandria.Keys;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.widgets.OptionalColorsLayout;

/**
 * Created by Paulina on 2015-10-31.
 */
public class PickColorDialogFragment extends DialogFragment
        implements OptionalColorsLayout.OnColorSelectedListener {

    public static final String TAG = PickColorDialogFragment.class.getName();

    private Uri bookUri;

    private OptionalColorsLayout colorsLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, getTheme());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.dialog_color_picker, container, false);

        colorsLayout = (OptionalColorsLayout) fragmentView.findViewById(R.id.dialog_colors_layout);

        if (getArguments() != null) {
            bookUri = getArguments().getParcelable(Keys.SELECTED_BOOK_URI);
        }
        if (bookUri != null) {
            loadColorFromDatabase();
        }
        colorsLayout.setOnColorSelectedListener(this);

        return fragmentView;
    }

    @Override
    public void onColorSelected(@ColorInt int color) {
        updateColorInDatabase(color);
        dismiss();
    }

    private void loadColorFromDatabase() {
        Cursor cursor = getContext().getContentResolver().query(
                bookUri,
                new String[]{AlexandriaContract.BookEntry.COLUMN_COLOR},
                null, null, null);
        if (cursor != null && cursor.moveToFirst() &&
                !cursor.isNull(cursor.getColumnIndex(AlexandriaContract.BookEntry.COLUMN_COLOR))) {

            int color = cursor.getInt(
                    cursor.getColumnIndex(AlexandriaContract.BookEntry.COLUMN_COLOR));
            colorsLayout.setSelectedColor(color);
        }
        cursor.close();
    }

    private void updateColorInDatabase(@ColorInt int color) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AlexandriaContract.BookEntry.COLUMN_COLOR, color);
        getContext().getContentResolver().update(bookUri, contentValues, null, null);
    }
}
