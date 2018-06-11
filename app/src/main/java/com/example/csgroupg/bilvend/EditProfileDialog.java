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
 * EditProfileDialog Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class EditProfileDialog extends AppCompatDialogFragment {

    //parameters
    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextNumber;
    private EditDialogListener listener;

    /**
     * This method creates a dialog for editing profile
     * @param savedInstanceState the bundle
     * @return instance of Dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editprofile, null);

        builder.setView(view)
                .setTitle("Change User Details")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextName.getText().toString();
                String surname = editTextSurname.getText().toString();
                String number = editTextNumber.getText().toString();
                listener.applyTexts(name , surname, number);
            }
        });

        editTextName = view.findViewById(R.id.reset_pass);
        editTextSurname = view.findViewById(R.id.edit_surname);
        editTextNumber = view.findViewById(R.id.edit_number);
        return builder.create();
    }

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
        void applyTexts(String name, String surname, String number);
    }
}
