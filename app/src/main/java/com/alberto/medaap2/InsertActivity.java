package com.alberto.medaap2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alberto.medaap2.medicamentosApi.MedicamentoService;
import com.alberto.medaap2.models.Medicamento;
import com.alberto.medaap2.utilities.ScannerConfig;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InsertActivity extends AppCompatActivity {

    private Retrofit retrofit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        setup();

    }

    private void setup() {
//        Registrar componentes y asignacion de funciones
        Button btnBarCode = findViewById(R.id.btnBarCode);
        Button btnCode = findViewById(R.id.btnCode);
        Button btnManual = findViewById(R.id.btnManual);

        btnBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaner();
            }
        });

        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goInsertCodeActivity();
            }
        });

        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goInsertManualActivity();
            }
        });
    }

    //    Funcion para el escaner
    private void scaner() {
        ScannerConfig scannerConfig = new ScannerConfig(this);
        scannerConfig.escanear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String resultString = result.getContents();
        if (resultString != null) {
            String subcodigo = resultString.substring(6, 12);

            obtenerDatos(subcodigo);

        } else {
            Toast.makeText(this, R.string.lecturaCanceladaScaner, Toast.LENGTH_SHORT).show();
        }
    }

    //    Funcion para consultar datos de la API
    @SuppressLint("ResourceAsColor")
    private void obtenerDatos(String cn) {

        final ProgressBar progressBar;

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.contenedor);
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleLarge);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(layoutParams);

        progressBar.setIndeterminate(true);

        constraintLayout.addView(progressBar);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://cima.aemps.es/cima/rest/presentacion/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MedicamentoService service = retrofit.create(MedicamentoService.class);
        Call<Medicamento> medicamentoCall = service.obtenerMedicamentoApi(cn);

        medicamentoCall.enqueue(new Callback<Medicamento>() {
            @Override
            public void onResponse(Call<Medicamento> call, Response<Medicamento> response) {
                if (response.isSuccessful()) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        Medicamento medicamento = response.body();
                        String nombreMed = medicamento.getNombre();
                        String cn = medicamento.getCn();

                        String nregistro = medicamento.getNregistro();

                        System.out.println("Nombre del medicamento: " + nombreMed);
                        System.out.println("CN****************************" + cn);
                        System.out.println("REGISTRO API***********************" + nregistro);
                        goSaveActivity(nombreMed, nregistro, cn);
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        goAlertActivity();
                        Toast.makeText(getApplicationContext(), R.string.noSeHaEncontrado, Toast.LENGTH_LONG).show();
                    }
                }else {
                    progressBar.setVisibility(View.GONE);
                    System.out.println("Error body" +  response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Medicamento> call, Throwable t) {
                System.out.println("On failure" + t.getMessage());
            }
        });
    }

    //    Funcion para ir al activity SaveActivity desde el escaner
    private void goSaveActivity(String name, String nregistro, String cn) {
        Intent intent = new Intent(this, SaveActivity.class);
        intent.putExtra("data", name);
        intent.putExtra("nregistro", nregistro);
        intent.putExtra("cn", cn);
        startActivity(intent);
    }


    //    Funcion para ir al Activity para introducir el codigo nacional
    private void goInsertCodeActivity() {
        Intent intent = new Intent(this, InsertCodeActivity.class);
        finish();
        startActivity(intent);
    }

    //    Funcion para ir al Activity Insert Manual
    private void goInsertManualActivity() {
        Intent intent = new Intent(this, InsertManualActivity.class);
        finish();
        startActivity(intent);
    }

    private void goAlertActivity(){
//        Manda el modo de introduccion de los datos para mostrar el texto en el Boton correspondiente
        String type = "scan";
        Intent intent = new Intent(this, AlertActivity.class);
        intent.putExtra("type", type);
        finish();
        startActivity(intent);
    }
}