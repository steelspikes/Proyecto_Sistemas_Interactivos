package com.example.proyecto_sistemas_interactivos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    // --- UI principal ---
    private Button btnAgregarRecordatorio;
    private LinearLayout contenedorRecordatorios;
    private TextView txtSinRecordatorios;

    // Días de la semana
    private TextView txtDiaDomingo, txtDiaLunes, txtDiaMartes,
            txtDiaMiercoles, txtDiaJueves, txtDiaViernes, txtDiaSabado;

    // Lista de recordatorios persistente
    static final List<Recordatorio> LISTA_RECORDATORIOS = new ArrayList<>();

    // Preferencias para guardar recordatorios
    private static final String PREFS_RECORDATORIOS = "prefs_recordatorios";
    private static final String KEY_LISTA_RECORDATORIOS = "lista_recordatorios";

    // Para el diálogo de nuevo recordatorio
    private final Calendar fechaHoraSeleccionada = Calendar.getInstance();
    private final SimpleDateFormat formatoFecha =
            new SimpleDateFormat("EEE, d 'de' MMM", Locale.getDefault());
    private final SimpleDateFormat formatoHora =
            new SimpleDateFormat("h:mm a", Locale.getDefault());

    // Handler para revisar cada cierto tiempo si toca mostrar alarma
    private Handler handlerAlarmas = new Handler();
    private Runnable runnableAlarmas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- referencias UI ---
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

        // botón calendario (barra gris)
        TextView btnIrCalendario = findViewById(R.id.btnIrCalendario);
        btnIrCalendario.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, CalendarActivity.class);
            startActivity(i);
        });

        // Cargar recordatorios guardados
        cargarRecordatoriosDesdePrefs();

        // Marcar el día actual en rosa
        marcarDiaActual();

        btnAgregarRecordatorio.setOnClickListener(v -> mostrarDialogoNuevoRecordatorio());

        // Pinta la lista para hoy
        actualizarListaRecordatorios();

        // Empieza a revisar si toca mostrar alguna alarma
        iniciarChequeoAlarmas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (runnableAlarmas != null) {
            handlerAlarmas.removeCallbacks(runnableAlarmas);
        }
    }

    // ---------------------------------------------------------------------
    //  DÍAS DE LA SEMANA
    // ---------------------------------------------------------------------
    private void marcarDiaActual() {
        TextView[] dias = new TextView[]{
                txtDiaDomingo, txtDiaLunes, txtDiaMartes, txtDiaMiercoles,
                txtDiaJueves, txtDiaViernes, txtDiaSabado
        };

        // Todos verdes normales
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

        EditText edtTitulo = view.findViewById(R.id.edtTituloRecordatorio);
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

                guardarRecordatoriosEnPrefs();
                actualizarListaRecordatorios();

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    // ---------------------------------------------------------------------
    //  LISTA DINÁMICA DE RECORDATORIOS (SOLO HOY)
    // ---------------------------------------------------------------------
    private void actualizarListaRecordatorios() {
        contenedorRecordatorios.removeAllViews();

        long hoyMillis = System.currentTimeMillis();

        // Ordenar por fecha/hora
        Collections.sort(LISTA_RECORDATORIOS,
                (a, b) -> Long.compare(a.fechaHoraMillis, b.fechaHoraMillis));

        boolean hayRecordatoriosHoy = false;

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Recordatorio r : LISTA_RECORDATORIOS) {
            if (esMismoDia(hoyMillis, r.fechaHoraMillis)) {
                hayRecordatoriosHoy = true;

                View card = inflater.inflate(R.layout.item_recordatorio, contenedorRecordatorios, false);

                TextView txtTitulo = card.findViewById(R.id.txtTituloRecordatorio);
                TextView txtHora = card.findViewById(R.id.txtHoraRecordatorio);

                txtTitulo.setText(r.titulo);
                txtHora.setText(formatoHora.format(r.getDate()));

                contenedorRecordatorios.addView(card);
            }
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
    //  PERSISTENCIA CON SharedPreferences
    // ---------------------------------------------------------------------
    private void guardarRecordatoriosEnPrefs() {
        SharedPreferences prefs = getSharedPreferences(PREFS_RECORDATORIOS, MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        // Formato sencillo: titulo|millis|mostrado;… (separados por salto de línea)
        for (Recordatorio r : LISTA_RECORDATORIOS) {
            sb.append(r.titulo.replace("\n", " "))
                    .append("|")
                    .append(r.fechaHoraMillis)
                    .append("|")
                    .append(r.mostrado ? "1" : "0")
                    .append("\n");
        }
        prefs.edit().putString(KEY_LISTA_RECORDATORIOS, sb.toString()).apply();
    }

    private void cargarRecordatoriosDesdePrefs() {
        LISTA_RECORDATORIOS.clear();
        SharedPreferences prefs = getSharedPreferences(PREFS_RECORDATORIOS, MODE_PRIVATE);
        String data = prefs.getString(KEY_LISTA_RECORDATORIOS, null);
        if (data == null || data.isEmpty()) return;

        String[] lineas = data.split("\n");
        for (String linea : lineas) {
            if (linea.trim().isEmpty()) continue;
            String[] partes = linea.split("\\|");
            if (partes.length >= 3) {
                String titulo = partes[0];
                long millis = Long.parseLong(partes[1]);
                boolean mostrado = partes[2].equals("1");
                Recordatorio r = new Recordatorio(titulo, millis);
                r.mostrado = mostrado;
                LISTA_RECORDATORIOS.add(r);
            }
        }
    }

    // ---------------------------------------------------------------------
    //  MÉTODO PARA EL CALENDARIO (pinta días en rosa)
    // ---------------------------------------------------------------------
    public static boolean tieneRecordatorio(int year, int monthZeroBased, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        for (Recordatorio r : LISTA_RECORDATORIOS) {
            c.setTimeInMillis(r.fechaHoraMillis);
            if (c.get(Calendar.YEAR) == year &&
                    c.get(Calendar.MONTH) == monthZeroBased &&
                    c.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------------
    //  CHEQUEO PERIÓDICO DE ALARMAS
    // ---------------------------------------------------------------------
    private void iniciarChequeoAlarmas() {
        runnableAlarmas = new Runnable() {
            @Override
            public void run() {
                long ahora = System.currentTimeMillis();

                for (Recordatorio r : LISTA_RECORDATORIOS) {
                    // Si ya pasó o es exactamente la hora, y aún no se mostró la pantalla
                    if (!r.mostrado && r.fechaHoraMillis <= ahora) {
                        r.mostrado = true;
                        guardarRecordatoriosEnPrefs();

                        // Abrimos pantalla de alerta a pantalla completa
                        Intent i = new Intent(HomeActivity.this, AlertActivity.class);
                        i.putExtra("titulo", r.titulo);
                        startActivity(i);

                        break; // mostramos uno por vez
                    }
                }

                // Volver a revisar en 30 segundos
                handlerAlarmas.postDelayed(this, 30_000);
            }
        };

        handlerAlarmas.post(runnableAlarmas);
    }

    // ---------------------------------------------------------------------
    //  MODELO DE DATOS
    // ---------------------------------------------------------------------
    static class Recordatorio {
        String titulo;
        long fechaHoraMillis;
        boolean mostrado = false;   // para no mostrar la alarma más de una vez

        Recordatorio(String titulo, long fechaHoraMillis) {
            this.titulo = titulo;
            this.fechaHoraMillis = fechaHoraMillis;
        }

        java.util.Date getDate() {
            return new java.util.Date(fechaHoraMillis);
        }
    }
}
