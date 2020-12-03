package com.alberto.medaap2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alberto.medaap2.models.Usuario;
import com.alberto.medaap2.utilities.MyAlert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class CreateUserActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPsw;
    private EditText etNombre;

    private Button btnCreate;
    private Button btnLogin;

    //    Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);


        etEmail = findViewById(R.id.etEmail);
        etNombre = findViewById(R.id.etNombre);
        etPsw = findViewById(R.id.etPsw);
        btnCreate = findViewById(R.id.btnCreate);
        btnLogin = findViewById(R.id.btnLogin);

        setup();
    }

    private void setup() {

        setTitle("Registro");

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Bonton crear pulsado");
                if (!validarEmail(etEmail.getText().toString())) {
                    MyAlert myAlert = new MyAlert(CreateUserActivity.this, getString(R.string.titleEmailCreateUser), getString(R.string.textEmailCreateUser), getString(R.string.ok));
                    myAlert.create();
                } else {
                    System.out.println("Email Correcto");
                    if (etNombre.getText().toString().isEmpty()){
                        MyAlert myAlert = new MyAlert(CreateUserActivity.this, getString(R.string.insertCodeTitleAlert), getString(R.string.txtNameCreateUser), getString(R.string.ok));
                        myAlert.create();
                    }else {
                        if (etPsw.getText().toString().isEmpty()) {
                            MyAlert myAlert = new MyAlert(CreateUserActivity.this, getString(R.string.insertCodeTitleAlert), getString(R.string.txtPswCreateUser), getString(R.string.ok));
                            myAlert.create();
                        } else {
                            createUser(etEmail.getText().toString(), etPsw.getText().toString());
                            System.out.println("psw Correcto");
                        }
                    }
                }
            }
        });
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


    private void createUser(String email, String psw) {
        //        ProgressBar
        final ProgressBar progressBar;

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.contenedor);
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(layoutParams);

        progressBar.setIndeterminate(true);

        linearLayout.addView(progressBar);

        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, psw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            System.out.println("Completado");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Usuario usuario = new Usuario();
                            usuario.setUid(user.getUid());
                            usuario.setNombre(etNombre.getText().toString());

//                            Actualizar el nombre de usuario a nivel FirebaseAuth
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(usuario.getNombre()).build();
                            user.updateProfile(profileUpdates);

                            saveUser(user.getUid(), usuario);
                            goToPrincipalActivity();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            MyAlert myAlert = new MyAlert(CreateUserActivity.this, getString(R.string.titleAlertFalta)
                                    , getString(R.string.txtFailCreateUser), getString(R.string.ok));
                            myAlert.create();
                            System.out.println("No completado");

                        }
                    }
                });
    }

    private void saveUser(String userUid, Usuario usuario) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Usuarios").child(userUid);
        reference.setValue(usuario);
    }

    private void goToPrincipalActivity() {
        Intent intent = new Intent(CreateUserActivity.this, PrincipalActivity.class);

        finish();
        startActivity(intent);
    }

}