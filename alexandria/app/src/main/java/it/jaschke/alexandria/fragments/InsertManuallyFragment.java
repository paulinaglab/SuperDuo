package it.jaschke.alexandria.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.utils.IsbnUtils;
import it.jaschke.alexandria.widgets.BookInfoView;
import it.jaschke.alexandria.widgets.OptionalColorsLayout;

/**
 * Created by Paulina on 2015-10-30.
 */
public class InsertManuallyFragment extends BaseInsertBookFragment
        implements View.OnClickListener, TextWatcher, OptionalColorsLayout.OnColorSelectedListener {

    private static final String EAN_EDIT_TEXT_CONTENT_KEY = "ean_edit_text_content_key";

    private String eanStr;

    private EditText eanEditText;
    private CardView resultCardView;
    private BookInfoView bookInfoView;
    private Button addButton;
    private View pickColorButton;
    private View alreadyAddedView;
    private View bookNotFoundInfoView;
    private ProgressBar progressBar;
    private OptionalColorsLayout optionalColorsLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_insert_manually, container, false);

        eanEditText = (EditText) fragmentView.findViewById(R.id.ean_edit_text);
        resultCardView = (CardView) fragmentView.findViewById(R.id.matching_book_data_cardview);
        bookInfoView = (BookInfoView) fragmentView.findViewById(R.id.matching_book_info_view);
        addButton = (Button) fragmentView.findViewById(R.id.matching_book_save_button);
        pickColorButton = fragmentView.findViewById(R.id.matching_book_pick_color_button);
        alreadyAddedView = fragmentView.findViewById(R.id.matching_book_already_added);
        bookNotFoundInfoView = fragmentView.findViewById(R.id.insert_manually_book_not_found);
        progressBar = (ProgressBar) fragmentView.findViewById(R.id.insert_manually_progress_bar);
        optionalColorsLayout = (OptionalColorsLayout)
                fragmentView.findViewById(R.id.matching_book_optional_color_layout);

        if (savedInstanceState != null) {
            eanEditText.setText(savedInstanceState.getString(EAN_EDIT_TEXT_CONTENT_KEY));
        }

        eanEditText.addTextChangedListener(this);
        pickColorButton.setOnClickListener(this);
        optionalColorsLayout.setOnColorSelectedListener(this);

        fragmentView.findViewById(R.id.insert_manually_back_button).setOnClickListener(this);
        addButton.setOnClickListener(this);

        fragmentView.findViewById(R.id.ean_edit_text).requestFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EAN_EDIT_TEXT_CONTENT_KEY, eanEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.insert_manually_back_button:
                getActivity().onBackPressed();
                break;
            case R.id.matching_book_save_button:
                insertBookToDatabase(getBook());
                setAddButtonAvailable(false);
                ((View) optionalColorsLayout.getParent()).setVisibility(View.GONE);
                break;
            case R.id.matching_book_pick_color_button:
                pickColorButton.setVisibility(View.GONE);
                ((View) optionalColorsLayout.getParent()).setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //no need
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //no need
    }

    @Override
    public void afterTextChanged(Editable s) {
        eanStr = s.toString();

        // Catch ISBN-10 numbers
        if (eanStr.length() == 10 && !eanStr.startsWith("97")) {
            eanStr = IsbnUtils.convertToISBN13(eanStr);
        }

        if (eanStr.length() < 13) {
            setResultBookViewVisible(false);
            progressBar.setVisibility(View.GONE);
            bookNotFoundInfoView.setVisibility(View.GONE);
            resetBook();
            return;
        }

        // Once we have an ISBN...
        progressBar.setVisibility(View.VISIBLE);
        showBookByEan(eanStr);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(eanEditText.getWindowToken(), 0);
    }

    @Override
    protected void setResultBookViewVisible(boolean visible) {
        if (visible)
            resultCardView.setVisibility(View.VISIBLE);
        else
            resultCardView.setVisibility(View.GONE);
    }

    @Override
    protected void setAddButtonAvailable(boolean available) {
        if (available) {
            addButton.setVisibility(View.VISIBLE);
            pickColorButton.setVisibility(View.VISIBLE);
            alreadyAddedView.setVisibility(View.GONE);
        } else {
            addButton.setVisibility(View.GONE);
            pickColorButton.setVisibility(View.GONE);
            alreadyAddedView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void displayReceivedBookData() {
        super.displayReceivedBookData();
        progressBar.setVisibility(View.GONE);
        bookInfoView.fillWithData(getBook());
        if (getBook().color == 0) {
            optionalColorsLayout.selectDefault();
        } else {
            resultCardView.setCardBackgroundColor(getBook().color);
        }

        // Hiding soft keyboard
        hideKeyboard();
    }

    @Override
    protected void displayBookNotFoundInAPI() {
        progressBar.setVisibility(View.GONE);
        setResultBookViewVisible(false);
        bookNotFoundInfoView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void displayNetworkError() {
        progressBar.setVisibility(View.GONE);
        super.displayNetworkError();
    }

    @Override
    protected String getEan() {
        return eanStr;
    }

    @Override
    public void onColorSelected(@ColorInt int color) {
        getBook().color = color;
        resultCardView.setCardBackgroundColor(color);
    }
}
