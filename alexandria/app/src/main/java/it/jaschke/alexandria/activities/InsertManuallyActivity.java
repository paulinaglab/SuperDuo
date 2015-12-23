package it.jaschke.alexandria.activities;


import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import it.jaschke.alexandria.Book;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.fragments.ConfirmLeaveDialog;
import it.jaschke.alexandria.fragments.InsertManuallyFragment;

/**
 * Created by Paulina on 2015-10-23.
 */
public class InsertManuallyActivity extends AppCompatActivity
        implements Snackbarable, ConfirmLeaveDialog.Listener {

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_manually);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.fragment_container);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public CoordinatorLayout getSnackbarParent() {
        return coordinatorLayout;
    }

    private boolean shouldPreventExit() {
        InsertManuallyFragment fragment = (InsertManuallyFragment) getSupportFragmentManager()
                .findFragmentById(R.id.insert_manually_fragment);
        if (fragment == null)
            return false;

        Book book = fragment.getBook();
        if (book == null || fragment.isBookInCatalog(book.ean))
            return false;
        else
            return true;
    }

    @Override
    public void onBackPressed() {
        if (shouldPreventExit())
            showLeaveConfirmationDialog();
        else
            super.onBackPressed();
    }

    private void showLeaveConfirmationDialog() {
        DialogFragment fragment = new ConfirmLeaveDialog();
        fragment.show(getSupportFragmentManager(), ConfirmLeaveDialog.class.getSimpleName());
    }

    @Override
    public void leaveButtonClicked() {
        finish();
    }

    @Override
    public void saveButtonClicked() {
        InsertManuallyFragment fragment = (InsertManuallyFragment) getSupportFragmentManager()
                .findFragmentById(R.id.insert_manually_fragment);
        if (fragment.getBook() != null) {
            fragment.insertBookToDatabase(fragment.getBook());
        }
        finish();
    }
}
