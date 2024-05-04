package com.example.stella.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.stella.R;

/**
 * succesDialog mueestra un pequeño dialog en donde mostramos, segun el type, un mensaje de éxito
 */

public class successDialog {
    public static void showDialog(Context context, int type) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_success);
        Button closeButton = dialog.findViewById(R.id.btn_closeNewAccDialog);
        TextView message = dialog.findViewById(R.id.txt_successContentMessage);
        switch (type){
            case 0:
                message.setText(context.getResources().getString(R.string.newtaskcreatedsuccessfully));
                break;
            case 1:
                message.setText(context.getResources().getString(R.string.taskEditedSuccessfully));
                break;
        }


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
