package com.alberto.medaap2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.alberto.medaap2.adapters.MedicamentoAdapter;
import com.alberto.medaap2.models.Medicamento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class PrincipalActivity extends AppCompatActivity {

    //    RecyclerView de Medicamentos
    private DatabaseReference referenceMedicamento;
    private MedicamentoAdapter medicamentoAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Medicamento> medicamentoArrayList = new ArrayList<>();
    private ImageButton btnSettings;
    private TextView tvNombreUsuario;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        getSupportActionBar().hide();


//        Componentes para el RecyclerView de Medicamentos
        referenceMedicamento = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setVisibility(View.INVISIBLE);
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);

        getMendicamentosFromFirebase();

        setup();
        preferences();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        medicamentoArrayList.clear();
        getMendicamentosFromFirebase();
    }

    private void setup() {
        sesionAbierta();
//        Abrir ajustes del telefono al hacer click en el boton settings
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });

//        Registrar componentes y asignacion de funciones
        Button btnIntroducirMed = findViewById(R.id.btnIntroducirMed);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnIntroducirMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Boton Introducir pulsado");
                goInsertActivity();

            }
        });

//        TODO Revisar todo lo que tiene que ver con el RecyclerView
//        Funcion para el boton listar
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logOut();
                goInicialActivity();
            }
        });

    }

    //    Preferences
    private void preferences() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String emailUser = user.getEmail();
        String userUid = user.getUid();
        String nombre = user.getDisplayName();

        System.out.println("Email********************" + emailUser);
        System.out.println("nombre***********************" + nombre);
        if (nombre != null) {
//            Toast.makeText(PrincipalActivity.this, "Bienvenido " + nombre, Toast.LENGTH_LONG).show();
            tvNombreUsuario.setText(getString(R.string.wellcomeUser) + " " + nombre);
        } else {
            tvNombreUsuario.setText(R.string.wellcome);
        }


        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.prefs_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", emailUser);
        editor.putString("nombre", nombre);
        editor.apply();

    }

    private void sesionAbierta() {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.prefs_file), Context.MODE_PRIVATE);

        String email = sharedPref.getString("email", null);
        String nombre = sharedPref.getString("nombre", null);
        setTitle(nombre);
    }

    //    Borrado de prefs/cerrar sesion
    private void logOut() {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.prefs_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    private void goInsertActivity() {
        Intent intent = new Intent(PrincipalActivity.this, InsertActivity.class);
        finish();
        startActivity(intent);
//        sesionAbierta();
    }

    private void goInicialActivity() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(PrincipalActivity.this, InicialActivity.class);
        finish();
        startActivity(intent);
    }

    //    Lectura de datos
    private void lectura() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("Medicamentos");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Medicamento medicamento = snapshot.getValue(Medicamento.class);
                System.out.println(medicamento.getIdAlarma());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //    Recycler View
    private void getMendicamentosFromFirebase() {

        final ProgressBar progressBar;

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.contenedor);
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(layoutParams);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        progressBar.setIndeterminate(true);
        constraintLayout.addView(progressBar);

        referenceMedicamento.child("Medicamentos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                btnSettings.setVisibility(View.VISIBLE);
                if (snapshot.exists()) {
                    medicamentoArrayList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        progressBar.setVisibility(View.GONE);
                        String nombreMedicamento = ds.child("nombre").getValue().toString();
                        String usuario = ds.child("usuario").getValue().toString();
                        int idAlarma = Integer.parseInt(ds.child("idAlarma").getValue().toString());
                        if (idAlarma == 0){
                            System.out.println("Alarma cancelada");
                        }
                        String dias = ds.child("dias").getValue().toString();
                        String horas = ds.child("horas").getValue().toString();
                        String inicio = ds.child("inicio").getValue().toString();
                        String pushKey = ds.child("pushKey").getValue().toString();
                        String nregistro = ds.child("nregistro").getValue().toString();
                        String cn = ds.child("cn").getValue().toString();
                        String fechaInicio = ds.child("fechaInicio").getValue().toString();

//                        Solo se muestran los mediamentos que tienen el usuario correspondiente al que inicia sesion
                        if (usuario.equals(FirebaseAuth.getInstance().getUid())) {
                            medicamentoArrayList.add(new Medicamento(nombreMedicamento, usuario, idAlarma, dias,
                                    horas, inicio, pushKey, nregistro, cn, fechaInicio));
                        }
                    }
                    medicamentoAdapter = new MedicamentoAdapter(R.layout.medicamento_view, medicamentoArrayList);

//                    Metodo Onclick
                    medicamentoAdapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int alarma = medicamentoArrayList.get(mRecyclerView.getChildAdapterPosition(v)).getIdAlarma();
                            String nombreMedicamento = medicamentoArrayList.get(mRecyclerView.getChildAdapterPosition(v)).getNombre();
                            String usuario = medicamentoArrayList.get(mRecyclerView.getChildAdapterPosition(v)).getUsuario();
                            String dias = medicamentoArrayList.get(mRecyclerView.getChildAdapterPosition(v)).getDias();
                            String horas = medicamentoArrayList.get(mRecyclerView.getChildAdapterPosition(v)).getHoras();
                            String inicio = medicamentoArrayList.get(mRecyclerView.getChildAdapterPosition(v)).getInicio();
                            String nregistro = medicamentoArrayList.get(mRecyclerView.getChildAdapterPosition(v)).getNregistro();
                            String pushKey = medicamentoArrayList.get(mRecyclerView.getChildAdapterPosition(v)).getPushKey();
                            String fechaInicio = medicamentoArrayList.get(mRecyclerView.getChildAdapterPosition(v)).getFechaInicio();

                            System.out.println("REGISTRO API DESDE PRINCIPAL**********************" + nregistro);
                            System.out.println("PUSHKEY DESDE PRINCIPAL**********************" + pushKey);

                            goToDetailMed(alarma, nombreMedicamento, usuario, dias, horas, inicio, nregistro, pushKey, fechaInicio);
                        }
                    });

                    mRecyclerView.setAdapter(medicamentoAdapter);
                }
                    progressBar.setVisibility(View.GONE);

            }
            

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void goToDetailMed(int alarma, String nombreMedicamento, String usuario, String dias, String horas, String inicio, String nregistro, String pushKey, String fechaInicio) {
        Intent intent = new Intent(PrincipalActivity.this, DetailsActivity.class);
        intent.putExtra("idAlarma", alarma);
        intent.putExtra("nombreMedicamento", nombreMedicamento);
        intent.putExtra("usuario", usuario);
        intent.putExtra("dias", dias);
        intent.putExtra("horas", horas);
        intent.putExtra("inicio", inicio);
        intent.putExtra("nregistro", nregistro);
        intent.putExtra("pushKey", pushKey);
        intent.putExtra("fechaInicio", fechaInicio);

        startActivity(intent);
        finish();

        System.out.println("Medicamento{" +
                "nombre='" + nombreMedicamento + '\'' +
                ", dias='" + dias + '\'' +
                ", horas='" + horas + '\'' +
                ", inicio='" + inicio + '\'' +
                ", idAlarma=" + alarma +
                ", usuario='" + usuario + '\'' +
                '}');

    }
}