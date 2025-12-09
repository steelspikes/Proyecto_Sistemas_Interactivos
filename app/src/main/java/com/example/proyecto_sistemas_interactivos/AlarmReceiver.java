package com.example.proyecto_sistemas_interactivos;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "recordatorio_channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Leer datos que mandó HomeActivity.programarAlarma(...)
        String titulo = intent.getStringExtra(AlertActivity.EXTRA_TITULO);
        long id       = intent.getLongExtra(AlertActivity.EXTRA_ID, -1);

        if (titulo == null || titulo.trim().isEmpty()) {
            titulo = "TU ACTIVIDAD";
        }

        // ✅ DEBUG: ver si el receiver realmente se ejecuta
        Toast.makeText(context, "Alarma: " + titulo, Toast.LENGTH_LONG).show();

        // Intent para la pantalla de alerta
        Intent fullScreenIntent = new Intent(context, AlertActivity.class);
        fullScreenIntent.putExtra(AlertActivity.EXTRA_TITULO, titulo);
        fullScreenIntent.putExtra(AlertActivity.EXTRA_ID, id);
        fullScreenIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // ---------- NOTIFICACIÓN (por si el sistema la usa como full-screen) ----------
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorios",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Alarmas de recordatorios");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // o tu ícono
                .setContentTitle("Recordatorio")
                .setContentText(titulo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setContentIntent(fullScreenPendingIntent)
                .setFullScreenIntent(fullScreenIntent != null ? fullScreenPendingIntent : null, true)
                .build();

        nm.notify((int) System.currentTimeMillis(), notification);

        // ---------- FORZAR APERTURA DE AlertActivity A PANTALLA COMPLETA ----------
        try {
            context.startActivity(fullScreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
            // Si por alguna razón el sistema lo bloquea,
            // al menos queda la notificación para abrir manualmente.
        }
    }
}
