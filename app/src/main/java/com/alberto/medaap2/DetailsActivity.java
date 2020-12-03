package com.alberto.medaap2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alberto.medaap2.receivers.AlarmReceiver;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailsActivity extends AppCompatActivity {

    private TextView tvNombreMedicamento;
    private TextView tvDias;
    private TextView tvHoras;
    private TextView tvInicio;
    private TextView tvIdAlarma;
    private TextView tvFechaInicio;
    private Button btnDeleteMedicamento;
    private Button btnUrl;
    private Button btnReturn;


    private String nombreMedicamento;
    private int idAlarma;
    private String pushKey;
    private String nregistro;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tvNombreMedicamento = findViewById(R.id.tvNombreMedicamento);
        tvDias = findViewById(R.id.tvDias);
        tvHoras = findViewById(R.id.tvHoras);
        tvInicio = findViewById(R.id.tvInicio);
        tvFechaInicio = findViewById(R.id.tvFechaInicio);
        btnDeleteMedicamento = findViewById(R.id.btnDeleteMedicamento);
        btnUrl = findViewById(R.id.btnUrl);
        btnReturn = findViewById(R.id.btnReturn);

        loadExtras();
        ocultarBotonUrl();

        btnDeleteMedicamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(DetailsActivity.this);
                alert.setTitle(getString(R.string.atencion));
                alert.setMessage(getString(R.string.eliminarTratamiento));
                alert.setCancelable(false);

                alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMedicamento(pushKey);
//                Es necesario cancelar la alarma tambien
                        cancelAlarm(idAlarma);
                    }
                });
                alert.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();



            }
        });

        btnUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Boton url pulsado");
                obtenerInfo();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPrincipalActivity();
            }
        });


    }

    private void loadExtras() {

        Bundle extras = getIntent().getExtras();

        idAlarma = extras.getInt("idAlarma");
        nombreMedicamento = extras.getString("nombreMedicamento");
        pushKey = extras.getString("pushKey");
        String dias = extras.getString("dias");
        String horas = extras.getString("horas");
        String inicio = extras.getString("inicio");
        String fechaInicio = extras.getString("fechaInicio");
        nregistro = extras.getString("nregistro");


        tvNombreMedicamento.setText(getString(R.string.nombreDelMedicamentoDetails) + " " + nombreMedicamento);
        tvDias.setText(getString(R.string.durante) + " " + dias + " " + getString(R.string.dias));
        tvHoras.setText(getString(R.string.cadaMayus) + " " + horas + " " + getString(R.string.horasMinus));
        tvInicio.setText(getString(R.string.laPrimeraTomaEs) + " " + inicio + " " + getString(R.string.horasMinus));
        tvFechaInicio.setText(getString(R.string.fecha_inicio) + " " + fechaInicio);

    }

    //    Consultar prospecto
    public void obtenerInfo() {
//          https://cima.aemps.es/cima/dochtml/p/70310/Prospecto.html

        String url = "https://cima.aemps.es/cima/dochtml/p/" + nregistro + "/Prospecto.html";

        System.out.println(url);

        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void cancelAlarm(int idAlarma) {
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent alarmaPending = PendingIntent.getBroadcast(getBaseContext(), idAlarma, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//Cancela Alarma.
        alarmManager.cancel(alarmaPending);

        Intent intent1 = new Intent(DetailsActivity.this, PrincipalActivity.class);
        startActivity(intent1);

        System.out.println("Alarma cancelada");
    }

    private void deleteMedicamento(String pushKey) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Medicamentos/" + pushKey).removeValue();

        Intent intent = new Intent(DetailsActivity.this, PrincipalActivity.class);
        finish();
        startActivity(intent);
    }

    private void ocultarBotonUrl() {
        if (nregistro.equals("0")) {
            btnUrl.setVisibility(View.INVISIBLE);
        }
    }

    private void goToPrincipalActivity() {
        Intent intent = new Intent(DetailsActivity.this, PrincipalActivity.class);
        finish();
        startActivity(intent);
    }

}