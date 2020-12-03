package com.alberto.medaap2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alberto.medaap2.utilities.MyAlert;

public class InsertManualActivity extends AppCompatActivity {

    private EditText etNameMed;
    private Button btnAnadir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_manual);

        setup();
    }

    private void setup() {
        etNameMed = findViewById(R.id.etNameMed);
        btnAnadir = findViewById(R.id.btnAnadir);

        btnAnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goSaveActivity();
            }
        });

    }

    private void goSaveActivity() {
        String nameMed = etNameMed.getText().toString();
        if (nameMed.isEmpty()) {
            MyAlert myAlert = new MyAlert(this, getString(R.string.insertCodeTitleAlert),
                    getString(R.string.insertManualName), getString(R.string.ok));
        } else {
            Intent intent = new Intent(this, SaveActivity.class);
            intent.putExtra("data", nameMed);
            intent.putExtra("nregistro", "0");
            intent.putExtra("cn", "0");
            finish();
            startActivity(intent);
        }

    }
}