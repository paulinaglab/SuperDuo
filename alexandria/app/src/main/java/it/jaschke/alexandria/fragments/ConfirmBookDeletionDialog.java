package it.jaschke.alexandria.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.jaschke.alexandria.R;

/**
 * Created by Paulina on 2015-12-20.
 */
public class ConfirmBookDeletionDialog extends DialogFragment {

    private Listener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (Listener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_confirm_deletion, container, false);

        dialogView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteClicked();
                dismiss();
            }
        });

        dialogView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancelClicked();
                dismiss();
            }
        });

        return dialogView;
    }

    public interface Listener {
        void deleteClicked();

        void cancelClicked();
    }
}
