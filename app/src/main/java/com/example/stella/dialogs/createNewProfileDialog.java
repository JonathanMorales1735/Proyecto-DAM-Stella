package com.example.stella.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.stella.R;

public class createNewProfileDialog {

    public interface OnDialogClickListener {
        void onAccept(String text);
        void onCancel();
    }

    public static void show(Context context, final OnDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_new_profile_name, null);
        final EditText editText = dialogView.findViewById(R.id.editText);


        builder.setView(dialogView)
                .setTitle(context.getResources().getString(R.string.set_new_profile_name))
                .setPositiveButton(context.getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String textoIngresado = editText.getText().toString();
                        if (listener != null) {
                            listener.onAccept(textoIngresado);
                        }
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onCancel();
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}