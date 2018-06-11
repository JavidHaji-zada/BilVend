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

import com.example.csgroupg.bilvend.R;

/**
 * EditAdvertisement Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class EditAdvertisement extends AppCompatDialogFragment {

    //parameters
    private EditText editTextTitle;
    private EditText editTextPrice;
    private EditText editTextDescription;
    private EditDialogListener listener;

    /**
     * This method starts the activity
     * @param savedInstanceState the instance of Bundle
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editadvertisement, null);

        builder.setView(view)
                .setTitle("Enter new advertisement details")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();
                String price = editTextPrice.getText().toString();
                listener.applyTexts(title , description, price);
            }
        });

        editTextTitle = view.findViewById(R.id.edit_title);
        editTextDescription = view.findViewById(R.id.edic_description);
        editTextPrice = view.findViewById(R.id.edit_price);
        return builder.create();
    }

    /**
     *
     * @param context
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
        void applyTexts(String title, String description, String price);
    }
}
