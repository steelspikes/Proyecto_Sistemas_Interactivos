package com.example.proyecto_sistemas_interactivos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddReminderActivity extends AppCompatActivity {

    private EditText edtTitulo;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        edtTitulo = findViewById(R.id.edtTituloRecordatorio);
        datePicker = findViewById(R.id.datePickerRecordatorio);
        timePicker = findViewById(R.id.timePickerRecordatorio);
        btnGuardar = findViewById(R.id.btnGuardarRecordatorio);

        timePicker.setIs24HourView(true); // si quieres formato 24h

        btnGuardar.setOnClickListener(v -> guardarRecordatorio());
    }

    private void guardarRecordatorio() {
        String titulo = edtTitulo.getText().toString().trim();

        if (titulo.isEmpty()) {
            edtTitulo.setError("Escribe el nombre de la actividad");
            return;
        }

        int dia = datePicker.getDayOfMonth();
        int mes = datePicker.getMonth() + 1; // enero = 0
        int anio = datePicker.getYear();

        int hora, minuto;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hora = timePicker.getHour();
            minuto = timePicker.getMinute();
        } else {
            hora = timePicker.getCurrentHour();
            minuto = timePicker.getCurrentMinute();
        }

        String mensaje = "Recordatorio guardado:\n"
                + titulo + "\n"
                + "Fecha: " + dia + "/" + mes + "/" + anio + "\n"
                + "Hora: " + String.format("%02d:%02d", hora, minuto);

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();

        // Aquí podrías guardar en una BD o SharedPreferences.
        // Por ahora solo regresamos a la pantalla principal:
        finish();
    }
}
