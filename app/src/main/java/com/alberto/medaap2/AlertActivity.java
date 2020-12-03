package com.alberto.medaap2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlertActivity extends AppCompatActivity {

    private TextView tvAlert;
    private Button btnRetry;
    private Button btnManual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        setup();
    }

    private void setup() {
        tvAlert = findViewById(R.id.tvAlert);
        btnRetry = findViewById(R.id.btnRetry);
        btnManual = findViewById(R.id.btnManual);

        loadExtras();

        btnRetry.setOnClickListener(new View.OnClickListener() {
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

    //    Recibe los datos del putExtra que indica el tipo de introduccion de mismos, cambian el texto del boton segun corresponda
    public void loadExtras() {
        Bundle bundle = getIntent().getExtras();
        String type = bundle.getString("type");

        if (type.equals("code")) {
            btnRetry.setText(getString(R.string.btnRetryCode));
        }
        if (type.equals("scan")) {
            btnRetry.setText(getString(R.string.btnRetryManual));
        }

    }

    public void goInsertCodeActivity() {
        Intent intent = new Intent(this, InsertCodeActivity.class);
        finish();
        startActivity(intent);
    }

    public void goInsertManualActivity() {
        Intent intent = new Intent(this, InsertManualActivity.class);
        finish();
        startActivity(intent);
    }
}