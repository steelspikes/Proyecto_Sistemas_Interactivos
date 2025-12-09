package com.example.proyecto_sistemas_interactivos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import android.os.Build;


public class HomeActivity extends AppCompatActivity {

    // UI principal
    private Button btnAgregarRecordatorio;
    private LinearLayout contenedorRecordatorios;
    private TextView txtSinRecordatorios;
    private TextView btnIrCalendario;

    // Días de la semana
    private TextView txtDiaDomingo, txtDiaLunes, txtDiaMartes,
            txtDiaMiercoles, txtDiaJueves, txtDiaViernes, txtDiaSabado;

    // Fechas reales de la semana actual (D, L, M, M, J, V, S)
    private final Calendar[] diasSemana = new Calendar[7];
    private int indiceDiaSeleccionado = 0; // 0 = Domingo

    // Lista en memoria (siempre sincronizada con SharedPreferences)
    private List<Recordatorio> listaRecordatorios = new ArrayList<>();

    // Para el diálogo de nuevo recordatorio
    private final Calendar fechaHoraSeleccionada = Calendar.getInstance();
    private final SimpleDateFormat formatoFecha =
            new SimpleDateFormat("EEE, d 'de' MMM", Locale.getDefault());
    private final SimpleDateFormat formatoHora =
            new SimpleDateFormat("h:mm a", Locale.getDefault());

    // SharedPreferences
    private static final String PREFS_NAME = "recordatorios_prefs";
    private static final String KEY_RECORDATORIOS = "lista_recordatorios_json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Referencias UI
        btnAgregarRecordatorio = findViewById(R.id.btnAgregarRecordatorio);
        contenedorRecordatorios = findViewById(R.id.contenedorRecordatorios);
        txtSinRecordatorios = findViewById(R.id.txtSinRecordatorios);
        btnIrCalendario = findViewById(R.id.btnIrCalendario);

        txtDiaDomingo = findViewById(R.id.txtDiaDomingo);
        txtDiaLunes = findViewById(R.id.txtDiaLunes);
        txtDiaMartes = findViewById(R.id.txtDiaMartes);
        txtDiaMiercoles = findViewById(R.id.txtDiaMiercoles);
        txtDiaJueves = findViewById(R.id.txtDiaJueves);
        txtDiaViernes = findViewById(R.id.txtDiaViernes);
        txtDiaSabado = findViewById(R.id.txtDiaSabado);

        // Semana actual y día seleccionado
        inicializarSemana();

        // Click en botón "Agregar recordatorio"
        btnAgregarRecordatorio.setOnClickListener(v -> mostrarDialogoNuevoRecordatorio());

        // Click en icono de calendario (arriba a la derecha)
        btnIrCalendario.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        // Click en cada círculo de día
        configurarClicksDiasSemana();

        // Cargar recordatorios desde memoria y prefs
        listaRecordatorios = cargarListaDesdePrefs(this);

        // Reprogramar alarmas pendientes (por si abriste la app después)
        reprogramarTodasLasAlarmas(this);

        // Mostrar lista de hoy (día seleccionado)
        actualizarListaRecordatorios();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Por si algo cambió (por ejemplo desde AlertActivity)
        listaRecordatorios = cargarListaDesdePrefs(this);
        actualizarListaRecordatorios();
    }

    // -------------------------------------------------------------
    //   SEMANA ACTUAL Y DÍAS
    // -------------------------------------------------------------
    private void inicializarSemana() {
        Calendar hoy = Calendar.getInstance();
        int dow = hoy.get(Calendar.DAY_OF_WEEK); // 1=Domingo..7=Sábado

        // Obtener el domingo de esta semana
        Calendar domingo = (Calendar) hoy.clone();
        int diff = dow - Calendar.SUNDAY; // cuántos días me tengo que mover hacia atrás
        domingo.add(Calendar.DAY_OF_MONTH, -diff);

        for (int i = 0; i < 7; i++) {
            Calendar c = (Calendar) domingo.clone();
            c.add(Calendar.DAY_OF_MONTH, i);
            diasSemana[i] = c;
        }

        indiceDiaSeleccionado = dow - 1; // 0..6
        marcarDiaSeleccionado();
    }

    private void configurarClicksDiasSemana() {
        txtDiaDomingo.setOnClickListener(v -> {
            indiceDiaSeleccionado = 0;
            marcarDiaSeleccionado();
            actualizarListaRecordatorios();
        });
        txtDiaLunes.setOnClickListener(v -> {
            indiceDiaSeleccionado = 1;
            marcarDiaSeleccionado();
            actualizarListaRecordatorios();
        });
        txtDiaMartes.setOnClickListener(v -> {
            indiceDiaSeleccionado = 2;
            marcarDiaSeleccionado();
            actualizarListaRecordatorios();
        });
        txtDiaMiercoles.setOnClickListener(v -> {
            indiceDiaSeleccionado = 3;
            marcarDiaSeleccionado();
            actualizarListaRecordatorios();
        });
        txtDiaJueves.setOnClickListener(v -> {
            indiceDiaSeleccionado = 4;
            marcarDiaSeleccionado();
            actualizarListaRecordatorios();
        });
        txtDiaViernes.setOnClickListener(v -> {
            indiceDiaSeleccionado = 5;
            marcarDiaSeleccionado();
            actualizarListaRecordatorios();
        });
        txtDiaSabado.setOnClickListener(v -> {
            indiceDiaSeleccionado = 6;
            marcarDiaSeleccionado();
            actualizarListaRecordatorios();
        });
    }

    private void marcarDiaSeleccionado() {
        TextView[] dias = new TextView[]{
                txtDiaDomingo, txtDiaLunes, txtDiaMartes, txtDiaMiercoles,
                txtDiaJueves, txtDiaViernes, txtDiaSabado
        };

        // Todos verdes
        for (TextView tv : dias) {
            tv.setBackgroundResource(R.drawable.circle_day);
        }

        // El seleccionado en rosa
        if (indiceDiaSeleccionado >= 0 && indiceDiaSeleccionado < dias.length) {
            dias[indiceDiaSeleccionado].setBackgroundResource(R.drawable.circle_day_hoy);
        }
    }

    // -------------------------------------------------------------
    //   DIÁLOGO "NUEVO RECORDATORIO"
    // -------------------------------------------------------------
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
        builder.setPositiveButton("Guardar", null); // sobrescribimos para validar

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            Button btnPositivo = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositivo.setOnClickListener(v -> {
                String titulo = edtTitulo.getText().toString().trim();
                if (titulo.isEmpty()) {
                    edtTitulo.setError("Escribe el nombre de la actividad");
                    return;
                }

                // Creamos recordatorio
                Recordatorio r = new Recordatorio();
                r.id = System.currentTimeMillis();
                r.titulo = titulo;
                r.fechaHoraMillis = fechaHoraSeleccionada.getTimeInMillis();
                r.completado = false;

                listaRecordatorios.add(r);
                // Ordenar por hora
                Collections.sort(listaRecordatorios, Comparator.comparingLong(o -> o.fechaHoraMillis));

                // Guardar
                guardarListaEnPrefs(HomeActivity.this, listaRecordatorios);

                // Programar alarma para pantalla completa
                programarAlarma(HomeActivity.this, r);

                // Actualizar UI (por si es para el día seleccionado)
                actualizarListaRecordatorios();

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    // -------------------------------------------------------------
    //   LISTA DINÁMICA (DÍA SELECCIONADO)
    // -------------------------------------------------------------
    private void actualizarListaRecordatorios() {
        contenedorRecordatorios.removeAllViews();

        // Siempre leo desde prefs por si cambió algo en segundo plano
        listaRecordatorios = cargarListaDesdePrefs(this);

        // Ordenados por fecha/hora
        Collections.sort(listaRecordatorios, Comparator.comparingLong(o -> o.fechaHoraMillis));

        Calendar fechaFiltro = diasSemana[indiceDiaSeleccionado];
        boolean hay = false;

        LayoutInflater inflater = LayoutInflater.from(this);
        Calendar aux = Calendar.getInstance();

        for (Recordatorio r : listaRecordatorios) {
            aux.setTimeInMillis(r.fechaHoraMillis);

            if (esMismoDia(aux, fechaFiltro)) {
                hay = true;

                View card = inflater.inflate(R.layout.item_recordatorio, contenedorRecordatorios, false);

                TextView txtTitulo = card.findViewById(R.id.txtTituloRecordatorio);
                TextView txtHora = card.findViewById(R.id.txtHoraRecordatorio);

                txtTitulo.setText(r.titulo);
                txtHora.setText(formatoHora.format(r.getDate()));

                // Si está completado, se ve "gris" (más tenue)
                if (r.completado) {
                    card.setAlpha(0.4f);
                } else {
                    card.setAlpha(1.0f);
                }

                contenedorRecordatorios.addView(card);
            }
        }

        txtSinRecordatorios.setVisibility(hay ? View.GONE : View.VISIBLE);
    }

    private boolean esMismoDia(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    // -------------------------------------------------------------
    //   PERSISTENCIA con SharedPreferences + Gson
    // -------------------------------------------------------------
    private static List<Recordatorio> cargarListaDesdePrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_RECORDATORIOS, null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<Recordatorio>>() {}.getType();
        List<Recordatorio> lista = new Gson().fromJson(json, type);
        return (lista != null) ? lista : new ArrayList<>();
    }

    private static void guardarListaEnPrefs(Context context, List<Recordatorio> lista) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = new Gson().toJson(lista);
        prefs.edit().putString(KEY_RECORDATORIOS, json).apply();
    }

    // -------------------------------------------------------------
    //   MÉTODOS USADOS POR OTRAS ACTIVITIES
    //   (CalendarActivity y AlertActivity)
    // -------------------------------------------------------------

    /** Indica si existe al menos un recordatorio (no completado) en esa fecha. */
    public static boolean tieneRecordatorio(Context context, int year, int month, int day) {
        List<Recordatorio> lista = cargarListaDesdePrefs(context);
        Calendar c = Calendar.getInstance();

        for (Recordatorio r : lista) {
            if (r.completado) continue;

            c.setTimeInMillis(r.fechaHoraMillis);
            if (c.get(Calendar.YEAR) == year
                    && c.get(Calendar.MONTH) == month
                    && c.get(Calendar.DAY_OF_MONTH) == day) {
                return true;
            }
        }
        return false;
    }

    /** Marca un recordatorio como completado (se usará al pulsar "Detener" en la alerta). */
    public static void marcarRecordatorioCompletado(Context context, long id) {
        List<Recordatorio> lista = cargarListaDesdePrefs(context);
        boolean cambio = false;

        for (Recordatorio r : lista) {
            if (r.id == id) {
                r.completado = true;
                cambio = true;
                break;
            }
        }

        if (cambio) {
            guardarListaEnPrefs(context, lista);
        }
    }

    /** Posponer 5 minutos (se usa al pulsar "+5 minutos"). */
    public static void posponer5Minutos(Context context, long id) {
        List<Recordatorio> lista = cargarListaDesdePrefs(context);
        boolean cambio = false;
        long ahoraMas5 = System.currentTimeMillis() + 5 * 60 * 1000L;
        Recordatorio objetivo = null;

        for (Recordatorio r : lista) {
            if (r.id == id) {
                r.completado = false;
                r.fechaHoraMillis = ahoraMas5;
                objetivo = r;
                cambio = true;
                break;
            }
        }

        if (cambio) {
            guardarListaEnPrefs(context, lista);
            if (objetivo != null) {
                programarAlarma(context, objetivo);
            }
        }
    }

    /** Programa una alarma del sistema que abrirá AlertActivity a la hora indicada. */
    public static void programarAlarma(Context context, Recordatorio r) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, AlertActivity.class);
        intent.putExtra("ID_RECORDATORIO", r.id);
        intent.putExtra("TITULO_RECORDATORIO", r.titulo);

        PendingIntent pi = PendingIntent.getActivity(
                context,
                (int) r.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerAtMillis = r.fechaHoraMillis;

        try {
            // Android 6+ con exact while idle (puede requerir permiso en Android 14)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
            }
        } catch (SecurityException e) {
            // Si el sistema no deja usar alarmas exactas (Android 14 sin permiso),
            // usamos una alarma normal para evitar que la app se cierre.
            am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
        }
    }


    /** Vuelve a programar las alarmas de todos los recordatorios pendientes. */
    public static void reprogramarTodasLasAlarmas(Context context) {
        List<Recordatorio> lista = cargarListaDesdePrefs(context);
        long ahora = System.currentTimeMillis();

        for (Recordatorio r : lista) {
            if (!r.completado && r.fechaHoraMillis > ahora) {
                programarAlarma(context, r);
            }
        }
    }

    // -------------------------------------------------------------
    //   MODELO DE DATOS
    // -------------------------------------------------------------
    public static class Recordatorio {
        long id;
        String titulo;
        long fechaHoraMillis;
        boolean completado;

        public java.util.Date getDate() {
            return new java.util.Date(fechaHoraMillis);
        }
    }
}
