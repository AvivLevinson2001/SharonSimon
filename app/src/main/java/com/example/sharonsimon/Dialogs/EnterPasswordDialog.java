package com.example.sharonsimon.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.sharonsimon.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class EnterPasswordDialog extends AppCompatDialogFragment {

    private EditText passwordET;
    private EnterPasswordDialogInterface listener;

    public interface EnterPasswordDialogInterface{
        void onPositiveButtonClick(String password);
        void onNegativeButtonClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity)context;
        try{
            listener = (EnterPasswordDialogInterface) activity;
        }catch (ClassCastException e){
            throw new ClassCastException("Activity: " + activity.toString() + " must implement EnterPasswordDialogInterface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View viewGroup = getActivity().getLayoutInflater().inflate(R.layout.enter_password_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        passwordET = viewGroup.findViewById(R.id.password_et);

        builder.setView(viewGroup)
                .setTitle("הכנס סיסמה")
                .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onNegativeButtonClick();
                    }
                })
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String password;
                        if(passwordET.getText() == null)
                            password = "";
                        else
                            password = passwordET.getText().toString();
                        listener.onPositiveButtonClick(password);
                    }
                });


        return builder.create();
    }
}
