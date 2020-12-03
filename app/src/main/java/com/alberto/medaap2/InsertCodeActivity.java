package com.alberto.medaap2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alberto.medaap2.medicamentosApi.MedicamentoService;
import com.alberto.medaap2.models.Medicamento;
import com.alberto.medaap2.utilities.MyAlert;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InsertCodeActivity extends AppCompatActivity {

    private EditText etCode;
    private Button btnInsertCode;
    private Button btnDonde;

    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_code);

        setup();

    }

    private void setup() {
        etCode = findViewById(R.id.etCode);

        btnInsertCode = findViewById(R.id.btnInsertCode);

        btnDonde = findViewById(R.id.btnDonde);

        btnInsertCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerDatos(etCode.getText().toString());

            }
        });

        btnDonde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView image = new ImageView(InsertCodeActivity.this);
                image.setImageResource(R.drawable.cn_texto);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(InsertCodeActivity.this).
                                setMessage(R.string.dondeCodigoNacional).
                                setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).
                                setView(image);
                builder.create().show();
            }
        });
    }

    //    Funcion para obtener datos de la API por CN
    private void obtenerDatos(String cn) {
//        Comprobar campos
        String codigo = etCode.getText().toString();
        if (codigo.isEmpty()) {
            MyAlert myAlert = new MyAlert(this, getString(R.string.insertCodeTitleAlert), getString(R.string.insertCodeCn), getString(R.string.ok));
            myAlert.create();
        } else {
            ProgressBar progressBar;
            ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.contenedor);
            progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
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
                            Medicamento medicamento = response.body();
                            String nombreMed = medicamento.getNombre();
                            String cn = medicamento.getCn();
                            String nregistro = medicamento.getNregistro();

                            goSaveActivity(nombreMed, nregistro, cn);
                        } catch (Exception e) {
                            goAlertActivity();
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.introduceloDeNuevo),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        System.out.println("Error body" + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Medicamento> call, Throwable t) {
                    System.out.println("On failure" + t.getMessage());
                }
            });
        }
    }

    private void goSaveActivity(String name, String nregistro, String cn) {
        Intent intent = new Intent(this, SaveActivity.class);
        intent.putExtra("data", name);
        intent.putExtra("nregistro", nregistro);
        intent.putExtra("cn", cn);
        finish();
        startActivity(intent);
    }

    private void goAlertActivity() {
        String type = "code";

        Intent intent = new Intent(this, AlertActivity.class);
//        Mandamos el tipo de introduccion de los datos para mostrar el texto en el boton correctamente
        intent.putExtra("type", type);
        finish();
        startActivity(intent);

    }
}