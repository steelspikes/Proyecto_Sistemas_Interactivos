package com.example.proyecto_sistemas_interactivos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_sistemas_interactivos.HomeActivity.Recordatorio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TutorActivity extends AppCompatActivity {

    private TextView profileIcon;

    // --- Vistas de Dependientes ---
    private RecyclerView dependentsRecyclerView;
    private DependentAdapter dependentAdapter;
    private List<Dependent> dependentList;
    private Button btnAddDependent;

    // --- Vistas de Calendario ---
    private Button btnEne, btnFeb, btnMar, btnAbr, btnMay, btnJun,
            btnJul, btnAgo, btnSep, btnOct, btnNov, btnDic;
    private TextView txtTituloMes;
    private GridLayout gridDias;

    // --- Vistas de Recordatorios ---
    private RecyclerView remindersRecyclerView;
    private ReminderAdapter reminderAdapter;
    private List<Recordatorio> reminderList;
    private TextView remindersTitle;


    // --- Estado del Calendario ---
    private int anioActual;
    private int mesSeleccionado; // 0 = Ene ... 11 = Dic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);

        profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(this::showProfileMenu);

        // --- Configuración de Dependientes ---
        setupDependents();

        // --- Configuración del Calendario ---
        setupCalendar();

        // --- Configuración de Recordatorios ---
        setupReminders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refrescar el calendario y los recordatorios
        dibujarCalendario();
        Calendar hoy = Calendar.getInstance();
        showRemindersForDay(hoy.get(Calendar.DAY_OF_MONTH));
    }

    private void showProfileMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.menu_profile, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                // Redirigir a MainActivity
                Intent intent = new Intent(TutorActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void setupDependents() {
        dependentsRecyclerView = findViewById(R.id.dependentsRecyclerView);
        btnAddDependent = findViewById(R.id.btnAddDependent);

        dependentList = new ArrayList<>();
        dependentList.add(new Dependent("Juan Pérez", R.drawable.maestra));

        dependentAdapter = new DependentAdapter(dependentList, dependent -> {
            Intent intent = new Intent(TutorActivity.this, HomeActivity.class);
            startActivity(intent);
        });
        dependentsRecyclerView.setAdapter(dependentAdapter);

        btnAddDependent.setOnClickListener(v -> {
            Intent intent = new Intent(TutorActivity.this, AddDependentActivity.class);
            startActivity(intent);
        });
    }

    private void setupCalendar() {
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

    private void setupReminders(){
        remindersTitle = findViewById(R.id.remindersTitle);
        remindersRecyclerView = findViewById(R.id.remindersRecyclerView);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(reminderList);
        remindersRecyclerView.setAdapter(reminderAdapter);
    }

    // ---------------------------------------------------------------------
    //  LÓGICA DEL CALENDARIO
    // ---------------------------------------------------------------------

    private void configurarListenersMeses() {
        View.OnClickListener listener = v -> {
            if (v.getId() == R.id.btnEne) mesSeleccionado = 0;
            else if (v.getId() == R.id.btnFeb) mesSeleccionado = 1;
            else if (v.getId() == R.id.btnMar) mesSeleccionado = 2;
            else if (v.getId() == R.id.btnAbr) mesSeleccionado = 3;
            else if (v.getId() == R.id.btnMay) mesSeleccionado = 4;
            else if (v.getId() == R.id.btnJun) mesSeleccionado = 5;
            else if (v.getId() == R.id.btnJul) mesSeleccionado = 6;
            else if (v.getId() == R.id.btnAgo) mesSeleccionado = 7;
            else if (v.getId() == R.id.btnSep) mesSeleccionado = 8;
            else if (v.getId() == R.id.btnOct) mesSeleccionado = 9;
            else if (v.getId() == R.id.btnNov) mesSeleccionado = 10;
            else if (v.getId() == R.id.btnDic) mesSeleccionado = 11;

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
        resetearMeses();
        Button[] monthButtons = {btnEne, btnFeb, btnMar, btnAbr, btnMay, btnJun, btnJul, btnAgo, btnSep, btnOct, btnNov, btnDic};
        if (mes >= 0 && mes < monthButtons.length) {
            // El circulo de seleccion no esta implementado
            //monthButtons[mes].setBackgroundResource(R.drawable.circle_day_selected);
        }

        String[] nombresMes = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        txtTituloMes.setText(String.format(Locale.getDefault(), "%s %d", nombresMes[mes], anioActual));

        dibujarCalendario();
    }

    private void resetearMeses() {
        Button[] monthButtons = {btnEne, btnFeb, btnMar, btnAbr, btnMay, btnJun, btnJul, btnAgo, btnSep, btnOct, btnNov, btnDic};
        for (Button btn : monthButtons) {
            //btn.setBackgroundResource(R.drawable.circle_day);
        }
    }

    private void dibujarCalendario() {
        gridDias.removeAllViews();
        gridDias.setColumnCount(7);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, anioActual);
        cal.set(Calendar.MONTH, mesSeleccionado);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int primerDiaSemana = cal.get(Calendar.DAY_OF_WEEK);
        int offset = primerDiaSemana - Calendar.SUNDAY;
        int diasEnMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add day labels
        String[] dayLabels = {"D", "L", "M", "M", "J", "V", "S"};
        for (String label : dayLabels) {
            TextView tv = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            tv.setLayoutParams(params);
            tv.setGravity(android.view.Gravity.CENTER);
            tv.setText(label);
            tv.setTextSize(14f);
            gridDias.addView(tv);
        }

        for (int i = 0; i < 42; i++) {
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
                boolean tieneRec = HomeActivity.tieneRecordatorio(TutorActivity.this, anioActual, mesSeleccionado, diaNumero);
                if (tieneRec) {
                    tv.setBackgroundResource(R.drawable.cell_day_active);
                } else {
                    tv.setBackgroundResource(R.drawable.cell_day_inactive);
                }
                final int finalDia = diaNumero;
                tv.setOnClickListener(v -> showRemindersForDay(finalDia));
            } else {
                tv.setText("");
                tv.setBackgroundResource(R.drawable.cell_day_inactive);
                tv.setOnClickListener(null);
            }
            gridDias.addView(tv);
        }
    }

    private void showRemindersForDay(int day) {
        remindersTitle.setVisibility(View.VISIBLE);
        remindersRecyclerView.setVisibility(View.VISIBLE);

        List<Recordatorio> dailyReminders = HomeActivity.getRemindersForDate(this, anioActual, mesSeleccionado, day);

        reminderList.clear();
        reminderList.addAll(dailyReminders);
        reminderAdapter.notifyDataSetChanged();
    }

    // ---------------------------------------------------------------------
    //  MODELO Y ADAPTADOR DE DEPENDIENTES
    // ---------------------------------------------------------------------

    public static class Dependent {
        private String name;
        private int imageResId;

        public Dependent(String name, int imageResId) {
            this.name = name;
            this.imageResId = imageResId;
        }

        public String getName() { return name; }
        public int getImageResId() { return imageResId; }
    }

    public static class DependentAdapter extends RecyclerView.Adapter<DependentAdapter.ViewHolder> {
        private List<Dependent> dependents;
        private OnDependentClickListener listener;

        public interface OnDependentClickListener {
            void onDependentClick(Dependent dependent);
        }

        public DependentAdapter(List<Dependent> dependents, OnDependentClickListener listener) {
            this.dependents = dependents;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dependent, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Dependent dependent = dependents.get(position);
            holder.dependentNameTextView.setText(dependent.getName());
            holder.dependentImageView.setImageResource(dependent.getImageResId());
            holder.itemView.setOnClickListener(v -> listener.onDependentClick(dependent));
        }

        @Override
        public int getItemCount() {
            return dependents.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView dependentNameTextView;
            ImageView dependentImageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                dependentNameTextView = itemView.findViewById(R.id.dependentNameTextView);
                dependentImageView = itemView.findViewById(R.id.dependentImageView);
            }
        }
    }


    // ---------------------------------------------------------------------
    //  MODELO Y ADAPTADOR DE RECORDATORIOS
    // ---------------------------------------------------------------------

    public static class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
        private List<Recordatorio> reminders;
        private final SimpleDateFormat formatHora = new SimpleDateFormat("h:mm a", Locale.getDefault());

        public ReminderAdapter(List<Recordatorio> reminders) {
            this.reminders = reminders;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Recordatorio reminder = reminders.get(position);
            holder.reminderTitle.setText(reminder.titulo);
            holder.reminderTime.setText(formatHora.format(reminder.fechaHoraMillis));

            if (reminder.emoji != null && !reminder.emoji.isEmpty()) {
                holder.reminderEmoji.setText(reminder.emoji);
                holder.reminderEmoji.setVisibility(View.VISIBLE);
            } else {
                holder.reminderEmoji.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return reminders.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView reminderTitle;
            TextView reminderTime;
            TextView reminderEmoji;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                reminderTitle = itemView.findViewById(R.id.reminderTitle);
                reminderTime = itemView.findViewById(R.id.reminderTime);
                reminderEmoji = itemView.findViewById(R.id.reminderEmoji);
            }
        }
    }
}
