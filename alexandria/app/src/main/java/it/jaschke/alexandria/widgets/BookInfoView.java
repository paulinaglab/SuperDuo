package it.jaschke.alexandria.widgets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.jaschke.alexandria.Book;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;

/**
 * Created by Paulina on 2015-10-27.
 */
public class BookInfoView extends FrameLayout {

    private TextView titleView;
    private TextView subtitleView;
    private TextView authorsView;
    private TextView categoryView;
    private ImageView coverView;

    public BookInfoView(Context context) {
        super(context);
        inflateLayout();
        initFields();
    }

    public BookInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateLayout();
        initFields();
    }

    public BookInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateLayout();
        initFields();
    }

    private void inflateLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.book_info, this);
    }

    private void initFields() {
        titleView = (TextView) findViewById(R.id.book_info_title);
        subtitleView = (TextView) findViewById(R.id.book_info_subtitle);
        authorsView = (TextView) findViewById(R.id.book_info_authors);
        categoryView = (TextView) findViewById(R.id.book_info_categories);
        coverView = (ImageView) findViewById(R.id.book_info_cover);
    }

    public void fillWithData(Cursor cursor) {
        fillTextView(titleView, cursor, AlexandriaContract.BookEntry.COLUMN_TITLE);
        fillTextView(subtitleView, cursor, AlexandriaContract.BookEntry.COLUMN_SUBTITLE);
        fillTextView(authorsView, cursor, AlexandriaContract.BookEntry.COLUMN_AUTHORS);
        authorsView.setText(authorsView.getText().toString().replace(",", ", "));
        fillTextView(categoryView, cursor, AlexandriaContract.BookEntry.COLUMN_CATEGORIES);

        loadCover(cursor);
    }

    public void fillWithData(Book book) {
        if (book != null) {
            titleView.setText(book.title);
            subtitleView.setText(book.subtitle);
            authorsView.setText(TextUtils.join(", ", book.authors));
            categoryView.setText(TextUtils.join(", ", book.categories));

            loadCover(book.imageUrl);

            // Update visibility - hide empty views
            updateFieldVisibility(subtitleView);
            updateFieldVisibility(authorsView);
            updateFieldVisibility(categoryView);
        }
    }

    private void loadCover(Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(AlexandriaContract.BookEntry.COLUMN_IMAGE_URL);
        String imgUrl;
        if (columnIndex != -1)
            imgUrl = cursor.getString(columnIndex);
        else
            imgUrl = null;
        loadCover(imgUrl);
    }

    private void loadCover(String imgUrl) {
        Glide.with(getContext())
                .load(imgUrl)
                .fitCenter()
                .error(R.drawable.no_book_cover)
                .into(coverView);
    }

    private void fillTextView(TextView textView, Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            String text = cursor.getString(columnIndex);
            textView.setText(text);
        } else
            textView.setText("");
        updateFieldVisibility(textView);
    }

    private void updateFieldVisibility(TextView textView) {
        if (textView.getText().toString().isEmpty())
            textView.setVisibility(View.GONE);
        else
            textView.setVisibility(View.VISIBLE);
    }
}
