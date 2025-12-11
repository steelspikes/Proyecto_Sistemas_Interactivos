package com.example.proyecto_sistemas_interactivos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import android.os.Build;
import android.view.WindowManager;

public class AlertActivity extends AppCompatActivity {

    // Constantes de los extras que usaremos en TODA la app
    public static final String EXTRA_TITULO = "TITULO_RECORDATORIO";
    public static final String EXTRA_DESCRIPCION = "DESCRIPCION_RECORDATORIO";
    public static final String EXTRA_EMOJI = "EMOJI_RECORDATORIO";
    public static final String EXTRA_ID     = "ID_RECORDATORIO";

    private long idRecordatorio;
    private String tituloRecordatorio;
    private String descripcionRecordatorio;
    private String emojiRecordatorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        setFinishOnTouchOutside(false);


        // Para que se muestre encima de la pantalla de bloqueo y encienda la pantalla
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            );
        }


        TextView txtEmoji = findViewById(R.id.txtEmojiAlarma);
        TextView txtTitulo = findViewById(R.id.txtTituloAlarma);
        TextView txtDescripcion = findViewById(R.id.txtDescripcionAlarma);
        Button btnDetener  = findViewById(R.id.btnDetener);
        Button btnMas5     = findViewById(R.id.btnMas5);

        // Leer datos que mandó el AlarmReceiver
        tituloRecordatorio = getIntent().getStringExtra(EXTRA_TITULO);
        descripcionRecordatorio = getIntent().getStringExtra(EXTRA_DESCRIPCION);
        emojiRecordatorio = getIntent().getStringExtra(EXTRA_EMOJI);
        idRecordatorio     = getIntent().getLongExtra(EXTRA_ID, -1);

        if (tituloRecordatorio == null || tituloRecordatorio.trim().isEmpty()) {
            tituloRecordatorio = "ALARMA";
        }

        if (emojiRecordatorio != null && !emojiRecordatorio.isEmpty()) {
            txtEmoji.setText(emojiRecordatorio);
            txtEmoji.setVisibility(View.VISIBLE);
        } else {
            txtEmoji.setVisibility(View.GONE);
        }

        String tituloPantalla =
                "HORA DE " + tituloRecordatorio.toUpperCase(Locale.getDefault());
        txtTitulo.setText(tituloPantalla);

        if (descripcionRecordatorio != null && !descripcionRecordatorio.isEmpty()) {
            txtDescripcion.setText(descripcionRecordatorio);
            txtDescripcion.setVisibility(View.VISIBLE);
        } else {
            txtDescripcion.setVisibility(View.GONE);
        }

        // Botón DETENER -> marcar completado y cerrar
        btnDetener.setOnClickListener(v -> {
            if (idRecordatorio != -1) {
                HomeActivity.marcarRecordatorioCompletado(
                        AlertActivity.this, idRecordatorio);
            }
            finish();
        });

        // Botón +5 MINUTOS -> posponer y cerrar
        btnMas5.setOnClickListener(v -> {
            if (idRecordatorio != -1) {
                HomeActivity.posponer5Minutos(
                        AlertActivity.this, idRecordatorio);
            }
            finish();
        });
    }
}
