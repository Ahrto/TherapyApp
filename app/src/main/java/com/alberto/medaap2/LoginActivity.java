package com.alberto.medaap2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alberto.medaap2.utilities.MyAlert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPsw;

    private Button btnLogin;

    //    Firebase
    FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPsw = findViewById(R.id.etPsw);

        btnLogin = findViewById(R.id.btnLogin);
        sesionAbierta();
        setup();

    }

    private void setup() {
        setTitle("Iniciar sesión");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Boton pulsado");
                if (!validarEmail(etEmail.getText().toString())) {
                    MyAlert myAlert = new MyAlert(LoginActivity.this, getString(R.string.insertCodeTitleAlert), getString(R.string.emailLogin), getString(R.string.ok));
                    myAlert.create();
                } else {
                    System.out.println("Email Correcto");

                    if (etPsw.getText().toString().isEmpty()) {
                        MyAlert myAlert = new MyAlert(LoginActivity.this, getString(R.string.insertCodeTitleAlert), getString(R.string.pswLogin), getString(R.string.ok));
                        myAlert.create();
                    } else {
                        loginUser(etEmail.getText().toString(), etPsw.getText().toString());

                    }
                }
            }
        });
    }

    //    Comprobar inicio de sesion de usuario
    private void sesionAbierta() {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.prefs_file), Context.MODE_PRIVATE);

        String email = sharedPref.getString("email", null);
        String nombre = sharedPref.getString("nombre", null);

        if (email != null && nombre != null) {
            System.out.println("Iniciada sesion************************************" + email);
            Toast.makeText(this, getString(R.string.wellcomeUser) + nombre, Toast.LENGTH_SHORT).show();
            goToPrincipalActivity();
        } else {
            System.out.println("Sesion no iniciada*****************************");
        }
    }


    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


    @SuppressLint("ResourceAsColor")
    private void loginUser(String email, String psw) {
//        ProgressBar

        final ProgressBar progressBar;
        LinearLayoutCompat linearLayoutCompat = findViewById(R.id.contenedor);
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(layoutParams);
        progressBar.setIndeterminate(true);
        linearLayoutCompat.addView(progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(email, psw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Usuario logeado Completado");
                            progressBar.setVisibility(View.GONE);
                            goToPrincipalActivity();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            MyAlert myAlert = new MyAlert(LoginActivity.this, getString(R.string.insertCodeTitleAlert)
                                    , getString(R.string.txtFailLogin), getString(R.string.ok));
                            myAlert.create();
                            System.out.println("No completado");
                        }
                    }
                });
    }


    private void goToPrincipalActivity() {
        Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
        finish();
        startActivity(intent);
    }
}