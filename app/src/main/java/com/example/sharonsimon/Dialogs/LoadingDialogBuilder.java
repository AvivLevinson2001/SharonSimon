package com.example.sharonsimon.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.sharonsimon.R;

/**
 * Created by ronto on 16-Dec-18.
 */
public class LoadingDialogBuilder
{
    /**
     * @param context
     * @return A loading dialog
     */
    public static Dialog createLoadingDialog(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.loading_dialog, null);
        builder.setView(v);
        builder.setCancelable(false);
        final Dialog dialog = builder.create();
        return  dialog;
    }
}
