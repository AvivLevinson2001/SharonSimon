package com.example.sharonsimon.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.sharonsimon.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class TrophyAnimationDialog extends AppCompatDialogFragment {

    private AnimationDialogInterface listener;

    public interface AnimationDialogInterface{
        void onDialogDismiss();
    }

    public static TrophyAnimationDialog newInstance(String animationFileName, String titleText){
        TrophyAnimationDialog dialog = new TrophyAnimationDialog();
        Bundle arguments = new Bundle();
        arguments.putString("titleText",titleText);
        arguments.putString("animationFileName",animationFileName);
        dialog.setArguments(arguments);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View viewGroup = getActivity().getLayoutInflater().inflate(R.layout.trophy_animation_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(viewGroup);

        String titleText = getArguments().getString("titleText");
        String animationFileName = getArguments().getString("animationFileName");

        TextView titleTV = viewGroup.findViewById(R.id.title_tv);
        titleTV.setText(titleText);
        LottieAnimationView animationView = viewGroup.findViewById(R.id.animation_view);
        animationView.setAnimation(animationFileName);

        return builder.create();
    }

    public void setListener(AnimationDialogInterface listener){
        this.listener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if(listener != null)
            listener.onDialogDismiss();
        super.onDismiss(dialog);
    }
}
