package com.example.proyecto_sistemas_interactivos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private Button btnEne, btnFeb, btnMar, btnAbr, btnMay, btnJun,
            btnJul, btnAgo, btnSep, btnOct, btnNov, btnDic;

    private TextView txtTituloMes;
    private GridLayout gridDias;

    private int anioActual;
    private int mesSeleccionado; // 0 = Ene ... 11 = Dic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Botón casa -> vuelve al Home
        findViewById(R.id.btnHomeCalendar).setOnClickListener(v -> {
            Intent i = new Intent(CalendarActivity.this, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        txtTituloMes = findViewById(R.id.txtTituloMes);
        gridDias = findViewById(R.id.gridDias);

        btnEne = findViewById(R.id.btnEne);
        btnFeb = findViewById(R.id.btnFeb);
        btnMar = findViewById(R.id.btnMar);
        btnAbr = findViewById(R.id.btnAbr);
        btnMay = findViewById(R.id.btnMay);
        btnJun = findViewById(R.id.btnJun);
        btnJul = findViewById(R.id.btnJul);
        btnAgo = findViewById(R.id.btnAgo);
        btnSep = findViewById(R.id.btnSep);
        btnOct = findViewById(R.id.btnOct);
        btnNov = findViewById(R.id.btnNov);
        btnDic = findViewById(R.id.btnDic);

        Calendar hoy = Calendar.getInstance();
        anioActual = hoy.get(Calendar.YEAR);
        mesSeleccionado = hoy.get(Calendar.MONTH); // 0..11

        configurarListenersMeses();
        seleccionarMes(mesSeleccionado); // mes actual
    }

    private void configurarListenersMeses() {
        View.OnClickListener listener = v -> {
            if (v == btnEne) mesSeleccionado = 0;
            else if (v == btnFeb) mesSeleccionado = 1;
            else if (v == btnMar) mesSeleccionado = 2;
            else if (v == btnAbr) mesSeleccionado = 3;
            else if (v == btnMay) mesSeleccionado = 4;
            else if (v == btnJun) mesSeleccionado = 5;
            else if (v == btnJul) mesSeleccionado = 6;
            else if (v == btnAgo) mesSeleccionado = 7;
            else if (v == btnSep) mesSeleccionado = 8;
            else if (v == btnOct) mesSeleccionado = 9;
            else if (v == btnNov) mesSeleccionado = 10;
            else if (v == btnDic) mesSeleccionado = 11;

            seleccionarMes(mesSeleccionado);
        };

        btnEne.setOnClickListener(listener);
        btnFeb.setOnClickListener(listener);
        btnMar.setOnClickListener(listener);
        btnAbr.setOnClickListener(listener);
        btnMay.setOnClickListener(listener);
        btnJun.setOnClickListener(listener);
        btnJul.setOnClickListener(listener);
        btnAgo.setOnClickListener(listener);
        btnSep.setOnClickListener(listener);
        btnOct.setOnClickListener(listener);
        btnNov.setOnClickListener(listener);
        btnDic.setOnClickListener(listener);
    }

    private void seleccionarMes(int mes) {
        // actualizar color de los botones de mes
        resetearMeses();
        switch (mes) {
            case 0: btnEne.setBackgroundResource(R.drawable.circle_day_selected); break;
            case 1: btnFeb.setBackgroundResource(R.drawable.circle_day_selected); break;
            case 2: btnMar.setBackgroundResource(R.drawable.circle_day_selected); break;
            case 3: btnAbr.setBackgroundResource(R.drawable.circle_day_selected); break;
            case 4: btnMay.setBackgroundResource(R.drawable.circle_day_selected); break;
            case 5: btnJun.setBackgroundResource(R.drawable.circle_day_selected); break;
            case 6: btnJul.setBackgroundResource(R.drawable.circle_day_selected); break;
            case 7: btnAgo.setBackgroundResource(R.drawable.circle_day_selected); break;
            case 8: btnSep.setBackgroundResource(R.drawable.circle_day_selected); break;
            case 9: btnOct.setBackgroundResource(R.drawable.circle_day_selected); break;
            case 10: btnNov.setBackgroundResource(R.drawable.circle_day_selected); break;
            case 11: btnDic.setBackgroundResource(R.drawable.circle_day_selected); break;
        }

        // Título del mes
        String[] nombresMes = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        txtTituloMes.setText(String.format(Locale.getDefault(), "%s %d", nombresMes[mes], anioActual));

        // Dibujar los días
        dibujarCalendario();
    }

    private void resetearMeses() {
        btnEne.setBackgroundResource(R.drawable.circle_day);
        btnFeb.setBackgroundResource(R.drawable.circle_day);
        btnMar.setBackgroundResource(R.drawable.circle_day);
        btnAbr.setBackgroundResource(R.drawable.circle_day);
        btnMay.setBackgroundResource(R.drawable.circle_day);
        btnJun.setBackgroundResource(R.drawable.circle_day);
        btnJul.setBackgroundResource(R.drawable.circle_day);
        btnAgo.setBackgroundResource(R.drawable.circle_day);
        btnSep.setBackgroundResource(R.drawable.circle_day);
        btnOct.setBackgroundResource(R.drawable.circle_day);
        btnNov.setBackgroundResource(R.drawable.circle_day);
        btnDic.setBackgroundResource(R.drawable.circle_day);
    }

    /** Genera las celdas para el mes seleccionado y pinta en rosa los días con recordatorio */
    private void dibujarCalendario() {
        gridDias.removeAllViews();
        gridDias.setColumnCount(7);

        // Configuramos el calendario con el primer día del mes
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, anioActual);
        cal.set(Calendar.MONTH, mesSeleccionado);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int primerDiaSemana = cal.get(Calendar.DAY_OF_WEEK); // 1=domingo
        int offset = primerDiaSemana - Calendar.SUNDAY;       // 0..6
        int diasEnMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int totalCeldas = 42; // 6 filas x 7 columnas

        for (int i = 0; i < totalCeldas; i++) {
            TextView tv = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(4, 4, 4, 4);
            tv.setLayoutParams(params);
            tv.setGravity(android.view.Gravity.CENTER);
            tv.setPadding(0, 16, 0, 16);

            int diaNumero = i - offset + 1;
            if (diaNumero >= 1 && diaNumero <= diasEnMes) {
                tv.setText(String.valueOf(diaNumero));

                boolean tieneRec = HomeActivity.tieneRecordatorio(year, month, day);


                if (tieneRec) {
                    tv.setBackgroundResource(R.drawable.cell_day_active);
                } else {
                    tv.setBackgroundResource(R.drawable.cell_day_inactive);
                }
            } else {
                // Celdas vacías
                tv.setText("");
                tv.setBackgroundResource(R.drawable.cell_day_inactive);
                tv.setAlpha(0.0f); // prácticamente invisibles
            }

            gridDias.addView(tv);
        }
    }
}
