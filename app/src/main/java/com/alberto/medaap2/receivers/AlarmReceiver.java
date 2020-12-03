package com.alberto.medaap2.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alberto.medaap2.InicialActivity;
import com.alberto.medaap2.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationCompat.Builder builder;
    private static final String CHANNEL_ID = "NOTIFICACION";
    private static int ID_UNICA = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        String data = extras.getString("nombreMed");
        String nombreUsuario = extras.getString("usuario");
        String tituloNotificacionNormal = "¡" + nombreUsuario + "!";
        String textoNotificacionNormal = context.getString(R.string.teTocaUnaToma) + " " + data;
        String tituloFinalMedicacion = context.getString(R.string.tratamientoFinalizado);
        String textoFinalTratamiento = context.getString(R.string.hasCompletadoElTratamiento) + " " + data;
        String pushKey = extras.getString("pushKey");

//        Recibir extra date
        Date dateInicio = new Date();
        dateInicio.setTime(intent.getLongExtra("inicio", -1));
        System.out.println("HORA DE INICIO**************************" + dateInicio);

        Date finalAlarma = new Date();
        finalAlarma.setTime(intent.getLongExtra("final", -1));
        System.out.println("HORA DE FIN DE ALARMA*******************" + finalAlarma);

//        Recoger id alarma
        int idAlarma = extras.getInt("idAlarma", -1);


//        Capturar hora de llegada de alarma
        Date dateAlarma = new Date();
        dateAlarma.getTime();
        System.out.println("HORA DE LLEGADA ALARMA*****************************" + dateAlarma);
        System.out.println("ID ALARMA*****************************" + idAlarma);

//        Comprobar fechas y cancelar alarma cuando corresponda
        if (dateAlarma.after(finalAlarma)) {
            createNotification(context, data, nombreUsuario, tituloFinalMedicacion, textoFinalTratamiento);
            PendingIntent sender = PendingIntent.getBroadcast(context, idAlarma, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
            estadoAlarma(pushKey, context, idAlarma);
        }
        if (dateAlarma.before(finalAlarma) || dateAlarma.equals(finalAlarma)) {
            createNotification(context, data, nombreUsuario, tituloNotificacionNormal, textoNotificacionNormal);
        }
    }

    private void createNotification(Context context, String data, String nombreUsuario, String titulo, String texto) {

        Random random = new Random();
        int n = random.nextInt(1000) + 1;

        builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle(titulo);
        builder.setContentText(texto);
        builder.setSmallIcon(R.drawable.pill_38165);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

//        Intent para que al apretar la notificacion nos lleve a una activity
        Intent intent = new Intent(context, InicialActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

//        Creacion de la notificacion
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(n, builder.build());
    }

    private void estadoAlarma(String pusKey, Context context, int idAlarma) {
        System.out.println("PUSH KEY DESDE ALARM RECEIVER ES:****************************" + pusKey);
        FirebaseApp.initializeApp(context);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("Medicamentos");
        reference.child(pusKey).child("idAlarma").setValue(0);
    }

}
