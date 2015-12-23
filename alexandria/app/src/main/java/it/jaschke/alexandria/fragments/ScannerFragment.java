package it.jaschke.alexandria.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.Result;

import it.jaschke.alexandria.Book;
import it.jaschke.alexandria.Keys;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.widgets.AlexandriaScannerView;
import it.jaschke.alexandria.widgets.OptionalColorsLayout;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static it.jaschke.alexandria.R.id.matching_book_save_button;

/**
 * Created by Paulina on 2015-10-28.
 */
public class ScannerFragment extends BaseInsertBookFragment
        implements ZXingScannerView.ResultHandler, View.OnClickListener,
        OptionalColorsLayout.OnColorSelectedListener {

    private static final String OPTIONAL_COLOR_PICKER_VISIBLE = "optional_color_picker_visible";

    private Camera camera;

    private String eanStr;

    private AlexandriaScannerView scannerView;
    private ProgressBar progressBar;
    private View bookWrapper;
    private View coloredWrapper;
    private TextView titleView;
    private TextView authorsView;
    private View pickColorButton;
    private Button addBookButton;
    private OptionalColorsLayout optionalColorsLayout;
    private View alreadyAddedView;
    private View bookNotFoundView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_scanner, container, false);

        scannerView = (AlexandriaScannerView) fragmentView.findViewById(R.id.scanner_view);
        scannerView.setSaveEnabled(false);

        progressBar = (ProgressBar) fragmentView.findViewById(R.id.scanner_progress_bar);
        bookWrapper = fragmentView.findViewById(R.id.matching_book_data_cardview);
        coloredWrapper = fragmentView.findViewById(R.id.scanner_book_colored_wrapper);
        titleView = (TextView) fragmentView.findViewById(R.id.matching_book_title);
        authorsView = (TextView) fragmentView.findViewById(R.id.matching_book_authors);
        pickColorButton = fragmentView.findViewById(R.id.matching_book_pick_color_button);
        addBookButton = (Button) fragmentView.findViewById(matching_book_save_button);
        alreadyAddedView = fragmentView.findViewById(R.id.matching_book_already_added);
        optionalColorsLayout = (OptionalColorsLayout)
                fragmentView.findViewById(R.id.matching_book_optional_color_layout);
        bookNotFoundView = fragmentView.findViewById(R.id.scanner_book_not_found);

        scannerView.setOnClickListener(this);
        addBookButton.setOnClickListener(this);
        pickColorButton.setOnClickListener(this);
        optionalColorsLayout.setOnColorSelectedListener(this);

        if (savedInstanceState != null) {
            setBook((Book) savedInstanceState.getSerializable(Keys.BOOK_POJO_EXTRA));
            if (getBook() != null) {
                displayReceivedBookData();
                setOptionalColorsLayoutVisible(savedInstanceState
                        .getBoolean(OPTIONAL_COLOR_PICKER_VISIBLE, false));
                optionalColorsLayout.setSelectedColor(getBook().color);
                eanStr = Long.toString(getBook().ean);
                setAddButtonAvailable(!isBookInCatalog(getBook().ean));
            }
        }

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        scannerView.setResultHandler(this);
        restartCamera();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Keys.BOOK_POJO_EXTRA, getBook());
        outState.putBoolean(OPTIONAL_COLOR_PICKER_VISIBLE,
                optionalColorsLayout.getVisibility() == View.VISIBLE);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        progressBar.setVisibility(View.VISIBLE);
        eanStr = result.getText();
        Log.d(getClass().getName(), "Read code: " + eanStr);
        showBookByEan(eanStr);
        camera = null;
    }

    @Override
    protected void setAddButtonAvailable(boolean available) {
        if (available) {
            addBookButton.setVisibility(View.VISIBLE);
            pickColorButton.setVisibility(View.VISIBLE);
            alreadyAddedView.setVisibility(View.GONE);
        } else {
            addBookButton.setVisibility(View.GONE);
            pickColorButton.setVisibility(View.GONE);
            alreadyAddedView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setResultBookViewVisible(boolean visible) {
        if (visible) {
            getShowingResultsAnimator().start();
        } else
            bookWrapper.setVisibility(View.INVISIBLE);
    }

    private Animator getShowingResultsAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                bookWrapper, View.TRANSLATION_Y, bookWrapper.getHeight(), 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                bookWrapper.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator getShowingBookNotFoundAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                bookNotFoundView, View.TRANSLATION_Y, bookNotFoundView.getHeight(), 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                bookNotFoundView.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    private Animator getHidingResultsAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                bookWrapper, View.TRANSLATION_Y, 0, bookWrapper.getHeight());
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                bookWrapper.setVisibility(View.INVISIBLE);
            }
        });
        return animator;
    }

    private Animator getHidingBookNotFoundAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                bookNotFoundView, View.TRANSLATION_Y, 0, bookNotFoundView.getHeight());
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                bookNotFoundView.setVisibility(View.INVISIBLE);
            }
        });
        return animator;
    }

    @Override
    protected void displayReceivedBookData() {
        super.displayReceivedBookData();
        progressBar.setVisibility(View.GONE);
        titleView.setText(getBook().title);
        authorsView.setText(TextUtils.join(", ", getBook().authors));
        if (getBook().color == 0) {
            optionalColorsLayout.selectDefault();
        } else {
            coloredWrapper.setBackgroundColor(getBook().color);
        }
    }

    @Override
    protected void displayBookNotFoundInAPI() {
        progressBar.setVisibility(View.GONE);

        getShowingBookNotFoundAnimator().start();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.matching_book_save_button:
                // Inserting book to database.
                insertBookToDatabase(getBook());
                // Back to scanning.
                resumeScanning();
                break;

            case R.id.matching_book_pick_color_button:
                setOptionalColorsLayoutVisible(true);
                break;

            case R.id.scanner_view:
                resumeScanning();
                break;
        }
    }

    private void resumeScanning() {
        if (bookWrapper.getVisibility() == View.VISIBLE) {
            // Hide results.
            Animator hidingAnimator = getHidingResultsAnimator();
            hidingAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    optionalColorsLayout.setVisibility(View.GONE);
                    scannerView.setResultHandler(ScannerFragment.this);
                    restartCamera();
                }
            });
            hidingAnimator.start();
        } else
            restartCamera();

        if (getSnackbar() != null) {
            getSnackbar().dismiss();
        }
        resetBook();
        eanStr = null;
        getHidingBookNotFoundAnimator().start();
    }

    private void restartCamera() {
        if (camera == null) {
            camera = Camera.open();
            scannerView.startCamera(camera);
        }
    }

    private void setOptionalColorsLayoutVisible(boolean visible) {
        if (visible) {
            optionalColorsLayout.setVisibility(View.VISIBLE);
            pickColorButton.setVisibility(View.GONE);
        } else {
            optionalColorsLayout.setVisibility(View.GONE);
            pickColorButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onColorSelected(@ColorInt int color) {
        getBook().color = color;
        coloredWrapper.setBackgroundColor(color);
        addBookButton.setTextColor(color);
    }
}
