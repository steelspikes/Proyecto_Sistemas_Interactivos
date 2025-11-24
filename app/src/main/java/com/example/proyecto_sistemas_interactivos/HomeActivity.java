package com.example.proyecto_sistemas_interactivos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    // --- Claves para SharedPreferences ---
    private static final String PREFS_NAME = "prefs_app";
    private static final String KEY_RECORDATORIOS = "recordatorios";

    // --- Lista estática que usan HomeActivity y CalendarActivity ---
    static final List<Recordatorio> LISTA_RECORDATORIOS = new ArrayList<>();

    // --- UI principal ---
    private Button btnAgregarRecordatorio;
    private LinearLayout contenedorRecordatorios;
    private TextView txtSinRecordatorios;

    // Días de la semana
    private TextView txtDiaDomingo, txtDiaLunes, txtDiaMartes,
            txtDiaMiercoles, txtDiaJueves, txtDiaViernes, txtDiaSabado;

    // Para el diálogo de nuevo recordatorio
    private final Calendar fechaHoraSeleccionada = Calendar.getInstance();
    private final SimpleDateFormat formatoFecha =
            new SimpleDateFormat("EEE, d 'de' MMM", Locale.getDefault());
    private final SimpleDateFormat formatoHora =
            new SimpleDateFormat("h:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ir al calendario desde la barra gris
        TextView btnIrCalendario = findViewById(R.id.btnIrCalendario);
        btnIrCalendario.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, CalendarActivity.class);
            startActivity(i);
        });

        // Referencias UI
        btnAgregarRecordatorio = findViewById(R.id.btnAgregarRecordatorio);
        contenedorRecordatorios = findViewById(R.id.contenedorRecordatorios);
        txtSinRecordatorios = findViewById(R.id.txtSinRecordatorios);

        txtDiaDomingo = findViewById(R.id.txtDiaDomingo);
        txtDiaLunes = findViewById(R.id.txtDiaLunes);
        txtDiaMartes = findViewById(R.id.txtDiaMartes);
        txtDiaMiercoles = findViewById(R.id.txtDiaMiercoles);
        txtDiaJueves = findViewById(R.id.txtDiaJueves);
        txtDiaViernes = findViewById(R.id.txtDiaViernes);
        txtDiaSabado = findViewById(R.id.txtDiaSabado);

        // Cargar recordatorios guardados
        cargarRecordatoriosDesdePrefs();

        // Marcar el día actual en rosa
        marcarDiaActual();

        // Botón para crear nuevo recordatorio
        btnAgregarRecordatorio.setOnClickListener(v -> mostrarDialogoNuevoRecordatorio());

        // Mostrar recordatorios de hoy
        actualizarListaRecordatorios();
    }

    // ---------------------------------------------------------------------
    //  DÍAS DE LA SEMANA
    // ---------------------------------------------------------------------
    private void marcarDiaActual() {
        TextView[] dias = new TextView[]{
                txtDiaDomingo, txtDiaLunes, txtDiaMartes, txtDiaMiercoles,
                txtDiaJueves, txtDiaViernes, txtDiaSabado
        };

        // Reinicia todos a verde normal
        for (TextView tv : dias) {
            tv.setBackgroundResource(R.drawable.circle_day);   // verde
        }

        Calendar hoy = Calendar.getInstance();
        int diaSemana = hoy.get(Calendar.DAY_OF_WEEK); // Domingo = 1 ... Sábado = 7
        int index = diaSemana - 1;
        if (index >= 0 && index < dias.length) {
            // Círculo rosa para el día actual
            dias[index].setBackgroundResource(R.drawable.circle_day_hoy);
        }
    }

    // ---------------------------------------------------------------------
    //  DIÁLOGO "NUEVO RECORDATORIO"
    // ---------------------------------------------------------------------
    private void mostrarDialogoNuevoRecordatorio() {
        // Fecha/hora inicial: ahora
        fechaHoraSeleccionada.setTimeInMillis(System.currentTimeMillis());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuevo recordatorio");

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_nuevo_recordatorio, null);
        builder.setView(view);

        TextView edtTitulo = view.findViewById(R.id.edtTituloRecordatorio);
        TextView txtFechaSel = view.findViewById(R.id.txtFechaSeleccionada);
        TextView txtHoraSel = view.findViewById(R.id.txtHoraSeleccionada);
        Button btnElegirFecha = view.findViewById(R.id.btnElegirFecha);
        Button btnElegirHora = view.findViewById(R.id.btnElegirHora);

        // Muestra fecha/hora inicial
        txtFechaSel.setText(formatoFecha.format(fechaHoraSeleccionada.getTime()));
        txtHoraSel.setText(formatoHora.format(fechaHoraSeleccionada.getTime()));

        btnElegirFecha.setOnClickListener(v -> {
            int anio = fechaHoraSeleccionada.get(Calendar.YEAR);
            int mes = fechaHoraSeleccionada.get(Calendar.MONTH);
            int dia = fechaHoraSeleccionada.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialogFecha = new DatePickerDialog(
                    HomeActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        fechaHoraSeleccionada.set(Calendar.YEAR, year);
                        fechaHoraSeleccionada.set(Calendar.MONTH, month);
                        fechaHoraSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        txtFechaSel.setText(formatoFecha.format(fechaHoraSeleccionada.getTime()));
                    },
                    anio, mes, dia
            );
            dialogFecha.show();
        });

        btnElegirHora.setOnClickListener(v -> {
            int hora = fechaHoraSeleccionada.get(Calendar.HOUR_OF_DAY);
            int minuto = fechaHoraSeleccionada.get(Calendar.MINUTE);

            TimePickerDialog dialogHora = new TimePickerDialog(
                    HomeActivity.this,
                    (view12, hourOfDay, minute) -> {
                        fechaHoraSeleccionada.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        fechaHoraSeleccionada.set(Calendar.MINUTE, minute);
                        fechaHoraSeleccionada.set(Calendar.SECOND, 0);
                        txtHoraSel.setText(formatoHora.format(fechaHoraSeleccionada.getTime()));
                    },
                    hora, minuto, false
            );
            dialogHora.show();
        });

        builder.setNegativeButton("Cancelar", (d, which) -> d.dismiss());
        builder.setPositiveButton("Guardar", null); // lo sobrescribimos para validar

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            Button btnPositivo = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositivo.setOnClickListener(v -> {
                String titulo = edtTitulo.getText().toString().trim();
                if (titulo.isEmpty()) {
                    edtTitulo.setError("Escribe el nombre de la actividad");
                    return;
                }

                // Crea y guarda el recordatorio
                Recordatorio r = new Recordatorio(titulo, fechaHoraSeleccionada.getTimeInMillis());
                LISTA_RECORDATORIOS.add(r);

                // Guardar en SharedPreferences
                guardarRecordatoriosEnPrefs();

                // Actualizar la lista en pantalla (ya ordenada)
                actualizarListaRecordatorios();

                // Ya NO mostramos el Toast aquí (se quitó el mensaje de abajo)

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    // ---------------------------------------------------------------------
    //  LISTA DINÁMICA DE RECORDATORIOS (SOLO HOY, ORDENADOS POR HORA)
    // ---------------------------------------------------------------------
    private void actualizarListaRecordatorios() {
        contenedorRecordatorios.removeAllViews();

        long hoy = System.currentTimeMillis();

        // Primero filtramos sólo los recordatorios de hoy
        List<Recordatorio> deHoy = new ArrayList<>();
        for (Recordatorio r : LISTA_RECORDATORIOS) {
            if (esMismoDia(hoy, r.fechaHoraMillis)) {
                deHoy.add(r);
            }
        }

        // Ordenamos por hora (de más temprano a más tarde)
        Collections.sort(deHoy, new Comparator<Recordatorio>() {
            @Override
            public int compare(Recordatorio r1, Recordatorio r2) {
                return Long.compare(r1.fechaHoraMillis, r2.fechaHoraMillis);
            }
        });

        boolean hayRecordatoriosHoy = !deHoy.isEmpty();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Recordatorio r : deHoy) {
            View card = inflater.inflate(R.layout.item_recordatorio, contenedorRecordatorios, false);

            TextView txtTitulo = card.findViewById(R.id.txtTituloRecordatorio);
            TextView txtHora = card.findViewById(R.id.txtHoraRecordatorio);

            txtTitulo.setText(r.titulo);
            txtHora.setText(formatoHora.format(r.getDate()));

            contenedorRecordatorios.addView(card);
        }

        if (hayRecordatoriosHoy) {
            txtSinRecordatorios.setVisibility(View.GONE);
        } else {
            txtSinRecordatorios.setVisibility(View.VISIBLE);
        }
    }

    private boolean esMismoDia(long t1, long t2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(t1);

        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(t2);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    // ---------------------------------------------------------------------
    //  PERSISTENCIA EN SharedPreferences
    // ---------------------------------------------------------------------
    private void cargarRecordatoriosDesdePrefs() {
        LISTA_RECORDATORIOS.clear();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_RECORDATORIOS, null);
        if (json == null || json.isEmpty()) return;

        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String titulo = obj.getString("titulo");
                long fecha = obj.getLong("fecha");
                LISTA_RECORDATORIOS.add(new Recordatorio(titulo, fecha));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void guardarRecordatoriosEnPrefs() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        JSONArray arr = new JSONArray();

        for (Recordatorio r : LISTA_RECORDATORIOS) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("titulo", r.titulo);
                obj.put("fecha", r.fechaHoraMillis);
                arr.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        prefs.edit().putString(KEY_RECORDATORIOS, arr.toString()).apply();
    }

    // ---------------------------------------------------------------------
    //  MÉTODOS ESTÁTICOS PARA CalendarActivity
    // ---------------------------------------------------------------------
    public static boolean tieneRecordatorio(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        for (Recordatorio r : LISTA_RECORDATORIOS) {
            c.setTimeInMillis(r.fechaHoraMillis);
            if (c.get(Calendar.YEAR) == year &&
                    c.get(Calendar.MONTH) == month &&
                    c.get(Calendar.DAY_OF_MONTH) == day) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------------
    //  MODELO DE DATOS
    // ---------------------------------------------------------------------
    static class Recordatorio {
        String titulo;
        long fechaHoraMillis;

        Recordatorio(String titulo, long fechaHoraMillis) {
            this.titulo = titulo;
            this.fechaHoraMillis = fechaHoraMillis;
        }

        java.util.Date getDate() {
            return new java.util.Date(fechaHoraMillis);
        }
    }
}
