package it.jaschke.alexandria.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.jaschke.alexandria.R;

public class ConfirmLeaveDialog extends DialogFragment {

    private Listener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (Listener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogRoot = inflater.inflate(R.layout.dialog_confirm_leave, container, false);
        dialogRoot.findViewById(R.id.leave_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.leaveButtonClicked();
                dismiss();
            }
        });
        dialogRoot.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.saveButtonClicked();
                dismiss();
            }
        });
        return dialogRoot;
    }

    public interface Listener {
        void leaveButtonClicked();

        void saveButtonClicked();
    }
}
