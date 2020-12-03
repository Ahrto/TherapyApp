package com.alberto.medaap2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alberto.medaap2.models.Medicamento;
import com.alberto.medaap2.receivers.AlarmReceiver;
import com.alberto.medaap2.utilities.MyAlert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class SaveActivity extends AppCompatActivity {

    private TextView tvNameMed;
    private TextView tvDias;
    private TextView tvHoras;
    private TextView tvInicio;
    private TextView tvDiaInicio;
    private EditText etDias;
    private EditText etHoras;
    private EditText etInicio;
    private EditText etDiaInicio;
    private Button btnSave;


    //    Alarma
    private AlarmManager alarmManager;
    private static final int ID_UNICA = 0;
    final int id = (int) System.currentTimeMillis() / 1000;
    Random generator = new Random();
    int id_nuevo = id * 5 + generator.nextInt();


    //    Notificaciones
    private static final String CHANNEL_ID = "NOTIFICACION";

    //    Firebase
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;


    //    ID del registro en Firebase
    private String pushKey;
    private String nregistro;
    private String cn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        setup();
    }

    private void setup() {

        tvNameMed = findViewById(R.id.tvNameMed);
        etDias = findViewById(R.id.etDias);
        etHoras = findViewById(R.id.etHoras);
        etInicio = findViewById(R.id.etInicio);
        etDiaInicio = findViewById(R.id.etDiaInicio);
        btnSave = findViewById(R.id.btnSave);

        setTitle(getString(R.string.estableceTuTratamiento));
        loadExtras();
        createNotificationChannel();


//        Funcion para el editText de inicio tratamiento
        etInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarHora();
            }
        });

//        Funcion click calendario
        etDiaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarDia();
            }
        });

//        Funcion para el boton Save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarCampos();
                establecerAlarma();

            }
        });
    }

    //    Funcion que recoge los extras
    private void loadExtras() {
        Bundle extras = getIntent().getExtras();

        String data = extras.getString("data");
        nregistro = extras.getString("nregistro", null);
        cn = extras.getString("cn");
        System.out.println("Nombre: " + data);
        tvNameMed.setText(data);
    }


    //    Funcion seleccionar dia
    private void seleccionarDia() {
        final Calendar calendar = Calendar.getInstance();
        int diaInicio = calendar.get(Calendar.DAY_OF_MONTH);
        int mesInicio = calendar.get(Calendar.MONTH);
        int yearInicio = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(SaveActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearInicio, int mesInicio, int diaInicio) {
                System.out.println("*********************************************" + diaInicio + "/" + (mesInicio + 1) + "/" + yearInicio);
                etDiaInicio.setText(String.format("%02d/%02d/%04d", diaInicio, mesInicio + 1, yearInicio));
            }
        }, yearInicio, mesInicio, diaInicio);
        datePickerDialog.show();
    }

    //    Funcion para seleccionar la hora de las tomas
    private void seleccionarHora() {
        final Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minutos = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, 2, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                etInicio.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, hora, minutos, true);
        timePickerDialog.show();
    }

    private void comprobarCampos() {
        String dias = etDias.getText().toString();
        String horas = etHoras.getText().toString();
        String inicio = etInicio.getText().toString();


//        Comprueba si los campos no estan vacios
        if (dias.isEmpty() || horas.isEmpty() || inicio.isEmpty()) {
            MyAlert myAlert = new MyAlert(this, getString(R.string.insertCodeTitleAlert), getString(R.string.txtCompletaCampos), getString(R.string.ok));
            myAlert.create();
        } else {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            long timeActualMs = timestamp.getTime();
//            System.out.println("TIMESTAMP**********************************" + timeActualMs);
//            save();
        }
    }

    //    Funcion guardar en Firebase
    private void save() {
        Toast.makeText(this, getString(R.string.tratamientoAgregado), Toast.LENGTH_SHORT).show();
        firebaseAuth = FirebaseAuth.getInstance();

        String dias = etDias.getText().toString();
        String horas = etHoras.getText().toString();
        String inicio = etInicio.getText().toString();
        String name = tvNameMed.getText().toString();
        String uidUsuario = firebaseAuth.getUid();
        String fechaInicio = etDiaInicio.getText().toString();

        Medicamento medicamento = new Medicamento();
        medicamento.setDias(dias);
        medicamento.setNombre(name);
        medicamento.setHoras(horas);
        medicamento.setInicio(inicio);
        medicamento.setIdAlarma(id_nuevo);
        medicamento.setUsuario(uidUsuario);
        medicamento.setNregistro(nregistro);
        medicamento.setCn(cn);
        medicamento.setFechaInicio(fechaInicio);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Medicamentos");

        pushKey = reference.push().getKey();
        medicamento.setPushKey(pushKey);

//        Se establece un value para el hijo de la id anterior
        reference.child(pushKey).setValue(medicamento);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.recuerda));
        builder.setMessage(getString(R.string.tomar) + " " + name + " " + getString(R.string.cada) + " " + horas + " " + getString(R.string.horasMinus));
        builder.setCancelable(false);
        builder.setIcon(R.drawable.pill_38165);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                goPrincipalActivity();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //    Funcion para ir a la PrincipalActivity
    private void goPrincipalActivity() {
        Intent intent = new Intent(this, PrincipalActivity.class);
//        Prueba
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    //    Funcion para establecer la alarma
    private void establecerAlarma() {

        if (etInicio.getText().toString().isEmpty() || etHoras.getText().toString().isEmpty()
                || etDias.getText().toString().isEmpty()) {
            System.out.println("Faltan datos para guardar el medicamento");
        } else {
            int dias = Integer.parseInt(etDias.getText().toString());
            int horaInicio = Integer.parseInt(etInicio.getText().toString().substring(0, 2));
            int minutosInicio = Integer.parseInt(etInicio.getText().toString().substring(3, 5));
            int repeticion = Integer.parseInt(etHoras.getText().toString());
            //        TODO Cambiar a 3600000 que es el tiempo en horas, Ahora para las pruebas esta en minutos 60000
//            TODO Esta en 5 minutos 1 = 5 minutos
            long repeticionMs = repeticion * 3600000;

//            Recoger fecha inicio
            int diaInicio = Integer.parseInt(etDiaInicio.getText().toString().substring(0, 2));
            int mesInicio = Integer.parseInt(etDiaInicio.getText().toString().substring(3, 5)) - 1;
            int yearInicio = Integer.parseInt(etDiaInicio.getText().toString().substring(6, 10));

//        Establecer calendario
            Calendar calendarInicio = Calendar.getInstance();
            calendarInicio.setTimeInMillis(System.currentTimeMillis());
            calendarInicio.set(Calendar.YEAR, yearInicio);
            calendarInicio.set(Calendar.MONTH, mesInicio);
            calendarInicio.set(Calendar.DAY_OF_MONTH, diaInicio);
            calendarInicio.set(Calendar.HOUR_OF_DAY, horaInicio);
            calendarInicio.set(Calendar.MINUTE, minutosInicio);
            calendarInicio.set(Calendar.SECOND, 0);
            System.out.println("Calendario de inicio: " + calendarInicio.getTime());
            System.out.println("DIA: " + diaInicio + "-Mes: " + (mesInicio + 1) + " - Año: " + yearInicio + "''''?????????????????");

//            Comprobacion hora final
            Date dateSistema = new Date();
            dateSistema.getTime();
            if (calendarInicio.getTime().before(dateSistema)) {
                MyAlert myAlert = new MyAlert(SaveActivity.this, getString(R.string.atencion), getString(R.string.laFechaDebeSerPosterior), getString(R.string.ok));
                myAlert.create();
            }
            if (calendarInicio.getTime().after(dateSistema)) {
                save();
                //        Extraer el nombre del usuario actual
                FirebaseUser user = firebaseAuth.getCurrentUser();

                alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("nombreMed", tvNameMed.getText().toString());
                intent.putExtra("usuario", user.getDisplayName());
                intent.putExtra("dias", etDias.getText().toString());

//            Pasar hora final alarma
                intent.putExtra("inicio", calendarInicio.getTimeInMillis());

//            TODO Para poner en minutos para las pruebas sustituir funcion y el put extra (finalAlarma)
//            long finalAlarma = sumarMinutos(calendar.getTime(), dias);
                long finalAlarmaDias = sumarDias(calendarInicio.getTime(), dias);
                intent.putExtra("final", finalAlarmaDias);

//            Pasar idAlarma
                intent.putExtra("idAlarma", id_nuevo);

//            Pasar pushKey
                intent.putExtra("pushKey", pushKey);

//                Comprobrar inicio y final alarma
                System.out.println("-----------------------Inicio de alarma: " + calendarInicio.getTime() + " - Final alarma: " + finalAlarmaDias);


                PendingIntent pendingIntentRepeticion = PendingIntent.getBroadcast(this, id_nuevo, intent, 0);
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarInicio.getTimeInMillis(),
                        repeticionMs, pendingIntentRepeticion);

                sumarMinutos(calendarInicio.getTime(), dias);

                Date dateFinal = new Date(sumarDias(calendarInicio.getTime(), dias));
                System.out.println("**********************LA alarma empieza a: " + calendarInicio.getTime() + " y finaliza a: " + dateFinal + ". Cada " + repeticion + " horas.");

            }
        }
    }

    //    Calendario
    public long sumarDias(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, dias);
        return calendar.getTimeInMillis();
    }

    public Date sumarHoras(Date fecha, int horas) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.HOUR, horas);
        return calendar.getTime();
    }

    public long sumarMinutos(Date fecha, int minutos) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.MINUTE, minutos);
        return calendar.getTimeInMillis();
    }


    private void cancelAlarm(int idAlarma) {
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent alarmaPending = PendingIntent.getBroadcast(getBaseContext(), idAlarma, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//Cancela Alarma.
        alarmManager.cancel(alarmaPending);

        System.out.println("Alarma cancelada");
    }

    //    Funcion para crear el canal para las notificaciones
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "Notificacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}