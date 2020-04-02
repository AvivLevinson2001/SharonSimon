package com.example.sharonsimon.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.example.sharonsimon.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class TrophyAnimationDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View viewGroup = getActivity().getLayoutInflater().inflate(R.layout.trophy_animation_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(viewGroup);

        return builder.create();
    }
}
