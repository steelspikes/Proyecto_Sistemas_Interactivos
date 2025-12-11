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
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;

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

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;


public class HomeActivity extends AppCompatActivity {

    // --- Preferencias para guardar recordatorios ---
    private static final String PREFS_RECORDATORIOS = "prefs_recordatorios";
    private static final String KEY_LISTA           = "lista_recordatorios";

    // --- UI principal ---
    private Button btnAgregarRecordatorio;
    private TextView btnAddReminderFooter;
    private TextView btnCalendarFooter;
    private LinearLayout contenedorRecordatorios;
    private TextView txtSinRecordatorios;

    // Días de la semana
    private TextView txtDiaDomingo, txtDiaLunes, txtDiaMartes,
            txtDiaMiercoles, txtDiaJueves, txtDiaViernes, txtDiaSabado;

    // Fecha seleccionada (por defecto hoy)
    private final Calendar fechaSeleccionada = Calendar.getInstance();

    // Lista en memoria
    private List<Recordatorio> listaRecordatorios = new ArrayList<>();

    // Formatos
    private final SimpleDateFormat formatoFecha =
            new SimpleDateFormat("EEE, d 'de' MMM", Locale.getDefault());
    private final SimpleDateFormat formatoHora =
            new SimpleDateFormat("h:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 🔔 Pedir permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }


        // Ir a calendario (icono 📅)
        TextView btnIrCalendario = findViewById(R.id.btnIrCalendario);
        btnIrCalendario.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, CalendarActivity.class);
            startActivity(i);
        });

        // Referencias UI
        btnAgregarRecordatorio   = findViewById(R.id.btnAgregarRecordatorio);
        btnAddReminderFooter     = findViewById(R.id.btnAddReminderFooter);
        btnCalendarFooter        = findViewById(R.id.btnCalendarFooter);
        contenedorRecordatorios  = findViewById(R.id.contenedorRecordatorios);
        txtSinRecordatorios      = findViewById(R.id.txtSinRecordatorios);

        txtDiaDomingo   = findViewById(R.id.txtDiaDomingo);
        txtDiaLunes     = findViewById(R.id.txtDiaLunes);
        txtDiaMartes    = findViewById(R.id.txtDiaMartes);
        txtDiaMiercoles = findViewById(R.id.txtDiaMiercoles);
        txtDiaJueves    = findViewById(R.id.txtDiaJueves);
        txtDiaViernes   = findViewById(R.id.txtDiaViernes);
        txtDiaSabado    = findViewById(R.id.txtDiaSabado);

        // Cargar recordatorios desde preferencias
        listaRecordatorios = cargarRecordatorios(this);

        // Marcar el día actual en los círculos
        marcarDiaActual();

        // Click en cada día de la semana -> cambiar fecha seleccionada
        configurarClicksDiasSemana();

        // Botón agregar recordatorio
        btnAgregarRecordatorio.setOnClickListener(v -> mostrarDialogoNuevoRecordatorio());

        btnAddReminderFooter.setOnClickListener(v -> mostrarDialogoNuevoRecordatorio());

        btnCalendarFooter.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, CalendarActivity.class);
            startActivity(i);
        });

        // Pintar los recordatorios del día seleccionado (hoy)
        actualizarListaRecordatorios();
    }

    // ---------------------------------------------------------------------
    //  MODELO
    // ---------------------------------------------------------------------
    public static class Recordatorio {
        long id;                // identificador único
        String titulo;
        String descripcion;
        String emoji;
        long fechaHoraMillis;
        boolean completado;

        public Recordatorio(long id, String titulo, String descripcion, String emoji, long fechaHoraMillis, boolean completado) {
            this.id = id;
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.emoji = emoji;
            this.fechaHoraMillis = fechaHoraMillis;
            this.completado = completado;
        }
    }

    // ---------------------------------------------------------------------
    //  UTILIDADES DE PREFERENCIAS
    // ---------------------------------------------------------------------
    private static List<Recordatorio> cargarRecordatorios(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                PREFS_RECORDATORIOS, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_LISTA, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Recordatorio>>() {}.getType();
        List<Recordatorio> lista = gson.fromJson(json, type);
        return (lista != null) ? lista : new ArrayList<>();
    }

    private static void guardarRecordatorios(Context context, List<Recordatorio> lista) {
        SharedPreferences prefs = context.getSharedPreferences(
                PREFS_RECORDATORIOS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(lista);
        prefs.edit().putString(KEY_LISTA, json).apply();
    }

    // ---------------------------------------------------------------------
    //  DÍAS DE LA SEMANA
    // ---------------------------------------------------------------------
    private void marcarDiaActual() {
        TextView[] dias = new TextView[]{
                txtDiaDomingo, txtDiaLunes, txtDiaMartes, txtDiaMiercoles,
                txtDiaJueves, txtDiaViernes, txtDiaSabado
        };

        // Todo en verde
        for (TextView tv : dias) {
            tv.setBackgroundResource(R.drawable.circle_day);
        }

        Calendar hoy = Calendar.getInstance();
        int diaSemana = hoy.get(Calendar.DAY_OF_WEEK); // Domingo = 1 ... Sábado = 7
        int index = diaSemana - 1;
        if (index >= 0 && index < dias.length) {
            dias[index].setBackgroundResource(R.drawable.circle_day_hoy);
        }
    }

    private void configurarClicksDiasSemana() {
        txtDiaDomingo.setOnClickListener(v -> cambiarDiaSemana(Calendar.SUNDAY));
        txtDiaLunes.setOnClickListener(v -> cambiarDiaSemana(Calendar.MONDAY));
        txtDiaMartes.setOnClickListener(v -> cambiarDiaSemana(Calendar.TUESDAY));
        txtDiaMiercoles.setOnClickListener(v -> cambiarDiaSemana(Calendar.WEDNESDAY));
        txtDiaJueves.setOnClickListener(v -> cambiarDiaSemana(Calendar.THURSDAY));
        txtDiaViernes.setOnClickListener(v -> cambiarDiaSemana(Calendar.FRIDAY));
        txtDiaSabado.setOnClickListener(v -> cambiarDiaSemana(Calendar.SATURDAY));
    }

    /** Cambia la fecha seleccionada al día indicado de ESTA semana y refresca lista */
    private void cambiarDiaSemana(int diaSemana) {
        Calendar hoy = Calendar.getInstance();
        int dif = diaSemana - hoy.get(Calendar.DAY_OF_WEEK);
        fechaSeleccionada.setTimeInMillis(hoy.getTimeInMillis());
        fechaSeleccionada.add(Calendar.DAY_OF_MONTH, dif);

        // Actualiza los círculos (solo cambia colores, el de hoy sigue rosa)
        marcarDiaActual();

        // Refresca lista para ese día
        actualizarListaRecordatorios();
    }

    // ---------------------------------------------------------------------
    //  DIÁLOGO "NUEVO RECORDATORIO"
    // ---------------------------------------------------------------------
    private void mostrarDialogoNuevoRecordatorio() {
        // Por defecto: ahora
        Calendar fechaHoraSeleccionada = Calendar.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuevo recordatorio");

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_nuevo_recordatorio, null);
        builder.setView(view);

        EditText edtEmoji = view.findViewById(R.id.edtEmoji);
        EditText edtTitulo = view.findViewById(R.id.edtTituloRecordatorio);
        EditText edtDescripcion = view.findViewById(R.id.edtDescripcionRecordatorio);
        TextView txtFechaSel = view.findViewById(R.id.txtFechaSeleccionada);
        TextView txtHoraSel = view.findViewById(R.id.txtHoraSeleccionada);
        Button btnElegirFecha = view.findViewById(R.id.btnElegirFecha);
        Button btnElegirHora = view.findViewById(R.id.btnElegirHora);

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
        builder.setPositiveButton("Guardar", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            Button btnPositivo = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositivo.setOnClickListener(v -> {
                String emoji = edtEmoji.getText().toString().trim();
                String titulo = edtTitulo.getText().toString().trim();
                String descripcion = edtDescripcion.getText().toString().trim();

                if (titulo.isEmpty()) {
                    edtTitulo.setError("Escribe el nombre de la actividad");
                    return;
                }

                // Crear recordatorio
                long id = System.currentTimeMillis(); // id simple
                Recordatorio r = new Recordatorio(
                        id,
                        titulo,
                        descripcion,
                        emoji,
                        fechaHoraSeleccionada.getTimeInMillis(),
                        false
                );

                // Añadir a la lista, guardar y ordenar
                listaRecordatorios.add(r);
                ordenarListaPorHora(listaRecordatorios);
                guardarRecordatorios(HomeActivity.this, listaRecordatorios);

                // Programar alarma (protegido con try/catch para evitar crasheos)
                try {
                    programarAlarma(HomeActivity.this, r);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Refrescar lista para la fecha actualmente seleccionada
                actualizarListaRecordatorios();

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void ordenarListaPorHora(List<Recordatorio> lista) {
        Collections.sort(lista, new Comparator<Recordatorio>() {
            @Override
            public int compare(Recordatorio r1, Recordatorio r2) {
                return Long.compare(r1.fechaHoraMillis, r2.fechaHoraMillis);
            }
        });
    }

    // ---------------------------------------------------------------------
    //  LISTA DINÁMICA DE RECORDATORIOS PARA LA FECHA SELECCIONADA
    // ---------------------------------------------------------------------
    private void actualizarListaRecordatorios() {
        // Siempre recargamos de preferencias por si cambió fuera
        listaRecordatorios = cargarRecordatorios(this);
        ordenarListaPorHora(listaRecordatorios);

        contenedorRecordatorios.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        boolean hayRecordatorios = false;

        for (Recordatorio r : listaRecordatorios) {
            if (esMismoDia(fechaSeleccionada.getTimeInMillis(), r.fechaHoraMillis)) {
                hayRecordatorios = true;

                View card = inflater.inflate(R.layout.item_recordatorio, contenedorRecordatorios, false);

                TextView txtEmoji = card.findViewById(R.id.txtEmoji);
                TextView txtTitulo = card.findViewById(R.id.txtTituloRecordatorio);
                TextView txtDescripcion = card.findViewById(R.id.txtDescripcionRecordatorio);
                TextView txtHora   = card.findViewById(R.id.txtHoraRecordatorio);

                if (r.emoji != null && !r.emoji.isEmpty()) {
                    txtEmoji.setText(r.emoji);
                    txtEmoji.setVisibility(View.VISIBLE);
                } else {
                    txtEmoji.setVisibility(View.GONE);
                }

                txtTitulo.setText(r.titulo);
                txtHora.setText(formatoHora.format(r.fechaHoraMillis));

                if (r.descripcion != null && !r.descripcion.isEmpty()) {
                    txtDescripcion.setText(r.descripcion);
                    txtDescripcion.setVisibility(View.VISIBLE);
                } else {
                    txtDescripcion.setVisibility(View.GONE);
                }

                // Si está completado, se "apaga" un poco
                if (r.completado) {
                    card.setAlpha(0.4f);
                } else {
                    card.setAlpha(1.0f);
                }

                contenedorRecordatorios.addView(card);
            }
        }

        txtSinRecordatorios.setVisibility(hayRecordatorios ? View.GONE : View.VISIBLE);
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
    //  MÉTODOS ESTÁTICOS USADOS POR OTRAS CLASES
    // ---------------------------------------------------------------------

    /** Lo usa CalendarActivity para pintar días en rosa */
    public static boolean tieneRecordatorio(Context context,
                                            int year, int month, int day) {
        List<Recordatorio> lista = cargarRecordatorios(context);
        Calendar c = Calendar.getInstance();
        for (Recordatorio r : lista) {
            c.setTimeInMillis(r.fechaHoraMillis);
            if (c.get(Calendar.YEAR) == year &&
                    c.get(Calendar.MONTH) == month &&
                    c.get(Calendar.DAY_OF_MONTH) == day &&
                    !r.completado) {
                return true;
            }
        }
        return false;
    }

    public static void marcarRecordatorioCompletado(Context context, long id) {
        List<Recordatorio> lista = cargarRecordatorios(context);
        for (Recordatorio r : lista) {
            if (r.id == id) {
                r.completado = true;
                break;
            }
        }
        guardarRecordatorios(context, lista);
    }

    public static void posponer5Minutos(Context context, long id) {
        List<Recordatorio> lista = cargarRecordatorios(context);
        Recordatorio target = null;
        for (Recordatorio r : lista) {
            if (r.id == id) {
                target = r;
                break;
            }
        }
        if (target == null) return;

        // Sumar 5 minutos
        target.fechaHoraMillis += 5 * 60 * 1000;
        target.completado = false;
        guardarRecordatorios(context, lista);

        // Reprogramar la alarma
        programarAlarma(context, target);
    }

    public static void programarAlarma(Context context, Recordatorio r) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        long now = System.currentTimeMillis();
        long triggerAtMillis = r.fechaHoraMillis;

        // Si quedó en el pasado, la movemos 10 segundos al futuro para pruebas
        if (triggerAtMillis <= now) {
            triggerAtMillis = now + 10_000;
        }

        // Toast de depuración
        java.text.DateFormat df = java.text.DateFormat.getDateTimeInstance();
        String textoHora = df.format(new java.util.Date(triggerAtMillis));
        android.widget.Toast.makeText(
                context,
                "Programando alarma para: " + textoHora,
                android.widget.Toast.LENGTH_LONG
        ).show();

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlertActivity.EXTRA_TITULO, r.titulo);
        intent.putExtra(AlertActivity.EXTRA_DESCRIPCION, r.descripcion);
        intent.putExtra(AlertActivity.EXTRA_EMOJI, r.emoji);
        intent.putExtra(AlertActivity.EXTRA_ID, r.id);

        int requestCode = (int) (r.id % Integer.MAX_VALUE);

        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ pide permiso para alarmas exactas
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
                } else {
                    // No tenemos permiso de exactas → usamos una normal
                    am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
            }
        } catch (SecurityException e) {
            // Por si el sistema bloquea las alarmas exactas
            e.printStackTrace();
            am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
        }
    }


}
