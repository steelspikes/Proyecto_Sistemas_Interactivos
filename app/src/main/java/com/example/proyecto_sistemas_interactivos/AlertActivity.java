package com.example.proyecto_sistemas_interactivos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AlertActivity extends AppCompatActivity {

    private long idRecordatorio;
    private String tituloRecordatorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        TextView txtTitulo = findViewById(R.id.txtTituloAlarma);
        Button btnDetener = findViewById(R.id.btnDetener);
        Button btnMas5 = findViewById(R.id.btnMas5);

        tituloRecordatorio = getIntent().getStringExtra("TITULO_RECORDATORIO");
        idRecordatorio = getIntent().getLongExtra("ID_RECORDATORIO", -1);

        if (tituloRecordatorio == null || tituloRecordatorio.trim().isEmpty()) {
            tituloRecordatorio = "ALARMA";
        }

        String tituloPantalla = "HORA DE " + tituloRecordatorio.toUpperCase(Locale.getDefault());
        txtTitulo.setText(tituloPantalla);

        btnDetener.setOnClickListener(v -> {
            if (idRecordatorio != -1) {
                HomeActivity.marcarRecordatorioCompletado(AlertActivity.this, idRecordatorio);
            }
            finish();
        });

        btnMas5.setOnClickListener(v -> {
            if (idRecordatorio != -1) {
                HomeActivity.posponer5Minutos(AlertActivity.this, idRecordatorio);
            }
            finish();
        });
    }
}
