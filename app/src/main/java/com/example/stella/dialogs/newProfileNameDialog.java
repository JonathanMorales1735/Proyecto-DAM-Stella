package com.example.stella.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.stella.R;

/**
 * newProfileNameDialog muestra un dialog en donde podemos introducir el nuevo nombre del perfil. Se usa para editar el nombre del perfil que queramos.
 */

public class newProfileNameDialog {

    public interface OnDialogClickListener {
        void onAccept(String text);
        void onCancel();
    }

    // originalName se utiliza para mostrar el nombre del perfil al abrir el dialog y editar a partir de ahí
    public static void show(Context context, String originalName, final OnDialogClickListener listener) {
        // Se establece la vista al dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_new_profile_name, null);
        final EditText editText = dialogView.findViewById(R.id.editText_newProfileName);
        editText.setText(originalName);

        // Se introduce el titulo traducido y la funcion de los botonees aceptar y cancelar
        builder.setView(dialogView)
                .setTitle(context.getResources().getString(R.string.set_new_name))
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