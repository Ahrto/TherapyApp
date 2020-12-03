package com.alberto.medaap2.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MyAlert extends AlertDialog {
    public MyAlert(Context context, String title, String message, String buttonText) {
        super(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Boton del Alert pulsado");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
