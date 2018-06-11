package com.example.csgroupg.bilvend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * ResetPasswordDialog Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class ResetPasswordDialog extends AppCompatDialogFragment {
    private EditText emailTextView;
    private EditDialogListener listener;

    /**
     * This method open a dialog for resetting password
     * @param savedInstanceState the instance of Bundle
     * @return instance of Dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_reset_password, null);
        builder.setView(view)
                .setTitle("Are you Sure you want to reset your password")
                .setNegativeButton("no", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            /**
             * This method applies the email
             * @param dialog instance of DialogInterface
             * @param which
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailTextView.getText().toString();
                listener.applyTexts(email);
            }
        });

        emailTextView = view.findViewById(R.id.reset_pass);
        return builder.create();
    }

    /**
     *
     * @param context the context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (EditDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement EditDialogListener");
        }
    }

    public interface EditDialogListener{
        void applyTexts(String email);
    }
}
