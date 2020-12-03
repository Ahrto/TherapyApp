package com.alberto.medaap2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class InicialActivity extends AppCompatActivity {

    private Button btnRegistrar;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);

//      DISCLAIMER
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.titleDisclaimer);
        builder.setMessage(R.string.txtDisclaimer);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.pill_38165);
        builder.setPositiveButton(R.string.btnDisclaimer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sesionAbierta();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnLogin = findViewById(R.id.btnLogin);


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCreateUserActivity();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
    }

    //    Comprobar inicio de sesion de usuario
    private void sesionAbierta() {

        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.prefs_file), Context.MODE_PRIVATE);

        String email = sharedPref.getString("email", null);
        String uid = sharedPref.getString("nombre", null);

        if (email != null && uid != null) {
            System.out.println("Iniciada sesion************************************" + email);
            goToPrincipalActivity();
        } else {
            System.out.println("Sesion no iniciada*****************************");
        }

    }

    private void goToCreateUserActivity() {
        Intent intent = new Intent(InicialActivity.this, CreateUserActivity.class);

        startActivity(intent);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(InicialActivity.this, LoginActivity.class);

        startActivity(intent);
    }

    private void goToPrincipalActivity() {
        Intent intent = new Intent(InicialActivity.this, PrincipalActivity.class);

        startActivity(intent);
    }
}