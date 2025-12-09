package com.example.proyecto_sistemas_interactivos;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AlertActivity extends AppCompatActivity {

    private TextView txtTituloAlarma;
    private Button btnDetener, btnMas5;
    private ImageView imgAlarma;

    private String tituloRecordatorio = "TU MEDICINA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        txtTituloAlarma = findViewById(R.id.txtTituloAlarma);
        btnDetener = findViewById(R.id.btnDetener);
        btnMas5 = findViewById(R.id.btnMas5);
        imgAlarma = findViewById(R.id.imgAlarma);

        // --- Título dinámico: viene del recordatorio ---
        Intent intent = getIntent();
        String t1 = intent.getStringExtra("titulo");
        String t2 = intent.getStringExtra("EXTRA_TITULO"); // por si en otro lado lo llamaste así

        if (t1 != null && !t1.isEmpty()) {
            tituloRecordatorio = t1;
        } else if (t2 != null && !t2.isEmpty()) {
            tituloRecordatorio = t2;
        }

        String tituloPantalla = "HORA DE " + tituloRecordatorio.toUpperCase(Locale.getDefault());
        txtTituloAlarma.setText(tituloPantalla);

        // --- Botón DETENER: solo cierra la pantalla ---
        btnDetener.setOnClickListener(v -> finish());

        // --- Botón +5 MINUTOS: reprograma la alarma 5 min después ---
        btnMas5.setOnClickListener(v -> {
            reprogramarAlarma5Min();
            finish();
        });
    }

    /**
     * Vuelve a programar esta misma alarma para dentro de 5 minutos.
     * IMPORTANTE: usa el mismo PendingIntent (requestCode = 0)
     * para que se reemplace la alarma anterior.
     */
    private void reprogramarAlarma5Min() {
        long cincoMin = 5 * 60 * 1000L;
        long tiempoDisparo = System.currentTimeMillis() + cincoMin;

        Intent i = new Intent(this, AlertActivity.class);
        i.putExtra("titulo", tituloRecordatorio);

        PendingIntent pi = PendingIntent.getActivity(
                this,
                0, // mismo requestCode que usaste al programar la alarma original
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (am != null) {
            am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    tiempoDisparo,
                    pi
            );
        }
    }
}
