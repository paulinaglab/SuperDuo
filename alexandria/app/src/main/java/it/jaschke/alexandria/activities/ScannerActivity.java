package it.jaschke.alexandria.activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.fragments.ConfirmLeaveDialog;
import it.jaschke.alexandria.fragments.ScannerFragment;

/**
 * Created by Paulina on 2015-10-23.
 */
public class ScannerActivity extends AppCompatActivity
        implements Snackbarable, ConfirmLeaveDialog.Listener {

    private CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.scanner_coordinator);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        if (savedInstanceState == null) {
            ScannerFragment fragment = new ScannerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.scanner_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        ScannerFragment fragment = (ScannerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.scanner_fragment_container);

        if (fragment == null || fragment.getBook() == null
                || fragment.isBookInCatalog(fragment.getBook().ean))
            super.onBackPressed();
        else
            new ConfirmLeaveDialog()
                    .show(getSupportFragmentManager(), ConfirmLeaveDialog.class.getSimpleName());
    }

    @Override
    public CoordinatorLayout getSnackbarParent() {
        return coordinatorLayout;
    }


    @Override
    public void leaveButtonClicked() {
        finish();
    }

    @Override
    public void saveButtonClicked() {
        ScannerFragment fragment = (ScannerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.scanner_fragment_container);
        if (fragment.getBook() != null) {
            fragment.insertBookToDatabase(fragment.getBook());
        }
        finish();
    }
}